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
import javax.servlet.http.HttpSession;

@WebServlet("/verify-otp")
public class VerifyOtpServlet_24162132 extends HttpServlet {
    private final UserService_24162132 users = new UserService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PendingRegistration_24162132 p = pending(req);
        if (p == null) { resp.sendRedirect(req.getContextPath() + "/register"); return; }
        show(req, resp, p);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PendingRegistration_24162132 p = pending(req);
        String ctx = req.getContextPath();
        if (p == null) { resp.sendRedirect(ctx + "/register"); return; }
        try {
            if ("resend".equals(req.getParameter("action"))) {
                users.resendOtp(p);
                req.setAttribute("info", "Đã gửi lại mã OTP tới email của bạn.");
            } else {
                users.completeRegistration(p, req.getParameter("otp"));
                req.getSession().removeAttribute(RegisterServlet_24162132.PENDING);
                WebUtil_24162132.flash(req, "Kích hoạt tài khoản thành công. Hãy đăng nhập.");
                resp.sendRedirect(ctx + "/login");
                return;
            }
        } catch (BusinessException_24162132 e) {
            if (p.isLocked()) {
                req.getSession().removeAttribute(RegisterServlet_24162132.PENDING);
                WebUtil_24162132.flashError(req, e.getMessage());
                resp.sendRedirect(ctx + "/register");
                return;
            }
            req.setAttribute("error", e.getMessage());
        }
        show(req, resp, p);
    }

    private PendingRegistration_24162132 pending(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (PendingRegistration_24162132) s.getAttribute(RegisterServlet_24162132.PENDING);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, PendingRegistration_24162132 p) throws ServletException, IOException {
        String e = p.getEmail();
        int at = e.indexOf('@');
        req.setAttribute("maskedEmail", e.charAt(0) + "***" + e.substring(at));
        req.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(req, resp);
    }
}
