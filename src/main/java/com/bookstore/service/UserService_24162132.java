package com.bookstore.service;

import com.bookstore.dao.UserDAO_24162132;
import com.bookstore.model.PendingRegistration_24162132;
import com.bookstore.model.User_24162132;
import com.bookstore.util.AppConfig_24162132;
import com.bookstore.util.MailUtil_24162132;
import com.bookstore.util.PasswordUtil_24162132;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

public class UserService_24162132 {
    private static final Logger LOG = Logger.getLogger(UserService_24162132.class.getName());
    private static final SecureRandom RNG = new SecureRandom();
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$");

    private final UserDAO_24162132 dao = new UserDAO_24162132();
    private final long ttlMs = AppConfig_24162132.getInt("otp.ttl.minutes", 5) * 60_000L;
    private final int maxAttempts = AppConfig_24162132.getInt("otp.max.attempts", 5);
    private final long cooldownMs = AppConfig_24162132.getInt("otp.resend.cooldown.seconds", 30) * 1000L;

    /** Bước 1: validate -> tạo OTP -> gửi mail. Chưa ghi DB. */
    public PendingRegistration_24162132 startRegistration(String email, String fullname, String phone, String password, String confirm) {
        email = email == null ? "" : email.trim().toLowerCase();
        fullname = fullname == null ? "" : fullname.trim();
        phone = phone == null ? "" : phone.trim();
        if (!EMAIL.matcher(email).matches() || email.length() > 50) throw new BusinessException_24162132("Email không hợp lệ (tối đa 50 ký tự).");
        if (fullname.isEmpty() || fullname.length() > 50) throw new BusinessException_24162132("Họ tên bắt buộc, tối đa 50 ký tự.");
        Integer phoneNum = null;
        if (!phone.isEmpty()) {
            if (!PHONE.matcher(phone).matches()) throw new BusinessException_24162132("Số điện thoại gồm 10 chữ số, bắt đầu bằng 0.");
            phoneNum = Integer.valueOf(phone); // cột phone kiểu int: số 0 đầu bị mất khi lưu
        }
        if (password == null || password.length() < 6) throw new BusinessException_24162132("Mật khẩu tối thiểu 6 ký tự.");
        if (!password.equals(confirm)) throw new BusinessException_24162132("Mật khẩu nhập lại không khớp.");
        if (dao.findByEmail(email) != null) throw new BusinessException_24162132("Email này đã được đăng ký.");

        PendingRegistration_24162132 p = new PendingRegistration_24162132(email, fullname, phoneNum, PasswordUtil_24162132.md5(password), maxAttempts);
        issueAndSend(p);
        return p;
    }

    public void resendOtp(PendingRegistration_24162132 p) {
        long wait = cooldownMs - p.millisSinceLastSent();
        if (wait > 0) throw new BusinessException_24162132("Vui lòng đợi " + (wait / 1000 + 1) + " giây trước khi gửi lại mã.");
        issueAndSend(p);
    }

    /** Bước 2: đúng OTP, còn hạn, chưa quá số lần thử -> mới ghi user vào DB. */
    public void completeRegistration(PendingRegistration_24162132 p, String otp) {
        if (p.isLocked()) throw new BusinessException_24162132("Nhập sai quá nhiều lần. Vui lòng đăng ký lại.");
        if (p.isExpired()) throw new BusinessException_24162132("Mã OTP đã hết hạn. Hãy bấm gửi lại mã.");
        if (otp == null || !PasswordUtil_24162132.equal(p.getOtp(), otp.trim())) {
            p.incAttempts();
            throw new BusinessException_24162132(p.isLocked() ? "Nhập sai quá nhiều lần. Vui lòng đăng ký lại." : "Mã OTP không đúng.");
        }
        if (dao.findByEmail(p.getEmail()) != null) throw new BusinessException_24162132("Email này đã được đăng ký.");
        User_24162132 u = new User_24162132();
        u.setEmail(p.getEmail());
        u.setFullname(p.getFullname());
        u.setPhone(p.getPhone());
        u.setPasswd(p.getPasswordHash());
        dao.insert(u);
    }

    /** identifier = giá trị cột users.email (tài khoản seed dùng 'admin', 'yennhi'; tài khoản đăng ký mới dùng email thật).
     *  @return user (đã xóa passwd) nếu đúng, null nếu sai. Không tiết lộ sai email hay sai mật khẩu. */
    public User_24162132 login(String identifier, String password) {
        if (identifier == null || password == null) return null;
        User_24162132 u = dao.findByEmail(identifier.trim().toLowerCase());
        if (u == null || !PasswordUtil_24162132.equal(u.getPasswd(), PasswordUtil_24162132.md5(password))) return null;
        dao.updateLastLogin(u.getId());
        u.setPasswd(null);
        return u;
    }

    private void issueAndSend(PendingRegistration_24162132 p) {
        p.issueOtp(String.format("%06d", RNG.nextInt(1_000_000)), ttlMs);
        try {
            MailUtil_24162132.send(p.getEmail(), "Mã xác thực đăng ký BookStore",
                "Mã OTP của bạn là: " + p.getOtp() + "\nMã có hiệu lực " + (ttlMs / 60_000) + " phút. Không chia sẻ mã này cho ai.");
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Gửi OTP thất bại", e);
            throw new BusinessException_24162132("Không gửi được email OTP. Kiểm tra cấu hình SMTP trong app.properties.");
        }
    }
}
