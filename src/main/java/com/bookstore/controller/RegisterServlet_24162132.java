package com.bookstore.controller;

import com.bookstore.model.PendingRegistration_24162132;
import com.bookstore.service.BusinessException_24162132;
import com.bookstore.service.UserService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet_24162132 extends HttpServlet {
    static final String PENDING = "pendingRegistration";
    private final UserService_24162132 users = new UserService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PendingRegistration_24162132 p = users.startRegistration(req.getParameter("email"), req.getParameter("fullname"),
                    req.getParameter("phone"), req.getParameter("password"), req.getParameter("confirm"));
            req.getSession().setAttribute(PENDING, p);
            resp.sendRedirect(req.getContextPath() + "/verify-otp");
        } catch (BusinessException_24162132 e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
