<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><decorator:title default="BookStore"/> | BookStore</title>
  <link rel="stylesheet" href="${ctx}/assets/css/style.css">
  <decorator:head/>
</head>
<body class="theme-user">
<%@ include file="_nav.jspf" %>
<main class="container"><decorator:body/></main>
<%@ include file="_footer.jspf" %>
</body>
</html>
