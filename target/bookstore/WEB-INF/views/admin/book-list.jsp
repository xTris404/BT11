<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Quản lý sách</title></head>
<body>
<div class="toolbar">
  <h1>Quản lý sách <small>(${bookPage.totalItems} cuốn)</small></h1>
  <a class="btn" href="${ctx}/admin/books?action=new&amp;page=${bookPage.page}">+ Thêm sách</a>
</div>
<table class="table">
  <thead><tr><th>ID</th><th>Bìa</th><th>Tiêu đề</th><th>ISBN</th><th>Tác giả</th><th>Giá</th><th>SL</th><th></th></tr></thead>
  <tbody>
  <c:forEach var="b" items="${bookPage.items}">
    <tr>
      <td>${b.bookId}</td>
      <td><img class="thumb" src="<c:url value='${b.coverPath}'/>" alt=""></td>
      <td><a href="${ctx}/book?id=${b.bookId}"><c:out value="${b.title}"/></a></td>
      <td>${b.isbn}</td>
      <td><c:out value="${b.authorNames}"/></td>
      <td>${b.price}</td>
      <td>${b.quantity}</td>
      <td class="actions">
        <a href="${ctx}/admin/books?action=edit&amp;id=${b.bookId}&amp;page=${bookPage.page}">Sửa</a>
        <form method="post" action="${ctx}/admin/books" onsubmit="return confirm('Xóa sách này? Các review của sách cũng sẽ bị xóa.');">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="id" value="${b.bookId}">
          <input type="hidden" name="returnPage" value="${bookPage.page}">
          <button class="link danger">Xóa</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
<t:pager current="${bookPage.page}" total="${bookPage.totalPages}" baseUrl="${ctx}/admin/books?page="/>
</body>
</html>
