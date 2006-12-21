<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form action="rates.htm" method="post" id="selectCustomerTypeForm">

	<spring:nestedPath path="rateCriteria">

	<spring:bind path="residential">
		<c:if test="${status.error}">
			<span class="error">${status.errorMessage}</span>
		</c:if>
	</spring:bind>

	<fieldset>
		<legend>Select your customer profile</legend>

		<label>What's most appropriate for you?</label><br>

		<spring:bind path="residential">
			<label>I'm a private person</label>
			<input type="radio" name="${status.expression}" value="true" <c:if test="${status.value == true}">checked</c:if>><br>
		</spring:bind>
		
		<spring:bind path="residential">
			<label>We're a business, institution or government agency</label>
			<input type="radio" name="${status.expression}" value="false" <c:if test="${status.value == false}">checked</c:if>><br>
		</spring:bind>

		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
		<input type="submit" value="Next" name="_eventId_submit">
	</fieldset>

	</spring:nestedPath>

	<script type="text/javascript">
		formRequest('selectCustomerTypeForm');
	</script>

</form>