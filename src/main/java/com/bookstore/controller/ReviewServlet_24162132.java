package com.bookstore.controller;

import com.bookstore.model.User_24162132;
import com.bookstore.service.BusinessException_24162132;
import com.bookstore.service.ReviewService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/review")
public class ReviewServlet_24162132 extends HttpServlet {
    private final ReviewService_24162132 reviews = new ReviewService_24162132();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        User_24162132 u = WebUtil_24162132.currentUser(req);
        if (u == null) {
            WebUtil_24162132.flashError(req, "Vui lòng đăng nhập để đánh giá sách.");
            resp.sendRedirect(ctx + "/login");
            return;
        }
        int bookId = WebUtil_24162132.toInt(req.getParameter("bookId"), -1);
        try {
            // userId lấy từ SESSION, không tin trường form: tránh giả mạo đánh giá thay người khác
            reviews.submit(u, bookId, WebUtil_24162132.toInt(req.getParameter("rating"), 0), req.getParameter("reviewText"));
            WebUtil_24162132.flash(req, "Đã lưu đánh giá của bạn.");
        } catch (BusinessException_24162132 e) {
            WebUtil_24162132.flashError(req, e.getMessage());
        }
        resp.sendRedirect(ctx + "/book?id=" + bookId);
    }
}
