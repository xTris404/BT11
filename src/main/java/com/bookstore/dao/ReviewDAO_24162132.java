package com.bookstore.dao;

import com.bookstore.model.Review_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO_24162132 {
    public List<Review_24162132> findByBook(int bookId) {
        try (Connection c = DBUtil_24162132.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT r.userid, r.bookid, r.rating, r.review_text, u.fullname FROM rating r JOIN users u ON u.id = r.userid WHERE r.bookid = ? ORDER BY u.fullname")) {
            ps.setInt(1, bookId);
            List<Review_24162132> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review_24162132 r = new Review_24162132();
                    r.setUserId(rs.getInt("userid"));
                    r.setBookId(rs.getInt("bookid"));
                    r.setRating(rs.getInt("rating"));
                    r.setReviewText(rs.getString("review_text"));
                    r.setFullname(rs.getString("fullname"));
                    list.add(r);
                }
            }
            return list;
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** PK (userid, bookid) => mỗi user 1 đánh giá/sách. INSERT ... ON DUPLICATE KEY UPDATE: update-hoặc-insert nguyên tử trong 1 câu lệnh, bấm Submit 2 lần không lỗi. */
    public void upsert(int userId, int bookId, int rating, String text) {
        String sql = "INSERT INTO rating(userid, bookid, rating, review_text) VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE rating = ?, review_text = ?";
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, bookId);
            ps.setInt(3, rating); ps.setString(4, text);
            ps.setInt(5, rating); ps.setString(6, text);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }
}
