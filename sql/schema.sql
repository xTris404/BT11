-- MySQL 8.0+ . Chạy toàn bộ file trong MySQL Workbench (Ctrl+Shift+Enter).
-- ⚠ Các lệnh DROP xóa dữ liệu cũ của 8 bảng này để có thể chạy lại nhiều lần.
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS users;

CREATE TABLE books (
  bookid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  isbn INT NULL, title VARCHAR(200) NULL, publisher VARCHAR(100) NULL,
  price DECIMAL(6,2) NULL, description TEXT NULL, publish_date DATE NULL,
  cover_image VARCHAR(100) NULL, quantity INT NULL);

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(50) NOT NULL UNIQUE, fullname VARCHAR(50) NULL, phone INT NULL,
  passwd VARCHAR(32) NOT NULL, signup_date DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  last_login DATETIME NULL, is_admin TINYINT(1) NULL DEFAULT 0);

CREATE TABLE author (
  author_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  author_name VARCHAR(100) NULL, date_of_birth DATE NULL);

CREATE TABLE book_author (
  bookid INT NOT NULL, author_id INT NOT NULL,
  PRIMARY KEY (bookid, author_id),
  CONSTRAINT fk_ba_book FOREIGN KEY (bookid) REFERENCES books(bookid),
  CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES author(author_id));

CREATE TABLE rating (
  userid INT NOT NULL, bookid INT NOT NULL, rating TINYINT NULL, review_text TEXT NULL,
  PRIMARY KEY (userid, bookid),
  CONSTRAINT fk_r_user FOREIGN KEY (userid) REFERENCES users(id),
  CONSTRAINT fk_r_book FOREIGN KEY (bookid) REFERENCES books(bookid));

-- ===== Giỏ hàng + đơn hàng (thêm sau; chạy riêng được bằng sql/migration_cart_order.sql) =====
-- Giỏ hàng lưu ở DB (không phải session): sống sót qua đăng xuất / hết hạn session / đổi thiết bị.
CREATE TABLE cart_item (
  userid INT NOT NULL, bookid INT NOT NULL, quantity INT NOT NULL,
  added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (userid, bookid),
  CONSTRAINT fk_ci_user FOREIGN KEY (userid) REFERENCES users(id),
  CONSTRAINT fk_ci_book FOREIGN KEY (bookid) REFERENCES books(bookid) ON DELETE CASCADE,
  CONSTRAINT chk_ci_qty CHECK (quantity > 0));

CREATE TABLE orders (
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
CREATE TABLE order_item (
  item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL, bookid INT NULL,
  title VARCHAR(200) NOT NULL, unit_price DECIMAL(6,2) NOT NULL, quantity INT NOT NULL,
  CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_oi_book FOREIGN KEY (bookid) REFERENCES books(bookid) ON DELETE SET NULL,
  CONSTRAINT chk_oi_qty CHECK (quantity > 0));

-- ===== Dữ liệu test =====
INSERT INTO author(author_name, date_of_birth) VALUES
 ('Nguyễn Nhật Ánh','1955-05-07'), ('Tô Hoài','1920-09-27'), ('Nam Cao','1915-10-29');

INSERT INTO books(isbn,title,publisher,price,description,publish_date,cover_image,quantity) VALUES
 (1001,'Mắt biếc','NXB Trẻ',85.00,'Chuyện tình đơn phương của Ngạn dành cho Hà Lan.','2019-08-01','no-cover.svg',20),
 (1002,'Cho tôi xin một vé đi tuổi thơ','NXB Trẻ',75.00,'Hồi ức tuổi thơ trong veo.','2018-05-10','no-cover.svg',15),
 (1003,'Tôi thấy hoa vàng trên cỏ xanh','NXB Trẻ',90.00,'Miền quê và những đứa trẻ.','2015-12-01','no-cover.svg',30),
 (1004,'Cô gái đến từ hôm qua','NXB Trẻ',70.00,'Chuyện học trò dí dỏm.','2017-03-15','no-cover.svg',12),
 (1005,'Kính vạn hoa','NXB Kim Đồng',65.00,'Bộ truyện dài tập nổi tiếng.','2016-06-01','no-cover.svg',40),
 (2001,'Dế Mèn phiêu lưu ký','NXB Kim Đồng',60.00,'Cuộc phiêu lưu của chú Dế Mèn.','2020-01-20','no-cover.svg',25),
 (2002,'Vợ chồng A Phủ','NXB Văn học',55.00,'Truyện ngắn Tây Bắc.','2014-09-09','no-cover.svg',18),
 (2003,'Chuyện ngày xưa','NXB Văn học',50.00,'Tập truyện ngắn.','2013-07-07','no-cover.svg',10),
 (2004,'Truyện Tây Bắc','NXB Văn học',68.00,'Tập truyện về vùng Tây Bắc.','2012-02-02','no-cover.svg',8),
 (3001,'Chí Phèo','NXB Văn học',45.00,'Bi kịch người nông dân bị tha hóa.','2011-11-11','no-cover.svg',22),
 (3002,'Lão Hạc','NXB Văn học',40.00,'Truyện ngắn hiện thực.','2010-10-10','no-cover.svg',17),
 (4001,'Tuyển tập truyện ngắn Việt Nam','NXB Giáo dục',99.00,'Sách có 2 tác giả.','2021-04-04','no-cover.svg',5);

INSERT INTO book_author(bookid, author_id)
SELECT b.bookid, a.author_id FROM books b JOIN author a ON
 (a.author_name='Nguyễn Nhật Ánh' AND b.isbn BETWEEN 1001 AND 1005) OR
 (a.author_name='Tô Hoài' AND (b.isbn BETWEEN 2001 AND 2004 OR b.isbn=4001)) OR
 (a.author_name='Nam Cao' AND (b.isbn BETWEEN 3001 AND 3002 OR b.isbn=4001));

-- Tên đăng nhập nằm ở cột email (đề không có cột username). Mật khẩu 123456 lưu MD5 (cột passwd chỉ 32 ký tự).
INSERT INTO users(email, fullname, phone, passwd, is_admin) VALUES
 ('admin',  'Quản trị viên', 912345678, MD5('123456'), 1),
 ('yennhi', 'Yến Nhi',       987654321, MD5('123456'), 0);

INSERT INTO rating(userid, bookid, rating, review_text)
SELECT u.id, b.bookid, 5, 'Sách rất hay, đáng đọc!' FROM users u, books b WHERE u.email='yennhi' AND b.isbn=1001;
INSERT INTO rating(userid, bookid, rating, review_text)
SELECT u.id, b.bookid, 4, 'Nội dung cảm động.' FROM users u, books b WHERE u.email='admin' AND b.isbn=1001;
