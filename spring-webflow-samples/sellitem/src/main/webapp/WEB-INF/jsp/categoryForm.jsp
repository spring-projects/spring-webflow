<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title>Sell Item - Select Category</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Select category</h2>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td>${sale.price}</td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td>${sale.itemCount}</td>
	</tr>
	<form:form commandName="sale" method="post">
		<tr>
			<td>Category:</td>
			<td>
				<spring:bind path="category">
					<select name="${status.expression}">
						<option value="" <c:if test="${status.value ==''}">selected</c:if>>
							None (0.02 discount rate)
						</option>
						<option value="A" <c:if test="${status.value =='A'}">selected</c:if>>
							Cat. A (0.1 discount rate when more than 100 items)
						</option>
						<option value="B" <c:if test="${status.value =='B'}">selected</c:if>>
							Cat. B (0.2 discount rate when more than 200 items)
						</option>
					</select>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td>Is shipping required?:</td>
			<td>
				<form:checkbox path="shipping"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
				<input type="submit" class="button" name="_eventId_submit" value="Next">
			</td>
		</tr>
	</form:form>
	</table>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>