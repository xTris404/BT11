package com.bookstore.service;

import com.bookstore.dao.AuthorDAO_24162132;
import com.bookstore.dao.BookDAO_24162132;
import com.bookstore.model.Author_24162132;
import com.bookstore.model.Book_24162132;
import com.bookstore.model.Page_24162132;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BookService_24162132 {
    public static final int HOME_PAGE_SIZE = 3;   // Câu 3: 03 sp/trang
    public static final int ADMIN_PAGE_SIZE = 5;  // Câu 6
    private static final Pattern COVER = Pattern.compile("^(?:[\\w.\\-]+|https?://[^\\s\"'<>]+)$");
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999.99"); // decimal(6,2)

    private final BookDAO_24162132 books = new BookDAO_24162132();
    private final AuthorDAO_24162132 authors = new AuthorDAO_24162132();

    public List<Author_24162132> authorsWithBooks() { return authors.findAllWithBooks(); }
    public List<Author_24162132> allAuthors() { return authors.findAll(); }
    public Page_24162132<Book_24162132> booksByAuthor(int authorId, int page) { return books.findByAuthor(authorId, page, HOME_PAGE_SIZE); }
    public Page_24162132<Book_24162132> list(int page) { return books.findAll(page, ADMIN_PAGE_SIZE); }

    public Book_24162132 get(int id) {
        Book_24162132 b = books.findById(id);
        if (b != null) b.setAuthorIds(authors.findIdsByBook(id));
        return b;
    }

    /** @return danh sách lỗi; rỗng nghĩa là đã lưu thành công. bookId == 0 => tạo mới, ngược lại => cập nhật. */
    public List<String> save(Book_24162132 b) {
        List<String> errors = validate(b);
        if (!errors.isEmpty()) return errors;
        if (b.getBookId() == 0) books.insert(b, b.getAuthorIds());
        else if (books.update(b, b.getAuthorIds()) == 0) errors.add("Sách không tồn tại (có thể đã bị xóa).");
        return errors;
    }

    public void delete(int id) { books.delete(id); }

    private List<String> validate(Book_24162132 b) {
        List<String> e = new ArrayList<>();
        if (b.getTitle() == null || b.getTitle().isBlank()) e.add("Tiêu đề không được để trống.");
        else if (b.getTitle().length() > 200) e.add("Tiêu đề tối đa 200 ký tự.");
        if (b.getPublisher() != null && b.getPublisher().length() > 100) e.add("Nhà xuất bản tối đa 100 ký tự.");
        if (b.getCoverImage() != null && (b.getCoverImage().length() > 100 || !COVER.matcher(b.getCoverImage()).matches()))
            e.add("Ảnh bìa: tên file (chữ, số, . _ -) hoặc URL http(s), tối đa 100 ký tự.");
        if (b.getPrice() != null && (b.getPrice().signum() < 0 || b.getPrice().compareTo(MAX_PRICE) > 0))
            e.add("Giá phải nằm trong khoảng 0 – 9999.99 (cột decimal(6,2)).");
        if (b.getQuantity() != null && b.getQuantity() < 0) e.add("Số lượng không được âm.");
        if (b.getAuthorIds() == null || b.getAuthorIds().isEmpty()) e.add("Chọn ít nhất 1 tác giả.");
        return e;
    }
}
