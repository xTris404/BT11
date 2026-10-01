<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="panel narrow">
  <h1>Đăng ký tài khoản</h1>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/register" class="stack">
    <label>Email <input type="email" name="email" maxlength="50" value="<c:out value='${param.email}'/>" required></label>
    <label>Họ tên <input type="text" name="fullname" maxlength="50" value="<c:out value='${param.fullname}'/>" required></label>
    <label>Số điện thoại (không bắt buộc) <input type="text" name="phone" pattern="0[0-9]{9}" value="<c:out value='${param.phone}'/>"></label>
    <label>Mật khẩu <input type="password" name="password" minlength="6" required></label>
    <label>Nhập lại mật khẩu <input type="password" name="confirm" required></label>
    <button class="btn">Đăng ký &amp; nhận mã OTP</button>
  </form>
  <p>Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></p>
</div>
</body>
</html>
