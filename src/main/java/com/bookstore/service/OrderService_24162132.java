package com.bookstore.service;

import com.bookstore.dao.CheckoutConflictException_24162132;
import com.bookstore.dao.OrderDAO_24162132;
import com.bookstore.model.Order_24162132;
import com.bookstore.model.User_24162132;
import java.util.regex.Pattern;

public class OrderService_24162132 {
    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$"); // cùng quy tắc với đăng ký
    private final OrderDAO_24162132 orders = new OrderDAO_24162132();

    /** Đặt hàng COD từ giỏ hàng của user. Chỉ nhận THÔNG TIN GIAO HÀNG; sách/số lượng/giá lấy từ DB trong DAO.
     *  @return order_id */
    public int checkout(User_24162132 user, String receiver, String phone, String address, String note) {
        receiver = trim(receiver);
        phone = trim(phone);
        address = trim(address);
        note = trim(note);
        if (receiver.isEmpty() || receiver.length() > 100) throw new BusinessException_24162132("Họ tên người nhận bắt buộc, tối đa 100 ký tự.");
        if (!PHONE.matcher(phone).matches()) throw new BusinessException_24162132("Số điện thoại gồm 10 chữ số, bắt đầu bằng 0.");
        if (address.isEmpty() || address.length() > 255) throw new BusinessException_24162132("Địa chỉ giao hàng bắt buộc, tối đa 255 ký tự.");
        if (note.length() > 255) throw new BusinessException_24162132("Ghi chú tối đa 255 ký tự.");
        try {
            return orders.place(user.getId(), receiver, phone, address, note.isEmpty() ? null : note);
        } catch (CheckoutConflictException_24162132 e) {
            throw new BusinessException_24162132(e.getMessage());
        }
    }

    /** null nếu đơn không tồn tại HOẶC không thuộc user này (không phân biệt, để không lộ sự tồn tại của đơn người khác). */
    public Order_24162132 get(int userId, int orderId) { return orders.findByIdForUser(orderId, userId); }

    /** users.phone là INT nên mất số 0 đầu (0912345678 -> 912345678): điền sẵn lại vào form cho đủ 10 số. */
    public String defaultPhone(User_24162132 u) {
        if (u.getPhone() == null) return "";
        String s = String.valueOf(u.getPhone());
        return s.length() == 9 ? "0" + s : s;
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
