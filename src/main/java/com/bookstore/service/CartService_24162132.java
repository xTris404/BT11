package com.bookstore.service;

import com.bookstore.dao.BookDAO_24162132;
import com.bookstore.dao.CartDAO_24162132;
import com.bookstore.model.Book_24162132;
import com.bookstore.model.Cart_24162132;
import com.bookstore.model.CartItem_24162132;
import java.util.List;

public class CartService_24162132 {
    private final CartDAO_24162132 cart = new CartDAO_24162132();
    private final BookDAO_24162132 books = new BookDAO_24162132();

    /** Giỏ kèm cờ "problem" cho từng dòng. KHÔNG tự sửa số lượng: tự ý đổi số lượng của người dùng còn tệ hơn báo cho họ biết. */
    public Cart_24162132 view(int userId) {
        List<CartItem_24162132> items = cart.findByUser(userId);
        for (CartItem_24162132 i : items) {
            if (i.getBook().getPrice() == null) i.setProblem("Sách chưa có giá bán - hãy xóa khỏi giỏ.");
            else if (i.getStock() <= 0) i.setProblem("Đã hết hàng - hãy xóa khỏi giỏ.");
            else if (i.getQuantity() > i.getStock()) i.setProblem("Kho chỉ còn " + i.getStock() + " cuốn - hãy giảm số lượng.");
        }
        return new Cart_24162132(items);
    }

    /** Cộng thêm qty vào số đang có. Tổng sau khi cộng không được vượt tồn kho. */
    public void add(int userId, int bookId, int qty) {
        if (qty < 1) throw new BusinessException_24162132("Số lượng phải từ 1 trở lên.");
        Book_24162132 b = sellable(bookId);
        int current = cart.findQuantity(userId, bookId);
        long next = (long) current + qty; // long: current + qty có thể tràn int nếu client gửi quantity ~ 2^31
        checkLimit(b, current, next);
        cart.setQuantity(userId, bookId, (int) next);
    }

    /** Đặt lại số lượng tuyệt đối trong [1, tồn kho]. Muốn bỏ sản phẩm phải dùng remove (không ngầm hiểu 0 = xóa). */
    public void setQuantity(int userId, int bookId, int qty) {
        if (qty < 1) throw new BusinessException_24162132("Số lượng phải từ 1 trở lên. Muốn bỏ sách khỏi giỏ hãy bấm Xóa.");
        int current = cart.findQuantity(userId, bookId);
        if (current == 0) throw new BusinessException_24162132("Sách này không có trong giỏ.");
        Book_24162132 b = sellable(bookId);
        checkLimit(b, current, qty);
        cart.setQuantity(userId, bookId, qty);
    }

    /** Idempotent: xóa thứ không còn ở đó (bấm 2 lần, mở 2 tab) không phải lỗi. */
    public void remove(int userId, int bookId) { cart.remove(userId, bookId); }

    public void clear(int userId) { cart.clear(userId); }

    // ---- helpers ----
    private Book_24162132 sellable(int bookId) {
        Book_24162132 b = books.findById(bookId);
        if (b == null) throw new BusinessException_24162132("Sách không tồn tại (có thể đã bị xóa).");
        if (b.getPrice() == null) throw new BusinessException_24162132("«" + b.getTitle() + "» chưa có giá bán.");
        if (stock(b) <= 0) throw new BusinessException_24162132("«" + b.getTitle() + "» đã hết hàng.");
        return b;
    }

    private static void checkLimit(Book_24162132 b, int current, long wanted) {
        int stock = stock(b);
        if (wanted > stock) {
            String have = current > 0 ? " (trong giỏ bạn đã có " + current + ")" : "";
            throw new BusinessException_24162132("«" + b.getTitle() + "» chỉ còn " + stock + " cuốn" + have + ".");
        }
    }

    private static int stock(Book_24162132 b) { return b.getQuantity() == null ? 0 : b.getQuantity(); }
}
