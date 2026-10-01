package com.bookstore.service;

import com.bookstore.dao.BookDAO_24162132;
import com.bookstore.dao.ReviewDAO_24162132;
import com.bookstore.model.Review_24162132;
import com.bookstore.model.User_24162132;
import java.util.List;

public class ReviewService_24162132 {
    private final ReviewDAO_24162132 reviews = new ReviewDAO_24162132();
    private final BookDAO_24162132 books = new BookDAO_24162132();

    public List<Review_24162132> list(int bookId) { return reviews.findByBook(bookId); }

    public void submit(User_24162132 user, int bookId, int rating, String text) {
        if (rating < 1 || rating > 5) throw new BusinessException_24162132("Điểm đánh giá phải từ 1 đến 5.");
        text = text == null ? "" : text.trim();
        if (text.length() > 2000) throw new BusinessException_24162132("Nội dung đánh giá tối đa 2000 ký tự.");
        if (books.findById(bookId) == null) throw new BusinessException_24162132("Sách không tồn tại.");
        reviews.upsert(user.getId(), bookId, rating, text);
    }
}
