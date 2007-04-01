<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Your Item List</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="<c:url value='/style.css'/>" type="text/css">
</head>

<body>

<div id="logo">
	<img src="<c:url value='/images/spring-logo.jpg'/>" alt="Logo"> 
</div>

<div id="content">
	<div id="insert">
	    <img src="<c:url value='/images/webflow-logo.jpg'/>" alt="Web Flow Logo">
	</div>
	<h2>Add a new item</h2>
	<hr>
	<!-- Tell webflow what executing flow we are participating in -->
	<form action="${flowExecutionKey}" method="post"/>
	<table>
		<tr>
			<td>
				Item:
			</td>
			<td>
				<input type="text" name="data">
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<!-- Tell webflow what event happened -->
				<input type="submit" name="_eventId_submit" value="Submit">
			</td>
		</tr>
	</table>
    </form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>