<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<html>
<head>
<title>Sell Item - Enter Shipping Information</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="style.css" type="text/css">
</head>
<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo" border="0"> 
</div>

<f:view>
<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Purchase cost overview</h2>
	<hr>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td><h:outputText value="#{sale.price}"/></td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td><h:outputText value="#{sale.itemCount}"/></td>
	</tr>
	<tr class="readOnly">
		<td>Category:</td><td><h:outputText value="#{sale.category}"/></td>
	<tr class="readOnly">
		<td>Shipping:</td>
		<c:choose>
			<c:when test="${sale.shipping}">
				<td><h:outputText value="#{sale.shippingType}"/></td>
			</c:when>
			<c:otherwise>
				<td>No shipping required: you're picking up the items</td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td colspan="2"></td>
	</tr>
	<tr>
		<td>Base amount:</td><td><h:outputText value="#{sale.amount}"/></td>
	</tr>
	<tr>
		<td>Delivery cost:</td><td><h:outputText value="#{sale.deliveryCost}"/></td>
	</tr>
	<tr>
		<td>Discount:</td><td><h:outputText value="#{sale.savings}"/> (Discount rate: <h:outputText value="#{sale.discountRate}"/>)</td>
	</tr>
	<tr>
		<td colspan="2"><hr></td>
	</tr>
	<tr>
		<td><b>Total cost</b>:</td><td><h:outputText value="#{sale.totalCost}"/></td>
	</tr>
	<tr>
		<td colspan="2" class="buttonBar">
			<form action="<c:url value="/index.faces"/>">
				<input type="submit" class="button" value="Home">
			</form>
		</td>
	</tr>
	</table>
</div>
</f:view>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>
</body>
</html>