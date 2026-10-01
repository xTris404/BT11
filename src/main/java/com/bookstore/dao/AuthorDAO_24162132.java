package com.bookstore.dao;

import com.bookstore.model.Author_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO_24162132 {
    public List<Author_24162132> findAll() {
        return query("SELECT author_id, author_name, date_of_birth FROM author ORDER BY author_name");
    }
    /** Chỉ tác giả có ít nhất 1 sách: tác giả rỗng không được tạo khối trống trên trang home. */
    public List<Author_24162132> findAllWithBooks() {
        return query("SELECT a.author_id, a.author_name, a.date_of_birth FROM author a "
                   + "WHERE EXISTS (SELECT 1 FROM book_author ba WHERE ba.author_id = a.author_id) ORDER BY a.author_name");
    }
    public List<Integer> findIdsByBook(int bookId) {
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT author_id FROM book_author WHERE bookid = ?")) {
            ps.setInt(1, bookId);
            List<Integer> ids = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) ids.add(rs.getInt(1)); }
            return ids;
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }
    private List<Author_24162132> query(String sql) {
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Author_24162132> list = new ArrayList<>();
            while (rs.next()) {
                Author_24162132 a = new Author_24162132();
                a.setAuthorId(rs.getInt("author_id"));
                a.setAuthorName(rs.getString("author_name"));
                a.setDateOfBirth(rs.getDate("date_of_birth"));
                list.add(a);
            }
            return list;
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }
}
