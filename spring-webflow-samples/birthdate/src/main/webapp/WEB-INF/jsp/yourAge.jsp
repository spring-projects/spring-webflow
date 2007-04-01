<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
<title>Birthdate - Your Age</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="style.css" type="text/css">
</head>
<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>
<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Your age</h2>
	<hr>
	<p>
		${birthDate.name}, you are now <I>${age}</I> old.
		You were born on <fmt:formatDate value="${birthDate.date}" pattern="dd-MM-yyyy"/>.
	</p>
	<hr>
	<form action="index.jsp">
		<INPUT type="submit" value="Home">
	</form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>
</body>
</html>