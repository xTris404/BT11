package com.bookstore.filter;

import com.bookstore.model.User_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Chặn ở server /cart, /checkout, /order: phải đăng nhập và là User (Admin có khu vực riêng, không mua hàng).
 *  Ẩn nút trên giao diện chỉ là UX: POST thẳng tới /cart vẫn gửi được nếu không có filter này. */
public class UserFilter_24162132 implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse p = (HttpServletResponse) res;
        User_24162132 u = WebUtil_24162132.currentUser(r);
        if (u == null) {
            WebUtil_24162132.flashError(r, "Vui lòng đăng nhập để dùng giỏ hàng.");
            p.sendRedirect(r.getContextPath() + "/login");
        } else if (u.isAdmin()) {
            p.sendError(HttpServletResponse.SC_FORBIDDEN, "Tài khoản quản trị không dùng chức năng mua hàng.");
        } else {
            chain.doFilter(req, res);
        }
    }
}
