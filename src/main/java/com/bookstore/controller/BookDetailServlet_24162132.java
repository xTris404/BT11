package com.bookstore.controller;

import com.bookstore.model.Book_24162132;
import com.bookstore.service.BookService_24162132;
import com.bookstore.service.ReviewService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Câu 4: chi tiết sách + danh sách review + form thêm review. */
@WebServlet("/book")
public class BookDetailServlet_24162132 extends HttpServlet {
    private final BookService_24162132 books = new BookService_24162132();
    private final ReviewService_24162132 reviews = new ReviewService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = WebUtil_24162132.toInt(req.getParameter("id"), -1);
        Book_24162132 b = books.get(id);
        if (b == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sách."); return; }
        req.setAttribute("book", b);
        req.setAttribute("reviews", reviews.list(id));
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(req, resp);
    }
}
