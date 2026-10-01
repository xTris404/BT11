<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Trang chủ</title></head>
<body>
<h1 id="products">Sách theo tác giả</h1>
<c:if test="${empty sections}"><p>Chưa có dữ liệu sách.</p></c:if>
<c:forEach var="sec" items="${sections}">
  <section class="author-block" id="author-${sec.author.authorId}">
    <h2>Tác giả: <c:out value="${sec.author.authorName}"/></h2>
    <div class="grid-3">
      <c:forEach var="b" items="${sec.page.items}">
        <article class="card">
          <a href="${ctx}/book?id=${b.bookId}"><img class="cover" src="<c:url value='${b.coverPath}'/>" alt="Bìa sách"></a>
          <ul class="meta">
            <li><b>Tiêu đề:</b> <a href="${ctx}/book?id=${b.bookId}"><c:out value="${b.title}"/></a></li>
            <li><b>Mã isbn:</b> ${b.isbn}</li>
            <li><b>Tác giả:</b> <c:out value="${b.authorNames}"/></li>
            <li><b>Publisher:</b> <c:out value="${b.publisher}"/></li>
            <li><b>Publisher_date:</b> <fmt:formatDate value="${b.publishDate}" pattern="dd/MM/yyyy"/></li>
            <li><b>Quantity:</b> ${b.quantity}</li>
            <li><b>Giá:</b> <c:if test="${b.price != null}"><fmt:formatNumber value="${b.price}" pattern="#,##0.00"/></c:if></li>
            <li><b>Review (${b.reviewCount})</b></li>
          </ul>
          <c:choose>
            <c:when test="${empty sessionScope.currentUser}"><a class="btn" href="${ctx}/login">Đăng nhập để mua</a></c:when>
            <c:when test="${sessionScope.currentUser.admin}"></c:when>
            <c:when test="${b.quantity > 0 and b.price != null}">
              <form method="post" action="${ctx}/cart" class="inline-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="bookId" value="${b.bookId}">
                <input type="hidden" name="quantity" value="1">
                <button class="btn">Thêm vào giỏ</button>
              </form>
            </c:when>
            <c:otherwise><span class="btn disabled">Hết hàng</span></c:otherwise>
          </c:choose>
        </article>
      </c:forEach>
    </div>
    <t:pager current="${sec.page.page}" total="${sec.page.totalPages}"
             baseUrl="${ctx}/home?${sec.otherParams}page_${sec.author.authorId}=" suffix="#author-${sec.author.authorId}"/>
  </section>
</c:forEach>
</body>
</html>
