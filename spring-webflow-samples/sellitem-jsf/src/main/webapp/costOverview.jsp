<%@ include file="includeTop.jsp" %>

<f:view>

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
	<tr class="readOnly">
		<td>Shipping:</td>
		<c:choose>
			<c:when test="${sale.shipping}">
				<td>${sale.shippingType}</td>
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
			<form action="<c:url value="/index.jsf"/>">
				<input type="submit" class="button" value="Home">
			</form>
		</td>
	</tr>
	</table>
	</div>

</f:view>

<%@ include file="includeBottom.jsp" %>
