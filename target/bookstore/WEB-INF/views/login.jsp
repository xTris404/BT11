<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="panel narrow">
  <h1>Đăng nhập</h1>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/login" class="stack">
    <label>Tên đăng nhập hoặc email <input type="text" name="username" maxlength="50" value="<c:out value='${param.username}'/>" required autofocus></label>
    <label>Mật khẩu <input type="password" name="password" required></label>
    <button class="btn">Đăng nhập</button>
  </form>
  <p>Chưa có tài khoản? <a href="${ctx}/register">Đăng ký</a></p>
</div>
</body>
</html>
