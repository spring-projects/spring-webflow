<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form action="rates.htm" method="post" id="selectPackageDetailsForm">

	<spring:nestedPath path="rateCriteria">

	<fieldset>
		<legend>Select package details</legend>

		<spring:bind path="packageType">
			<label>Package type</label>
			<select name="${status.expression}">
				<option value="-1">-- Select package type</option>
				<c:forEach items="${packageTypes}" var="packageType">
					<option value="${packageType.key}" <c:if test="${status.value == packageType.key}">selected</c:if>>${packageType.value}</option>
				</c:forEach>
			</select>
			<c:if test="${status.error}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
			<br>
		</spring:bind>

		<spring:bind path="packageWeight">
			<label>Package weight</label>
			<input type="text" name="${status.expression}" value="${status.value}">
			<c:if test="${status.error}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
			<br>
		</spring:bind>

		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
		<input type="submit" value="Next" name="_eventId_submit">
	</fieldset>

	</spring:nestedPath>
	
	<script type="text/javascript">
		formRequest('selectPackageDetailsForm');
	</script>

</form>