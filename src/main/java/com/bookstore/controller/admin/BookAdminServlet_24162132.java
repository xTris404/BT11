package com.bookstore.controller.admin;

import com.bookstore.model.Book_24162132;
import com.bookstore.service.BookService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Câu 6: CRUD books có phân trang. Được AdminFilter_24162132 bảo vệ (/admin/*). */
@WebServlet(urlPatterns = {"/admin", "/admin/books"})
public class BookAdminServlet_24162132 extends HttpServlet {
    private final BookService_24162132 books = new BookService_24162132();
    private static final String LIST = "/WEB-INF/views/admin/book-list.jsp", FORM = "/WEB-INF/views/admin/book-form.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        if ("/admin".equals(req.getServletPath())) { resp.sendRedirect(ctx + "/admin/books"); return; }
        String action = req.getParameter("action");
        if ("new".equals(action)) { showForm(req, resp, new Book_24162132()); return; }
        if ("edit".equals(action)) {
            Book_24162132 b = books.get(WebUtil_24162132.toInt(req.getParameter("id"), -1));
            if (b == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            showForm(req, resp, b);
            return;
        }
        req.setAttribute("bookPage", books.list(WebUtil_24162132.toInt(req.getParameter("page"), 1)));
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher(LIST).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        int returnPage = WebUtil_24162132.toInt(req.getParameter("returnPage"), 1);
        if ("delete".equals(req.getParameter("action"))) {
            books.delete(WebUtil_24162132.toInt(req.getParameter("id"), -1));
            WebUtil_24162132.flash(req, "Đã xóa sách.");
            resp.sendRedirect(ctx + "/admin/books?page=" + returnPage);
            return;
        }
        List<String> errors = new ArrayList<>();
        Book_24162132 b = readBook(req, errors);
        if (errors.isEmpty()) errors.addAll(books.save(b));
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            showForm(req, resp, b);
            return;
        }
        boolean created = WebUtil_24162132.toInt(req.getParameter("bookId"), 0) == 0;
        WebUtil_24162132.flash(req, created ? "Đã thêm sách mới." : "Đã cập nhật sách.");
        // sách mới nằm cuối danh sách (ORDER BY bookid): nhảy tới trang cuối, Page_24162132.clamp lo phần còn lại
        resp.sendRedirect(ctx + "/admin/books?page=" + (created ? Integer.MAX_VALUE : returnPage));
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Book_24162132 b) throws ServletException, IOException {
        req.setAttribute("book", b);
        req.setAttribute("allAuthors", books.allAuthors());
        req.setAttribute("returnPage", WebUtil_24162132.toInt(req.getParameter("returnPage"), WebUtil_24162132.toInt(req.getParameter("page"), 1)));
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    private Book_24162132 readBook(HttpServletRequest req, List<String> errors) {
        Book_24162132 b = new Book_24162132();
        b.setBookId(WebUtil_24162132.toInt(req.getParameter("bookId"), 0));
        b.setTitle(trim(req.getParameter("title")));
        b.setPublisher(nullIfBlank(req.getParameter("publisher")));
        b.setDescription(nullIfBlank(req.getParameter("description")));
        b.setCoverImage(nullIfBlank(req.getParameter("coverImage")));
        try {
            String s = nullIfBlank(req.getParameter("isbn"));
            b.setIsbn(s == null ? null : Integer.valueOf(s));
        } catch (NumberFormatException e) { errors.add("ISBN phải là số nguyên không quá 2.147.483.647 (cột isbn kiểu int)."); }
        try {
            String s = nullIfBlank(req.getParameter("price"));
            b.setPrice(s == null ? null : new BigDecimal(s).setScale(2, RoundingMode.HALF_UP));
        } catch (NumberFormatException e) { errors.add("Giá không hợp lệ."); }
        try {
            String s = nullIfBlank(req.getParameter("publishDate"));
            b.setPublishDate(s == null ? null : Date.valueOf(LocalDate.parse(s)));
        } catch (DateTimeParseException e) { errors.add("Ngày xuất bản không hợp lệ (yyyy-MM-dd)."); }
        try {
            String s = nullIfBlank(req.getParameter("quantity"));
            b.setQuantity(s == null ? null : Integer.valueOf(s));
        } catch (NumberFormatException e) { errors.add("Số lượng phải là số nguyên."); }
        String[] ids = req.getParameterValues("authorIds");
        List<Integer> authorIds = new ArrayList<>();
        if (ids != null) for (String id : ids) { int v = WebUtil_24162132.toInt(id, -1); if (v > 0) authorIds.add(v); }
        b.setAuthorIds(authorIds);
        return b;
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static String nullIfBlank(String s) { return s == null || s.isBlank() ? null : s.trim(); }
}
