package com.bookstore.dao;

/** Trạng thái DB không còn cho phép đặt hàng (giỏ rỗng, hết tồn kho, thiếu giá...). Thông điệp an toàn để hiển thị.
 *  Nằm ở tầng DAO vì chỉ DAO biết điều này SAU KHI đã khóa dòng; tầng service đổi nó thành BusinessException. */
public class CheckoutConflictException_24162132 extends RuntimeException {
    public CheckoutConflictException_24162132(String message) { super(message); }
}
