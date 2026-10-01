<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title>Đơn hàng #${order.orderId}</title></head>
<body>
<h1>Đơn hàng #${order.orderId}</h1>
<div class="panel">
  <ul class="meta">
    <li><b>Ngày đặt:</b> <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></li>
    <li><b>Trạng thái:</b> <c:out value="${order.statusLabel}"/></li>
    <li><b>Thanh toán:</b> <c:out value="${order.paymentLabel}"/></li>
    <li><b>Người nhận:</b> <c:out value="${order.receiverName}"/></li>
    <li><b>Điện thoại:</b> <c:out value="${order.phone}"/></li>
    <li><b>Địa chỉ:</b> <c:out value="${order.address}"/></li>
    <c:if test="${not empty order.note}"><li><b>Ghi chú:</b> <c:out value="${order.note}"/></li></c:if>
  </ul>
</div>
<table class="table" style="margin-top:16px">
  <thead><tr><th>Sách</th><th class="num">Đơn giá</th><th class="num">SL</th><th class="num">Thành tiền</th></tr></thead>
  <tbody>
  <c:forEach var="i" items="${order.items}">
    <tr>
      <td><c:choose>
            <c:when test="${not empty i.bookId}"><a href="${ctx}/book?id=${i.bookId}"><c:out value="${i.title}"/></a></c:when>
            <c:otherwise><c:out value="${i.title}"/> <span class="muted">(sách đã ngừng bán)</span></c:otherwise>
          </c:choose></td>
      <td class="num"><fmt:formatNumber value="${i.unitPrice}" pattern="#,##0.00"/></td>
      <td class="num">${i.quantity}</td>
      <td class="num"><fmt:formatNumber value="${i.subtotal}" pattern="#,##0.00"/></td>
    </tr>
  </c:forEach>
  </tbody>
  <tfoot><tr><td colspan="3" class="num"><b>Tổng cộng (trả khi nhận hàng)</b></td><td class="num"><b><fmt:formatNumber value="${order.total}" pattern="#,##0.00"/></b></td></tr></tfoot>
</table>
<p><a class="btn" href="${ctx}/home">Tiếp tục mua sách</a></p>
</body>
</html>
