package com.bookstore.controller;

import com.bookstore.model.Order_24162132;
import com.bookstore.model.User_24162132;
import com.bookstore.service.OrderService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Xem chi tiết / xác nhận đơn hàng của CHÍNH MÌNH. */
@WebServlet("/order")
public class OrderServlet_24162132 extends HttpServlet {
    private final OrderService_24162132 orders = new OrderService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24162132 u = WebUtil_24162132.currentUser(req);
        Order_24162132 o = orders.get(u.getId(), WebUtil_24162132.toInt(req.getParameter("id"), -1));
        if (o == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy đơn hàng."); return; }
        req.setAttribute("order", o);
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
    }
}
