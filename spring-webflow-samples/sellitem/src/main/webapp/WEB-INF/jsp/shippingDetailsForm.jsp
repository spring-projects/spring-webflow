<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title>Sell Item - Enter Shipping Information</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Enter shipping information</h2>
	<hr>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td>${sale.price}</td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td>${sale.itemCount}</td>
	</tr>
	<tr class="readOnly">
		<td>Category:</td><td>${sale.category}</td>
	<tr class="readOnly">
		<td>Shipping:</td><td>${sale.shipping}</td>
	</tr>
	<form:form commandName="sale" method="post">
		<tr>
			<td>Shipping type:</td>
			<td>
				<spring:bind path="shippingType">
					<select name="${status.expression}">
						<option value="S" <c:if test="${status.value=='S'}">selected</c:if>>
							Standard (10 extra cost)
						</option>
						<option value="E" <c:if test="${status.value=='E'}">selected</c:if>>
							Express (20 extra cost)
						</option>
					</select>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td>Ship date (DD/MM/YYYY):</td>
			<td>
				<form:input path="shipDate" />
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