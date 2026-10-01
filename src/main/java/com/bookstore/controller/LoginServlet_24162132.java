package com.bookstore.controller;

import com.bookstore.model.User_24162132;
import com.bookstore.service.UserService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet_24162132 extends HttpServlet {
    private final UserService_24162132 users = new UserService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebUtil_24162132.currentUser(req) != null) { resp.sendRedirect(req.getContextPath() + "/home"); return; }
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24162132 u = users.login(req.getParameter("username"), req.getParameter("password"));
        if (u == null) {
            req.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp); // quay lại trang đăng nhập
            return;
        }
        HttpSession s = req.getSession();
        req.changeSessionId(); // chống session fixation: đổi ID ngay khi quyền thay đổi
        s.setAttribute(WebUtil_24162132.USER, u);
        resp.sendRedirect(req.getContextPath() + (u.isAdmin() ? "/admin/books" : "/home"));
    }
}
