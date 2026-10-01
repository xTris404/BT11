package com.bookstore.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/** UTF-8 cho tiếng Việt + đặt ${ctx} (context path) cho mọi JSP. Phải chạy TRƯỚC mọi lệnh đọc parameter. */
public class EncodingFilter_24162132 implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        req.setAttribute("ctx", ((HttpServletRequest) req).getContextPath());
        chain.doFilter(req, res);
    }
}
