<%
    String basePath = request.getScheme() +
            "://"
            + request.getServerName() + ":"
            + 	request.getServerPort()
            + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">
</head>
<body>
<script type="text/javascript">
    document.location.href = "login.jsp";
</script>
</body>
</html>
