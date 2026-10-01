# BookStore – Lập Trình Web 

> 💡 Maven WAR · Servlet 4.0 + JDBC (**MySQL 8**) + JSP/JSTL · SiteMesh 2.4.2 · chạy trên **Tomcat 9** (không dùng Tomcat 10+: nó dùng `jakarta.*`, code này dùng `javax.*`).

## 1. Chạy project

1. MySQL Workbench → chạy `sql/schema.sql` (tạo DB `bookstore` + dữ liệu test).
   - ⚠️ Đã có DB từ bài trước và **không muốn mất dữ liệu**? Chỉ chạy `sql/migration_cart_order.sql` (thêm 3 bảng `cart_item`, `orders`, `order_item`, chạy lại nhiều lần được).
2. Kiểm tra `src/main/resources/app.properties` (mặc định `root` / `Tr1@MySQL2026`, DB `bookstore`, cổng 3306).
3. Sửa footer trong `src/main/webapp/WEB-INF/web.xml` (`studentName`, `studentId`, `examCode`).
4. Đứng ở thư mục có `pom.xml`, chạy:
   ```
   mvn clean package cargo:run
   ```
   Cargo tự tải Tomcat 9.0.93 và deploy (cổng **8081** để không đụng Tomcat khác đang chiếm 8080) → mở `http://localhost:8081/bookstore/`. (Hoặc copy `target/bookstore.war` vào `webapps` của Tomcat 9.)

| Đăng nhập | Mật khẩu | Vai trò |
|---|---|---|
| `admin` | `123456` | Admin → vào `/admin/books` |
| `yennhi` | `123456` | User → vào `/home` |

> 💡 Đề không có cột `username`, nên ô "Tên đăng nhập" được so với cột **`users.email`**. Hai tài khoản seed dùng `admin`, `yennhi` làm giá trị đó; tài khoản đăng ký mới (Câu 2) đăng nhập bằng email thật.
>
> 💡 `mail.enabled=false` (mặc định): OTP **được in ra log Tomcat** thay vì gửi mail. Đặt `true` + App Password Gmail khi cần gửi thật.

> ⚠️ **Nhận diện đúng Tomcat:** trang lỗi 404 ghi `Apache Tomcat/10.x` nghĩa là request đang vào Tomcat 10 (WAR này sẽ không nạp được). Bản Tomcat 9 do Cargo chạy hiện `Apache Tomcat/9.0.93`. Nếu vẫn thấy 10.x → có một Tomcat 10 (service Windows / IDE) đang chạy; hãy dừng nó.


## 2. Đề bài → file

| Câu | Nội dung | File chính |
|---|---|---|
| 1 | 3 tầng, Sitemesh 2 vai trò, header/footer | `controller/` · `service/` · `dao/` · `WEB-INF/decorators.xml` · `decorators/user.jsp`, `admin.jsp`, `_nav.jspf`, `_footer.jspf` |
| 2 | Đăng ký OTP, đăng nhập, đăng xuất (Session) | `RegisterServlet`, `VerifyOtpServlet`, `LoginServlet`, `LogoutServlet`, `UserService`, `MailUtil` |
| 3 | Home: 3 sách/trang **theo từng tác giả** | `HomeServlet`, `BookDAO.findByAuthor`, `home.jsp`, `tags/pager.tag` |
| 4 | Chi tiết sách + review | `BookDetailServlet`, `ReviewServlet`, `ReviewDAO.upsert`, `book-detail.jsp` |
| 7 | Giỏ hàng (User): thêm / sửa số lượng trong giới hạn tồn kho / xóa / xóa hết | `CartServlet` (`/cart`), `CartService`, `CartDAO`, `Cart`, `CartItem`, `cart.jsp`, nút trên `home.jsp` + `book-detail.jsp` |
| 8 | Thanh toán COD | `CheckoutServlet` (`/checkout`), `OrderServlet` (`/order`), `OrderService`, `OrderDAO`, `Order`, `OrderItem`, `checkout.jsp`, `order-detail.jsp`, `UserFilter` |
| 6 | CRUD books có phân trang | `admin/BookAdminServlet`, `BookService`, `admin/book-list.jsp`, `book-form.jsp` |


