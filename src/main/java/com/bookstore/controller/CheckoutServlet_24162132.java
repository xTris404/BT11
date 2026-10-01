package com.bookstore.controller;

import com.bookstore.model.Cart_24162132;
import com.bookstore.model.User_24162132;
import com.bookstore.service.BusinessException_24162132;
import com.bookstore.service.CartService_24162132;
import com.bookstore.service.OrderService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Thanh toán COD: GET hiện form giao hàng + tóm tắt giỏ; POST đặt hàng. */
@WebServlet("/checkout")
public class CheckoutServlet_24162132 extends HttpServlet {
    private final CartService_24162132 carts = new CartService_24162132();
    private final OrderService_24162132 orders = new OrderService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24162132 u = WebUtil_24162132.currentUser(req);
        Cart_24162132 cart = carts.view(u.getId());
        if (!cart.isCheckoutable()) { // giỏ rỗng hoặc có dòng lỗi: về giỏ hàng để xử lý, không cho tới bước nhập địa chỉ
            WebUtil_24162132.flashError(req, cart.isEmpty() ? "Giỏ hàng trống." : "Giỏ hàng có sách không thể mua, vui lòng xử lý trước khi thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        req.setAttribute("cart", cart);
        req.setAttribute("receiverName", u.getFullname());
        req.setAttribute("phone", orders.defaultPhone(u));
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        User_24162132 u = WebUtil_24162132.currentUser(req);
        try {
            int orderId = orders.checkout(u, req.getParameter("receiverName"), req.getParameter("phone"),
                    req.getParameter("address"), req.getParameter("note"));
            WebUtil_24162132.flash(req, "Đặt hàng thành công! Bạn thanh toán khi nhận hàng.");
            resp.sendRedirect(ctx + "/order?id=" + orderId); // PRG: F5 trang kết quả không đặt thêm đơn
        } catch (BusinessException_24162132 e) {
            Cart_24162132 cart = carts.view(u.getId());
            if (cart.isEmpty()) { // thường do bấm đúp: đơn đầu đã thành công và xóa giỏ
                WebUtil_24162132.flashError(req, e.getMessage());
                resp.sendRedirect(ctx + "/cart");
                return;
            }
            req.setAttribute("error", e.getMessage());
            req.setAttribute("cart", cart);
            req.setAttribute("receiverName", req.getParameter("receiverName"));
            req.setAttribute("phone", req.getParameter("phone"));
            req.setAttribute("address", req.getParameter("address"));
            req.setAttribute("note", req.getParameter("note"));
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        }
    }
}
