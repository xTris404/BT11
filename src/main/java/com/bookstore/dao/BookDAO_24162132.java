package com.bookstore.dao;

import com.bookstore.model.Book_24162132;
import com.bookstore.model.Page_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDAO_24162132 {
    /** authors gộp bằng GROUP_CONCAT, số review bằng subquery -> 1 round-trip thay vì N+1. */
    private static final String SELECT =
        "SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, b.publish_date, b.cover_image, b.quantity, "
      + "(SELECT GROUP_CONCAT(a.author_name ORDER BY a.author_name SEPARATOR ', ') FROM book_author ba JOIN author a ON a.author_id = ba.author_id WHERE ba.bookid = b.bookid) AS author_names, "
      + "(SELECT COUNT(*) FROM rating r WHERE r.bookid = b.bookid) AS review_count "
      + "FROM books b ";

    public Page_24162132<Book_24162132> findByAuthor(int authorId, int page, int size) {
        return paged("WHERE b.bookid IN (SELECT ba.bookid FROM book_author ba WHERE ba.author_id = ?) ", new Object[]{authorId}, page, size);
    }

    public Page_24162132<Book_24162132> findAll(int page, int size) {
        return paged("", new Object[0], page, size);
    }

    public Book_24162132 findById(int id) {
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE b.bookid = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    public int insert(Book_24162132 b, List<Integer> authorIds) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO books(isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES (?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    bind(ps, b);
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); id = k.getInt(1); }
                }
                link(c, id, authorIds);
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** @return số dòng books bị cập nhật (0 = id không tồn tại). */
    public int update(Book_24162132 b, List<Integer> authorIds) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            c.setAutoCommit(false);
            try {
                int n;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE books SET isbn=?, title=?, publisher=?, price=?, description=?, publish_date=?, cover_image=?, quantity=? WHERE bookid=?")) {
                    bind(ps, b);
                    ps.setInt(9, b.getBookId());
                    n = ps.executeUpdate();
                }
                if (n > 0) { execById(c, "DELETE FROM book_author WHERE bookid = ?", b.getBookId()); link(c, b.getBookId(), authorIds); }
                c.commit();
                return n;
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    /** Xóa con trước, cha sau, trong 1 transaction: lỗi giữa chừng thì rollback, không để dữ liệu mồ côi/dở dang. */
    public void delete(int id) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            c.setAutoCommit(false);
            try {
                execById(c, "DELETE FROM rating WHERE bookid = ?", id);
                execById(c, "DELETE FROM book_author WHERE bookid = ?", id);
                execById(c, "DELETE FROM books WHERE bookid = ?", id);
                c.commit();
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    // ---- helpers ----
    private Page_24162132<Book_24162132> paged(String where, Object[] args, int page, int size) {
        try (Connection c = DBUtil_24162132.getConnection()) {
            int total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM books b " + where)) {
                bindAll(ps, args);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getInt(1); }
            }
            int pg = Page_24162132.clamp(page, total, size);
            Object[] a = Arrays.copyOf(args, args.length + 2);
            a[args.length] = size;
            a[args.length + 1] = (pg - 1) * size;
            List<Book_24162132> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(SELECT + where + "ORDER BY b.bookid LIMIT ? OFFSET ?")) {
                bindAll(ps, a);
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
            }
            return new Page_24162132<>(items, pg, size, total);
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    private static void bindAll(PreparedStatement ps, Object[] a) throws SQLException {
        for (int i = 0; i < a.length; i++) ps.setObject(i + 1, a[i]);
    }

    private static void bind(PreparedStatement ps, Book_24162132 b) throws SQLException {
        DBUtil_24162132.setInt(ps, 1, b.getIsbn());
        ps.setString(2, b.getTitle());
        ps.setString(3, b.getPublisher());
        if (b.getPrice() == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, b.getPrice());
        ps.setString(5, b.getDescription());
        ps.setDate(6, b.getPublishDate());
        ps.setString(7, b.getCoverImage());
        DBUtil_24162132.setInt(ps, 8, b.getQuantity());
    }

    private static void link(Connection c, int bookId, List<Integer> authorIds) throws SQLException {
        if (authorIds == null || authorIds.isEmpty()) return;
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO book_author(bookid, author_id) VALUES (?, ?)")) {
            for (Integer a : authorIds) { ps.setInt(1, bookId); ps.setInt(2, a); ps.addBatch(); }
            ps.executeBatch();
        }
    }

    private static void execById(Connection c, String sql, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); }
    }

    private static Book_24162132 map(ResultSet rs) throws SQLException {
        Book_24162132 b = new Book_24162132();
        b.setBookId(rs.getInt("bookid"));
        b.setIsbn((Integer) rs.getObject("isbn"));
        b.setTitle(rs.getString("title"));
        b.setPublisher(rs.getString("publisher"));
        b.setPrice(rs.getBigDecimal("price"));
        b.setDescription(rs.getString("description"));
        b.setPublishDate(rs.getDate("publish_date"));
        b.setCoverImage(rs.getString("cover_image"));
        b.setQuantity((Integer) rs.getObject("quantity"));
        b.setAuthorNames(rs.getString("author_names"));
        b.setReviewCount(rs.getInt("review_count"));
        return b;
    }
}
