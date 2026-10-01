package com.bookstore.model;

import java.math.BigDecimal;

/** Một dòng trong giỏ: sách + số lượng đang chọn. {@code problem} do tầng service gán khi dòng không thể thanh toán. */
public class CartItem_24162132 {
    private final Book_24162132 book;
    private final int quantity;
    private String problem;

    public CartItem_24162132(Book_24162132 book, int quantity) { this.book = book; this.quantity = quantity; }

    public Book_24162132 getBook() { return book; }
    public int getQuantity() { return quantity; }
    /** Tồn kho hiện tại. Cột books.quantity cho phép NULL => coi như hết hàng. */
    public int getStock() { return book.getQuantity() == null ? 0 : book.getQuantity(); }
    public BigDecimal getSubtotal() {
        return book.getPrice() == null ? BigDecimal.ZERO : book.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
    public String getProblem() { return problem; }
    public void setProblem(String v) { problem = v; }
}
