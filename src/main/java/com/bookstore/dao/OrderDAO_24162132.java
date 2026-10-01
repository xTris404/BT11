package com.bookstore.dao;

import com.bookstore.model.Order_24162132;
import com.bookstore.model.OrderItem_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO_24162132 {
    private static final BigDecimal MAX_TOTAL = new BigDecimal("99999999.99"); // orders.total decimal(10,2)

    private static final class Line {
        int bookId, qty;
        String title;
        BigDecimal price;
        Integer stock;
    }

    /**
     * Đặt hàng COD trong MỘT transaction: khóa giỏ + sách -> kiểm tra tồn kho -> ghi đơn -> trừ kho -> xóa giỏ.
     * Số lượng và giá lấy từ DB (giỏ + bảng books), KHÔNG nhận từ client: sửa form/giá ở trình duyệt vô tác dụng.
     * @return order_id mới.
     * @throws CheckoutConflictException_24162132 nếu giỏ rỗng / hết hàng / thiếu giá (đã rollback, không trừ kho).
     */
    public int place(int userId, String receiver, String phone, String address, String note) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            c.setAutoCommit(false);
            try {
                List<Line> lines = lockCart(c, userId);
                if (lines.isEmpty())
                    throw new CheckoutConflictException_24162132("Giỏ hàng đang trống (có thể đơn này đã được đặt ở lần bấm trước).");
                BigDecimal total = BigDecimal.ZERO;
                for (Line l : lines) {
                    if (l.price == null)
                        throw new CheckoutConflictException_24162132("«" + l.title + "» hiện chưa có giá bán. Hãy xóa khỏi giỏ để tiếp tục.");
                    int stock = l.stock == null ? 0 : l.stock;
                    if (stock < l.qty)
                        throw new CheckoutConflictException_24162132("«" + l.title + "» chỉ còn " + stock + " cuốn trong kho (bạn đang chọn " + l.qty + "). Hãy chỉnh lại số lượng trong giỏ.");
                    total = total.add(l.price.multiply(BigDecimal.valueOf(l.qty)));
                }
                if (total.compareTo(MAX_TOTAL) > 0)
                    throw new CheckoutConflictException_24162132("Giá trị đơn hàng quá lớn, vui lòng giảm số lượng.");

                int orderId = insertOrder(c, userId, receiver, phone, address, note, total);
                insertItems(c, orderId, lines);
                decrementStock(c, lines);
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM cart_item WHERE userid = ?")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                c.commit();
                return orderId;
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** Chỉ trả đơn nếu thuộc về userId: đổi ?id= sang đơn người khác chỉ nhận về null (404), không lộ đơn của họ. */
    public Order_24162132 findByIdForUser(int orderId, int userId) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            Order_24162132 o;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT order_id, userid, receiver_name, phone, address, note, payment_method, status, total, created_at FROM orders WHERE order_id = ? AND userid = ?")) {
                ps.setInt(1, orderId);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    o = new Order_24162132();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("userid"));
                    o.setReceiverName(rs.getString("receiver_name"));
                    o.setPhone(rs.getString("phone"));
                    o.setAddress(rs.getString("address"));
                    o.setNote(rs.getString("note"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setStatus(rs.getString("status"));
                    o.setTotal(rs.getBigDecimal("total"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }
            List<OrderItem_24162132> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT bookid, title, unit_price, quantity FROM order_item WHERE order_id = ? ORDER BY item_id")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        OrderItem_24162132 i = new OrderItem_24162132();
                        i.setBookId((Integer) rs.getObject("bookid"));
                        i.setTitle(rs.getString("title"));
                        i.setUnitPrice(rs.getBigDecimal("unit_price"));
                        i.setQuantity(rs.getInt("quantity"));
                        items.add(i);
                    }
                }
            }
            o.setItems(items);
            return o;
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    // ---- helpers ----
    /** FOR UPDATE khóa các dòng cart_item + books liên quan tới khi commit/rollback.
     *  ORDER BY bookid: mọi giao dịch khóa theo CÙNG một thứ tự => hai người mua chung 2 cuốn không khóa chéo (deadlock). */
    private static List<Line> lockCart(Connection c, int userId) throws SQLException {
        String sql = "SELECT ci.bookid, ci.quantity AS qty, b.title, b.price, b.quantity AS stock "
                   + "FROM cart_item ci JOIN books b ON b.bookid = ci.bookid WHERE ci.userid = ? ORDER BY ci.bookid FOR UPDATE";
        List<Line> lines = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Line l = new Line();
                    l.bookId = rs.getInt("bookid");
                    l.qty = rs.getInt("qty");
                    String t = rs.getString("title");
                    l.title = (t == null || t.isBlank()) ? "Sách #" + l.bookId : t;
                    l.price = rs.getBigDecimal("price");
                    l.stock = (Integer) rs.getObject("stock");
                    lines.add(l);
                }
            }
        }
        return lines;
    }

    private static int insertOrder(Connection c, int userId, String receiver, String phone, String address, String note, BigDecimal total) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO orders(userid, receiver_name, phone, address, note, payment_method, status, total) VALUES (?,?,?,?,?,'COD','PENDING',?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, receiver);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, note);
            ps.setBigDecimal(6, total);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    private static void insertItems(Connection c, int orderId, List<Line> lines) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO order_item(order_id, bookid, title, unit_price, quantity) VALUES (?,?,?,?,?)")) {
            for (Line l : lines) {
                ps.setInt(1, orderId);
                ps.setInt(2, l.bookId);
                ps.setString(3, l.title);
                ps.setBigDecimal(4, l.price);
                ps.setInt(5, l.qty);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Điều kiện quantity >= ? là lớp phòng thủ thứ hai: dù đã khóa dòng, kho không bao giờ âm. */
    private static void decrementStock(Connection c, List<Line> lines) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE books SET quantity = quantity - ? WHERE bookid = ? AND quantity >= ?")) {
            for (Line l : lines) {
                ps.setInt(1, l.qty);
                ps.setInt(2, l.bookId);
                ps.setInt(3, l.qty);
                if (ps.executeUpdate() != 1)
                    throw new CheckoutConflictException_24162132("«" + l.title + "» vừa hết hàng. Vui lòng kiểm tra lại giỏ.");
            }
        }
    }
}
