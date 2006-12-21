<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Enter price and item count</h2>
	<hr>
	<table>
		<form:form commandName="sale" method="post">
		<tr>
			<td>Price:</td>
			<td><form:input path="price" /></td><td><form:errors path="price" /></td>
		</tr>		
		<tr>
			<td>Item count:</td>
			<td><form:input path="itemCount" /></td><td><form:errors path="itemCount" /></td>
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