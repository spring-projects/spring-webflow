<%@ include file="includeTop.jsp" %>

<div id="content">

	<div id="insert">
		<img src="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/images/webflow-logo.jpg") %>"/>
	</div>
	<form action="<portlet:actionURL/>" method="post">
	<table>
		<tr>
			<td>
            	<div class="portlet-section-header">Search Criteria</div>
            </td>
		</tr>
		<tr>
			<td colspan="2">
				<hr>
			</td>
		</tr>
		<spring:hasBindErrors name="searchCriteria">
		<tr>
			<td colspan="2">
				<div class="portlet-msg-error">Please provide valid search criteria!</div>
			</td>
		</tr>
		</spring:hasBindErrors>
		<spring:bind path="searchCriteria.firstName">
		<tr>
			<td>First Name</td>
			<td>
				<input type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>">
			</td>
		</tr>
		</spring:bind>		
		<spring:bind path="searchCriteria.lastName">
		<TR>
			<td>Last Name</td>
			<td>
				<input type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>">
			</td>
		</TR>
		</spring:bind>
		<tr>
			<td colspan="2">
				<hr>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="<c:out value="${flowExecutionKey}"/>">
				<input type="submit" class="portlet-form-button" name="_eventId_search" value="Search">
			</td>
		</tr>
	</table>
	</form>
</div>

<%@ include file="includeBottom.jsp" %>