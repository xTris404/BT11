package com.bookstore.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Book_24162132 {
    private int bookId;
    private Integer isbn, quantity;
    private String title, publisher, description, coverImage;
    private BigDecimal price;
    private Date publishDate;
    // các trường dẫn xuất (không phải cột của bảng books)
    private String authorNames;
    private int reviewCount;
    private List<Integer> authorIds = new ArrayList<>();

    public int getBookId() { return bookId; }
    public void setBookId(int v) { bookId = v; }
    public Integer getIsbn() { return isbn; }
    public void setIsbn(Integer v) { isbn = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String v) { publisher = v; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { price = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public Date getPublishDate() { return publishDate; }
    public void setPublishDate(Date v) { publishDate = v; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String v) { coverImage = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { quantity = v; }
    public String getAuthorNames() { return authorNames; }
    public void setAuthorNames(String v) { authorNames = v; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int v) { reviewCount = v; }
    public List<Integer> getAuthorIds() { return authorIds; }
    public void setAuthorIds(List<Integer> v) { authorIds = v; }

    /** Đường dẫn ảnh để JSP dùng với c:url (tên file trong /assets/images hoặc URL http(s)). */
    public String getCoverPath() {
        if (coverImage == null || coverImage.isBlank()) return "/assets/images/no-cover.svg";
        if (coverImage.startsWith("http://") || coverImage.startsWith("https://")) return coverImage;
        return "/assets/images/" + coverImage;
    }
}
