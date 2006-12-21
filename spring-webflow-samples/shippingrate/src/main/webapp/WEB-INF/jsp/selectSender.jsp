<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form action="rates.htm" method="post" id="selectSenderForm">

	<spring:nestedPath path="rateCriteria">

	<fieldset>
		<legend>Select sender location</legend>
		<spring:bind path="senderCountryCode">
			<label>Country</label>
			<select name="${status.expression}">
				<option value="null">-- Select country</option>
				<c:forEach items="${countries}" var="country">
					<option value="${country.key}" <c:if test="${status.value == country.key}">selected</c:if>>${country.value}</option>
				</c:forEach>
			</select>
			<c:if test="${status.error}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
			<br>
		</spring:bind>
		<spring:bind path="senderZipCode">
			<label>Zip code</label>
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
		formRequest('selectSenderForm');
	</script>

</form>