## 3. Cấu trúc 3 tầng

```
controller (Presentation) ──► service (Business) ──► dao (Data Access) ──► MySQL
   Servlet + JSP                 luật nghiệp vụ          JDBC thuần
```

Quy tắc phụ thuộc: **chỉ đi xuống**. Controller không import DAO; DAO không biết `HttpServletRequest`.

## 4. Quyết định thiết kế → nếu bỏ thì hỏng gì

| Quyết định | Lý do (nguyên lý) | Nếu bỏ / failure mode |
|---|---|---|
| `EncodingFilter` UTF-8, chạy đầu tiên | Tomcat đọc parameter theo ISO-8859-1 nếu chưa set; phải set **trước** lần đọc đầu | Tên/đánh giá tiếng Việt thành `Nguyá»…n` |
| DB `utf8mb4` + `characterEncoding=UTF-8` trong URL | Ba điểm phải cùng mã hóa: trang web ↔ kết nối JDBC ↔ cột dữ liệu | Mất dấu, thành `?` hoặc lỗi `Incorrect string value` |
| `SiteMeshFilter` khai báo **sau** `AdminFilter` | Filter chạy theo thứ tự khai báo; guard phải chặn trước khi trang được dựng | Request trái phép vẫn tốn công render |
| `decorators.xml`: `/admin/*` đứng **trước** `/*` | Sitemesh dùng decorator khớp đầu tiên | Admin bị áp giao diện user |
| Sitemesh khớp theo **URL gốc** (`/admin/books`), JSP nằm trong `WEB-INF` | Filter bao ngoài servlet nên thấy URL người dùng gõ, không thấy đường dẫn JSP sau `forward` | Đặt pattern theo đường dẫn JSP sẽ không bao giờ khớp |
| `AdminFilter` chặn `/admin/*` ở server | Ẩn menu chỉ là UX, người dùng vẫn gõ được URL | User thường vào thẳng `/admin/books` xóa sách |
| OTP + dữ liệu đăng ký giữ trong **Session**, chỉ `INSERT` sau khi OTP đúng | Schema đề không có cột trạng thái kích hoạt/OTP | Ghi DB trước → email rác chiếm chỗ; không ai xóa |
| OTP: `SecureRandom`, TTL 5 phút, tối đa 5 lần thử, cooldown gửi lại 30 giây | 6 chữ số chỉ có 10⁶ khả năng | Bỏ giới hạn lần thử → dò OTP trong vài phút |
| `changeSessionId()` khi đăng nhập | Session ID trước đăng nhập có thể do kẻ tấn công cài sẵn | Session fixation |
| Lỗi đăng nhập chung "Tên đăng nhập hoặc mật khẩu không đúng" | Tách hai loại lỗi cho phép dò tài khoản nào tồn tại | User enumeration |
| `userid` của review lấy từ **session**, không từ form | Trường form do client kiểm soát | Sửa `userid` trong form → đánh giá thay người khác |
| `INSERT … ON DUPLICATE KEY UPDATE` cho review | PK `(userid, bookid)` → 1 đánh giá/sách; "update rồi insert" có khoảng hở giữa hai lệnh | Bấm Submit 2 lần → lỗi trùng khóa (500) |
| `PreparedStatement` mọi nơi | Tham số không bao giờ được diễn giải như SQL | SQL injection ở login/CRUD |
| `<c:out>` cho mọi dữ liệu người dùng | Escape HTML khi in | Review chứa `<script>` → stored XSS |
| Xóa sách trong **1 transaction** (rating → book_author → books) | FK bắt buộc xóa con trước; lỗi giữa chừng phải rollback | Xóa dở dang: mất review nhưng sách còn, hoặc FK lỗi |
| Count trước rồi `Page.clamp` | `?page=999` / `?page=-1` không được trả trang rỗng; `LIMIT/OFFSET` âm báo lỗi | 500 khi người dùng sửa URL |
| Tham số phân trang riêng `page_{authorId}` + giữ trang tác giả khác | Mỗi tác giả là một danh sách độc lập | Đổi trang tác giả A làm tác giả B nhảy về trang 1 |
| `GROUP_CONCAT` + subquery đếm review trong **một** câu SELECT | 3 sách/trang × (1 + 2 query phụ) = N+1 round-trip | 7–10 query mỗi khối tác giả |
| `pager.tag` dùng chung | Home & admin cùng logic phân trang | Sửa lỗi ở hai nơi, dễ lệch nhau |
| Home chỉ liệt kê tác giả **có sách** (`EXISTS`) | Tránh khối "Tác giả: X" rỗng | Khối trống trên trang chủ |

