<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="current" type="java.lang.Integer" required="true" %>
<%@ attribute name="total" type="java.lang.Integer" required="true" %>
<%@ attribute name="baseUrl" required="true" description="URL kết thúc bằng 'page=' - số trang sẽ được nối vào sau" %>
<%@ attribute name="suffix" required="false" description="Ví dụ #author-3" %>
<nav class="pager">
  <c:choose>
    <c:when test="${current > 1}"><a href="${baseUrl}${current - 1}${suffix}">Trang trước</a></c:when>
    <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
  </c:choose>
  <span>–</span>
  <c:forEach begin="1" end="${total}" var="i">
    <c:choose>
      <c:when test="${i == current}"><strong>${i}</strong></c:when>
      <c:otherwise><a href="${baseUrl}${i}${suffix}">${i}</a></c:otherwise>
    </c:choose>
  </c:forEach>
  <span>–</span>
  <c:choose>
    <c:when test="${current < total}"><a href="${baseUrl}${current + 1}${suffix}">Trang sau</a></c:when>
    <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
  </c:choose>
</nav>
