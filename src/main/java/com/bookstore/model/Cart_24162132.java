package com.bookstore.model;

import java.math.BigDecimal;
import java.util.List;

public class Cart_24162132 {
    private final List<CartItem_24162132> items;

    public Cart_24162132(List<CartItem_24162132> items) { this.items = items; }

    public List<CartItem_24162132> getItems() { return items; }
    public boolean isEmpty() { return items.isEmpty(); }
    public BigDecimal getTotal() {
        BigDecimal t = BigDecimal.ZERO;
        for (CartItem_24162132 i : items) t = t.add(i.getSubtotal());
        return t;
    }
    public int getTotalQuantity() {
        int n = 0;
        for (CartItem_24162132 i : items) n += i.getQuantity();
        return n;
    }
    /** Chỉ cho thanh toán khi giỏ không rỗng và không dòng nào có vấn đề (hết hàng, vượt tồn kho, chưa có giá). */
    public boolean isCheckoutable() {
        if (items.isEmpty()) return false;
        for (CartItem_24162132 i : items) if (i.getProblem() != null) return false;
        return true;
    }
}