## 5. Bẫy trong schema đề (đã xử lý, nhưng nên biết)

- **`users.phone INT`**: số 10 chữ số bắt đầu bằng `0` mất số 0 đầu, và ≥ 11 chữ số tràn `int`. Code chỉ nhận `0\d{9}`, lưu dạng số.
- **`books.isbn INT`**: ISBN-13 (13 chữ số) **không lưu vừa**. Form báo lỗi khi vượt `2.147.483.647`.
- **`books.price DECIMAL(6,2)`**: tối đa `9999.99`. Validate ở `BookService`.
- **`users.passwd VARCHAR(32)`**: chỉ vừa MD5 hex (32 ký tự). MD5 không muối, tính rất nhanh → yếu trước rainbow table/brute-force GPU. Thực tế: đổi cột thành `VARCHAR(60+)` và dùng BCrypt/Argon2.
- **Không có cột `username`**: xem mục 1 (dùng cột `email`).
- Thêm `UNIQUE` cho `users.email` (đề không ghi) để DB — không chỉ code — chặn trùng khi hai người đăng ký đồng thời.

## 6. Giỏ hàng & thanh toán COD

### 6.1 Luồng

```
home / book-detail ──POST /cart (action=add)──► CartService.add ──► CartDAO.setQuantity ──► cart_item
/cart (xem · update · remove · clear)
/checkout (form giao hàng) ──POST──► OrderService.checkout ──► OrderDAO.place  [1 transaction]
                                         │ khóa cart_item+books → kiểm tra kho → INSERT orders/order_item → trừ kho → xóa giỏ
                                         └──► redirect /order?id=… (Post-Redirect-Get)
```

Cả `/cart`, `/checkout`, `/order` đều đi qua `UserFilter`: chưa đăng nhập → về `/login`; tài khoản Admin → 403 (Admin có khu vực riêng, không mua hàng).

### 6.2 Checklist tự kiểm tra (đăng nhập `yennhi` / `123456`)

1. Trang chủ → **Thêm vào giỏ** sách “Mắt biếc” (kho 20) → vào `/cart` thấy 1 dòng.
2. Chi tiết sách “Tuyển tập truyện ngắn Việt Nam” (kho 5): thêm 5 → OK; thêm tiếp 1 → báo `chỉ còn 5 cuốn (trong giỏ bạn đã có 5)`.
3. Trong giỏ: sửa SL = `0` / `-1` / `abc` / `6` (với kho 5) → đều bị từ chối, số cũ giữ nguyên; sửa SL = `3` → OK, tổng tiền đổi.
4. **Xóa** một dòng → mất dòng đó; **Xóa hết** → giỏ trống.
5. Admin vào `/cart` → 403. Chưa đăng nhập vào `/cart` → về `/login`.
6. `/checkout`: để trống tên/địa chỉ, sđt `912345678` → trình duyệt chặn bằng `pattern`, server kiểm tra lại độc lập; khi server từ chối thì form **giữ lại** nội dung đã nhập.
7. Đặt hàng hợp lệ → chuyển sang `/order?id=…`, tổng đúng; vào MySQL: `books.quantity` giảm đúng, `cart_item` của user rỗng, `orders`/`order_item` có dữ liệu.
8. Mở `/cart` ở tab A, tab B đặt hết cùng sách, quay lại tab A bấm thanh toán → bị chặn (về `/cart` kèm cảnh báo hết hàng), **không** có đơn mới.
9. Đổi `?id=` trong `/order` sang đơn của người khác → 404.
10. Admin xóa một sách đã được đặt → thành công; đơn cũ vẫn hiện tên sách + ghi “(sách đã ngừng bán)”.
