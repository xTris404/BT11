package com.bookstore.controller;

import com.bookstore.model.User_24162132;
import com.bookstore.service.BusinessException_24162132;
import com.bookstore.service.CartService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Giỏ hàng: GET /cart xem; POST /cart?action=add|update|remove|clear. Đăng nhập + quyền User do UserFilter chặn trước. */
@WebServlet("/cart")
public class CartServlet_24162132 extends HttpServlet {
    private final CartService_24162132 carts = new CartService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24162132 u = WebUtil_24162132.currentUser(req); // userId luôn lấy từ SESSION, không bao giờ từ form/URL
        req.setAttribute("cart", carts.view(u.getId()));
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        User_24162132 u = WebUtil_24162132.currentUser(req);
        String action = req.getParameter("action");
        int bookId = WebUtil_24162132.toInt(req.getParameter("bookId"), -1);
        int qty = WebUtil_24162132.toInt(req.getParameter("quantity"), -1); // không phải số -> -1 -> bị service từ chối
        String back = ctx + "/cart";
        try {
            if ("add".equals(action)) {
                back = bookId > 0 ? ctx + "/book?id=" + bookId : ctx + "/home"; // lỗi thì quay về trang sách để người dùng chỉnh lại
                carts.add(u.getId(), bookId, qty);
                WebUtil_24162132.flash(req, "Đã thêm sách vào giỏ hàng.");
                back = ctx + "/cart";
            } else if ("update".equals(action)) {
                carts.setQuantity(u.getId(), bookId, qty);
                WebUtil_24162132.flash(req, "Đã cập nhật số lượng.");
            } else if ("remove".equals(action)) {
                carts.remove(u.getId(), bookId);
                WebUtil_24162132.flash(req, "Đã xóa sách khỏi giỏ hàng.");
            } else if ("clear".equals(action)) {
                carts.clear(u.getId());
                WebUtil_24162132.flash(req, "Đã xóa toàn bộ giỏ hàng.");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ.");
                return;
            }
        } catch (BusinessException_24162132 e) {
            WebUtil_24162132.flashError(req, e.getMessage());
        }
        resp.sendRedirect(back); // Post-Redirect-Get: F5 không gửi lại thao tác
    }
}
