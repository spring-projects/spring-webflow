<%@ include file="includeTop.jsp" %>

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
				<spring:bind path="sale.shippingType">
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

<%@ include file="includeBottom.jsp" %>