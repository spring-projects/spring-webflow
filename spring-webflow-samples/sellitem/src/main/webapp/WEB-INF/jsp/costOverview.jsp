<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
	<title>Sell Item - Order Summary</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Purchase cost overview</h2>
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
	</tr>
	<tr class="readOnly">
		<td valign="top">Shipping Info:</td>
		<td>
			<c:choose>
				<c:when test="${sale.shipping}">
					<table>
						<tr class="readOnly">
							<td>Type:</td>
							<td>${sale.shippingType}</td>
						</tr>
						<tr class="readOnly">
							<td>Date:</td>
							<td>
								<spring:bind path="sale.shipDate">
								${status.value}
								</spring:bind>
							</td>
						</tr>
					</table>
				</c:when>
				<c:otherwise>
					No shipping required: you're picking up the items
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td colspan="2"></td>
	</tr>
	<tr>
		<td>Base amount:</td><td>${sale.amount}</td>
	</tr>
	<tr>
		<td>Delivery cost:</td><td>${sale.deliveryCost}</td>
	</tr>
	<tr>
		<td>Discount:</td><td>${sale.savings} (Discount rate: ${sale.discountRate})</td>
	</tr>
	<tr>
		<td colspan="2"><hr></td>
	</tr>
	<tr>
		<td><b>Total cost</b>:</td><td>${sale.totalCost}</td>
	</tr>
	<tr>
		<td colspan="2" class="buttonBar">
			<form action="<c:url value="/index.jsp"/>">
				<input type="submit" class="button" value="Home">
			</form>
		</td>
	</tr>
	</table>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>