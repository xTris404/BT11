<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title>Thanh toán</title></head>
<body>
<h1>Thanh toán đơn hàng</h1>
<c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/> <a href="${ctx}/cart">Về giỏ hàng</a></div></c:if>

<div class="checkout-grid">
  <section class="panel">
    <h2>Thông tin giao hàng</h2>
    <form method="post" action="${ctx}/checkout" class="stack" onsubmit="var b=this.querySelector('button.btn'); if(b){b.disabled=true;b.textContent='Đang xử lý...';}">
      <label>Họ tên người nhận <input type="text" name="receiverName" maxlength="100" value="<c:out value='${receiverName}'/>" required></label>
      <label>Số điện thoại <input type="tel" name="phone" maxlength="10" pattern="0[0-9]{9}" placeholder="0912345678" value="<c:out value='${phone}'/>" required></label>
      <label>Địa chỉ giao hàng <textarea name="address" rows="3" maxlength="255" required><c:out value="${address}"/></textarea></label>
      <label>Ghi chú (không bắt buộc) <textarea name="note" rows="2" maxlength="255"><c:out value="${note}"/></textarea></label>
      <fieldset>
        <legend>Phương thức thanh toán</legend>
        <label class="check"><input type="radio" checked disabled> Thanh toán khi nhận hàng (COD)</label>
      </fieldset>
      <button class="btn">Đặt hàng</button>
    </form>
  </section>

  <section class="panel">
    <h2>Đơn hàng của bạn</h2>
    <table class="table">
      <c:forEach var="i" items="${cart.items}">
        <tr>
          <td><c:out value="${i.book.title}"/> × ${i.quantity}
              <c:if test="${not empty i.problem}"><div class="warn">⚠ <c:out value="${i.problem}"/></div></c:if></td>
          <td class="num"><fmt:formatNumber value="${i.subtotal}" pattern="#,##0.00"/></td>
        </tr>
      </c:forEach>
      <tr><td><b>Tổng cộng</b></td><td class="num"><b><fmt:formatNumber value="${cart.total}" pattern="#,##0.00"/></b></td></tr>
    </table>
    <p class="muted">Giá và tồn kho được kiểm tra lại lúc đặt hàng. <a href="${ctx}/cart">Sửa giỏ hàng</a></p>
  </section>
</div>
</body>
</html>
