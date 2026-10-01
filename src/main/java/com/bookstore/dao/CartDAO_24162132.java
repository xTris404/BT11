package com.bookstore.dao;

import com.bookstore.model.Book_24162132;
import com.bookstore.model.CartItem_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO_24162132 {
    /** JOIN books để lấy giá + tồn kho HIỆN TẠI: giỏ chỉ lưu (user, sách, số lượng), không lưu bản sao giá. */
    public List<CartItem_24162132> findByUser(int userId) {
        String sql = "SELECT b.bookid, b.title, b.price, b.cover_image, b.quantity AS stock, ci.quantity AS qty "
                   + "FROM cart_item ci JOIN books b ON b.bookid = ci.bookid WHERE ci.userid = ? ORDER BY ci.added_at, b.bookid";
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            List<CartItem_24162132> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book_24162132 b = new Book_24162132();
                    b.setBookId(rs.getInt("bookid"));
                    b.setTitle(rs.getString("title"));
                    b.setPrice(rs.getBigDecimal("price"));
                    b.setCoverImage(rs.getString("cover_image"));
                    b.setQuantity((Integer) rs.getObject("stock"));
                    list.add(new CartItem_24162132(b, rs.getInt("qty")));
                }
            }
            return list;
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** @return số lượng đang có trong giỏ; 0 nếu chưa có. */
    public int findQuantity(int userId, int bookId) {
        try (Connection c = DBUtil_24162132.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT quantity FROM cart_item WHERE userid = ? AND bookid = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** Đặt số lượng TUYỆT ĐỐI (không cộng dồn trong SQL). PK (userid, bookid) => upsert nguyên tử, bấm 2 lần không lỗi trùng khóa. */
    public void setQuantity(int userId, int bookId, int quantity) {
        String sql = "INSERT INTO cart_item(userid, bookid, quantity) VALUES (?,?,?) ON DUPLICATE KEY UPDATE quantity = ?";
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            ps.setInt(4, quantity);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** Điều kiện userid nằm trong WHERE: không thể xóa dòng của người khác dù đoán đúng bookId. */
    public void remove(int userId, int bookId) {
        try (Connection c = DBUtil_24162132.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cart_item WHERE userid = ? AND bookid = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    public void clear(int userId) {
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM cart_item WHERE userid = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }
}
