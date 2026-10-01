package com.bookstore.model;

import java.math.BigDecimal;

/** Bản chụp một dòng đơn hàng tại lúc đặt: title/unitPrice KHÔNG đọc lại từ bảng books. */
public class OrderItem_24162132 {
    private Integer bookId; // NULL nếu sách đã bị admin xóa sau khi đặt
    private String title;
    private BigDecimal unitPrice;
    private int quantity;

    public Integer getBookId() { return bookId; }
    public void setBookId(Integer v) { bookId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal v) { unitPrice = v; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { quantity = v; }
    public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
