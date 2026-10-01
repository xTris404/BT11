<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title>Giỏ hàng</title></head>
<body>
<h1>Giỏ hàng của bạn</h1>
<c:choose>
  <c:when test="${empty cart.items}">
    <div class="panel"><p>Giỏ hàng đang trống.</p><a class="btn" href="${ctx}/home">Tiếp tục mua sách</a></div>
  </c:when>
  <c:otherwise>
    <table class="table cart-table">
      <thead><tr><th></th><th>Sách</th><th class="num">Đơn giá</th><th>Số lượng</th><th class="num">Thành tiền</th><th></th></tr></thead>
      <tbody>
      <c:forEach var="i" items="${cart.items}">
        <tr>
          <td><img class="thumb" src="<c:url value='${i.book.coverPath}'/>" alt="Bìa"></td>
          <td>
            <a href="${ctx}/book?id=${i.book.bookId}"><c:out value="${i.book.title}"/></a>
            <div class="muted">Còn ${i.stock} cuốn trong kho</div>
            <c:if test="${not empty i.problem}"><div class="warn">⚠ <c:out value="${i.problem}"/></div></c:if>
          </td>
          <td class="num"><fmt:formatNumber value="${i.book.price}" pattern="#,##0.00"/></td>
          <td>
            <form method="post" action="${ctx}/cart" class="inline-form">
              <input type="hidden" name="action" value="update">
              <input type="hidden" name="bookId" value="${i.book.bookId}">
              <input class="qty" type="number" name="quantity" value="${i.quantity}" min="1" max="${i.stock}" required>
              <button class="btn">Cập nhật</button>
            </form>
          </td>
          <td class="num"><fmt:formatNumber value="${i.subtotal}" pattern="#,##0.00"/></td>
          <td>
            <form method="post" action="${ctx}/cart" class="inline-form">
              <input type="hidden" name="action" value="remove">
              <input type="hidden" name="bookId" value="${i.book.bookId}">
              <button class="link danger" onclick="return confirm('Xóa sách này khỏi giỏ hàng?')">Xóa</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      </tbody>
      <tfoot>
        <tr><td colspan="4" class="num"><b>Tổng cộng (${cart.totalQuantity} cuốn)</b></td>
            <td class="num"><b><fmt:formatNumber value="${cart.total}" pattern="#,##0.00"/></b></td><td></td></tr>
      </tfoot>
    </table>

    <div class="toolbar cart-actions">
      <form method="post" action="${ctx}/cart" class="inline-form">
        <input type="hidden" name="action" value="clear">
        <button class="link danger" onclick="return confirm('Xóa toàn bộ giỏ hàng?')">Xóa hết giỏ hàng</button>
      </form>
      <div class="actions">
        <a href="${ctx}/home">← Tiếp tục mua sách</a>
        <c:choose>
          <c:when test="${cart.checkoutable}"><a class="btn" href="${ctx}/checkout">Thanh toán (COD)</a></c:when>
          <c:otherwise><span class="btn disabled" title="Hãy xử lý các dòng có cảnh báo trước">Thanh toán (COD)</span></c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:otherwise>
</c:choose>
</body>
</html>
