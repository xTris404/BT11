package com.bookstore.model;

import java.io.Serializable;

/** Đăng ký đang chờ xác thực OTP. Sống trong HttpSession, CHƯA ghi vào bảng users. */
public class PendingRegistration_24162132 implements Serializable {
    private final String email, fullname, passwordHash;
    private final Integer phone;
    private String otp;
    private long expiresAt, lastSentAt;
    private int attempts;
    private final int maxAttempts;

    public PendingRegistration_24162132(String email, String fullname, Integer phone, String passwordHash, int maxAttempts) {
        this.email = email; this.fullname = fullname; this.phone = phone;
        this.passwordHash = passwordHash; this.maxAttempts = maxAttempts;
    }
    public void issueOtp(String otp, long ttlMillis) {
        long now = System.currentTimeMillis();
        this.otp = otp; this.lastSentAt = now; this.expiresAt = now + ttlMillis;
    }
    public boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
    public boolean isLocked() { return attempts >= maxAttempts; }
    public void incAttempts() { attempts++; }
    public long millisSinceLastSent() { return System.currentTimeMillis() - lastSentAt; }
    public String getOtp() { return otp; }
    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
    public Integer getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
}
