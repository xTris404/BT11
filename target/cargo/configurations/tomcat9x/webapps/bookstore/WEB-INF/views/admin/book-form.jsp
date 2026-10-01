<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>${book.bookId == 0 ? 'Thêm sách' : 'Sửa sách'}</title></head>
<body>
<h1>${book.bookId == 0 ? 'Thêm sách mới' : 'Cập nhật sách'}</h1>
<c:if test="${not empty errors}">
  <div class="alert error"><ul><c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach></ul></div>
</c:if>
<form method="post" action="${ctx}/admin/books" class="stack form-wide">
  <input type="hidden" name="action" value="save">
  <input type="hidden" name="bookId" value="${book.bookId}">
  <input type="hidden" name="returnPage" value="${returnPage}">
  <label>Tiêu đề * <input type="text" name="title" maxlength="200" value="<c:out value='${book.title}'/>" required></label>
  <label>ISBN <input type="number" name="isbn" value="${book.isbn}"></label>
  <label>Nhà xuất bản <input type="text" name="publisher" maxlength="100" value="<c:out value='${book.publisher}'/>"></label>
  <label>Giá <input type="number" name="price" step="0.01" min="0" max="9999.99" value="${book.price}"></label>
  <label>Ngày xuất bản <input type="date" name="publishDate" value="${book.publishDate}"></label>
  <label>Số lượng <input type="number" name="quantity" min="0" value="${book.quantity}"></label>
  <label>Ảnh bìa (tên file trong assets/images hoặc URL) <input type="text" name="coverImage" maxlength="100" value="<c:out value='${book.coverImage}'/>"></label>
  <label>Mô tả <textarea name="description" rows="5"><c:out value="${book.description}"/></textarea></label>
  <fieldset>
    <legend>Tác giả * (chọn ≥ 1)</legend>
    <c:forEach var="a" items="${allAuthors}">
      <label class="check"><input type="checkbox" name="authorIds" value="${a.authorId}" ${book.authorIds.contains(a.authorId) ? 'checked' : ''}> <c:out value="${a.authorName}"/></label>
    </c:forEach>
  </fieldset>
  <div><button class="btn">Lưu</button> <a href="${ctx}/admin/books?page=${returnPage}">Hủy</a></div>
</form>
</body>
</html>
