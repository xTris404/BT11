-- MySQL 8.0.16+ . Chỉ THÊM 3 bảng giỏ hàng/đơn hàng vào DB bookstore đã có; KHÔNG xóa dữ liệu cũ, chạy lại nhiều lần được.
USE bookstore;

-- ===== Giỏ hàng + đơn hàng (thêm sau; chạy riêng được bằng sql/migration_cart_order.sql) =====
-- Giỏ hàng lưu ở DB (không phải session): sống sót qua đăng xuất / hết hạn session / đổi thiết bị.
CREATE TABLE IF NOT EXISTS cart_item (
  userid INT NOT NULL, bookid INT NOT NULL, quantity INT NOT NULL,
  added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (userid, bookid),
  CONSTRAINT fk_ci_user FOREIGN KEY (userid) REFERENCES users(id),
  CONSTRAINT fk_ci_book FOREIGN KEY (bookid) REFERENCES books(bookid) ON DELETE CASCADE,
  CONSTRAINT chk_ci_qty CHECK (quantity > 0));

CREATE TABLE IF NOT EXISTS orders (
  order_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  receiver_name VARCHAR(100) NOT NULL, phone VARCHAR(15) NOT NULL,
  address VARCHAR(255) NOT NULL, note VARCHAR(255) NULL,
  payment_method VARCHAR(10) NOT NULL DEFAULT 'COD',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  total DECIMAL(10,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_o_user FOREIGN KEY (userid) REFERENCES users(id));

-- title + unit_price là BẢN CHỤP tại lúc đặt: sửa giá / đổi tên / xóa sách sau này không làm đổi lịch sử đơn.
CREATE TABLE IF NOT EXISTS order_item (
  item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL, bookid INT NULL,
  title VARCHAR(200) NOT NULL, unit_price DECIMAL(6,2) NOT NULL, quantity INT NOT NULL,
  CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_oi_book FOREIGN KEY (bookid) REFERENCES books(bookid) ON DELETE SET NULL,
  CONSTRAINT chk_oi_qty CHECK (quantity > 0));
  
  
  
  

