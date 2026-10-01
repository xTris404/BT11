package com.bookstore.model;

/** View model của trang home: 1 tác giả + trang sách hiện tại + phần query string của các tác giả khác. */
public class AuthorSection_24162132 {
    private final Author_24162132 author;
    private final Page_24162132<Book_24162132> page;
    private final String otherParams;

    public AuthorSection_24162132(Author_24162132 author, Page_24162132<Book_24162132> page, String otherParams) {
        this.author = author; this.page = page; this.otherParams = otherParams;
    }
    public Author_24162132 getAuthor() { return author; }
    public Page_24162132<Book_24162132> getPage() { return page; }
    public String getOtherParams() { return otherParams; }
}
