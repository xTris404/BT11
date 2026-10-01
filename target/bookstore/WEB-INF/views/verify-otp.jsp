<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Xác thực OTP</title></head>
<body>
<div class="panel narrow">
  <h1>Xác thực email</h1>
  <p>Mã OTP 6 chữ số đã được gửi tới <b><c:out value="${maskedEmail}"/></b>.</p>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
  <c:if test="${not empty info}"><div class="alert success"><c:out value="${info}"/></div></c:if>
  <form method="post" action="${ctx}/verify-otp" class="stack">
    <label>Mã OTP <input type="text" name="otp" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" required></label>
    <button class="btn">Xác nhận</button>
  </form>
  <form method="post" action="${ctx}/verify-otp"><input type="hidden" name="action" value="resend"><button class="link">Gửi lại mã</button></form>
</div>
</body>
</html>
