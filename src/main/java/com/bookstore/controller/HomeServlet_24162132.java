package com.bookstore.controller;

import com.bookstore.model.Author_24162132;
import com.bookstore.model.AuthorSection_24162132;
import com.bookstore.model.Book_24162132;
import com.bookstore.model.Page_24162132;
import com.bookstore.service.BookService_24162132;
import com.bookstore.util.WebUtil_24162132;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Câu 3: mỗi tác giả một khối, phân trang độc lập bằng tham số page_{authorId}. */
@WebServlet("/home")
public class HomeServlet_24162132 extends HttpServlet {
    private final BookService_24162132 books = new BookService_24162132();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Author_24162132> authors = books.authorsWithBooks();
        Map<Integer, Page_24162132<Book_24162132>> pages = new LinkedHashMap<>();
        for (Author_24162132 a : authors) {
            int wanted = WebUtil_24162132.toInt(req.getParameter("page_" + a.getAuthorId()), 1);
            pages.put(a.getAuthorId(), books.booksByAuthor(a.getAuthorId(), wanted));
        }
        List<AuthorSection_24162132> sections = new ArrayList<>();
        for (Author_24162132 a : authors) {
            // giữ nguyên trang hiện tại của các tác giả khác khi đổi trang của tác giả này
            StringBuilder others = new StringBuilder();
            for (Map.Entry<Integer, Page_24162132<Book_24162132>> e : pages.entrySet()) {
                if (e.getKey() != a.getAuthorId() && e.getValue().getPage() > 1)
                    others.append("page_").append(e.getKey()).append('=').append(e.getValue().getPage()).append("&amp;");
            }
            sections.add(new AuthorSection_24162132(a, pages.get(a.getAuthorId()), others.toString()));
        }
        req.setAttribute("sections", sections);
        WebUtil_24162132.moveFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
