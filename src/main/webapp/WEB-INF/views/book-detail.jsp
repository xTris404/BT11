<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title><c:out value="${book.title}"/></title></head>
<body>
<p><a href="${ctx}/home">← Về trang chủ</a></p>
<div class="detail">
  <img class="cover big" src="<c:url value='${book.coverPath}'/>" alt="Bìa sách">
  <ul class="meta">
    <li><b>Tiêu đề:</b> <c:out value="${book.title}"/></li>
    <li><b>Mã isbn:</b> ${book.isbn}</li>
    <li><b>Tác giả:</b> <c:out value="${book.authorNames}"/></li>
    <li><b>Publisher:</b> <c:out value="${book.publisher}"/></li>
    <li><b>Publisher_date:</b> <fmt:formatDate value="${book.publishDate}" pattern="dd/MM/yyyy"/></li>
    <li><b>Quantity:</b> ${book.quantity}</li>
    <li><b>Giá:</b> ${book.price}</li>
    <li><b>Reviews (${book.reviewCount})</b></li>
    <li class="desc"><c:out value="${book.description}"/></li>
  </ul>
</div>

<div class="panel buy-box">
  <c:choose>
    <c:when test="${empty sessionScope.currentUser}">
      <p>Vui lòng <a href="${ctx}/login">đăng nhập</a> để mua sách.</p>
    </c:when>
    <c:when test="${sessionScope.currentUser.admin}"></c:when>
    <c:when test="${book.quantity > 0 and book.price != null}">
      <form method="post" action="${ctx}/cart" class="inline-form">
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="bookId" value="${book.bookId}">
        <label class="check">Số lượng (tối đa ${book.quantity})
          <input class="qty" type="number" name="quantity" value="1" min="1" max="${book.quantity}" required>
        </label>
        <button class="btn">Thêm vào giỏ</button>
      </form>
    </c:when>
    <c:otherwise><span class="btn disabled">Hết hàng</span></c:otherwise>
  </c:choose>
</div>

<h2>Reviews</h2>
<c:if test="${empty reviews}"><p>Chưa có đánh giá nào.</p></c:if>
<c:forEach var="r" items="${reviews}">
  <div class="review"><b><c:out value="${r.fullname}"/></b> (${r.rating}/5): <c:out value="${r.reviewText}"/></div>
</c:forEach>

<h3>Thêm review</h3>
<c:choose>
  <c:when test="${empty sessionScope.currentUser}">
    <p>Vui lòng <a href="${ctx}/login">đăng nhập</a> để đánh giá.</p>
  </c:when>
  <c:otherwise>
    <form method="post" action="${ctx}/review" class="stack">
      <input type="hidden" name="bookId" value="${book.bookId}">
      <label>Điểm
        <select name="rating">
          <c:forEach begin="1" end="5" var="i"><option value="${6 - i}">${6 - i} ★</option></c:forEach>
        </select>
      </label>
      <label>Nội dung <textarea name="reviewText" rows="4" maxlength="2000"></textarea></label>
      <small>Mỗi tài khoản chỉ có 1 đánh giá/sách; gửi lại sẽ cập nhật đánh giá cũ.</small>
      <button class="btn">Submit</button>
    </form>
  </c:otherwise>
</c:choose>
</body>
</html>
