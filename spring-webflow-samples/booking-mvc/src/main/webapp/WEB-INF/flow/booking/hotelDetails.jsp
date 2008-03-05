<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>View Hotel</h1>
</div>

<div class="section">
	<form:form id="hotel" modelAttribute="hotel">
	<fieldset>
		<div class="field">
			<div class="label">Name:</div>
			<div class="output">${hotel.name}</div>
		</div>
		<div class="field">
			<div class="label">Address:</div>
			<div class="output">${hotel.address}</div>
		</div>
		<div class="field">
			<div class="label">City:</div>
			<div class="output">${hotel.city}</div>
		</div>
		<div class="field">
			<div class="label">State:</div>
			<div class="output">${hotel.state}</div>
		</div>
		<div class="field">
			<div class="label">Zip:</div>
			<div class="output">${hotel.zip}</div>
		</div>
		<div class="field">
			<div class="label">Country:</div>
			<div class="output">${hotel.country}</div>
		</div>
	    <div class="field">
	        <div class="label">Nightly rate:</div>
	        <div class="output">
	        	<spring:bind path="price">${status.value}</spring:bind>
	        </div>
	    </div>
		<div class="buttonGroup">
			<input type="submit" name="_eventId_book" value="Book Hotel"/>&#160;
			<input type="submit" name="_eventId_cancel" value="Back to Search"/>&#160;
		</div>
	</fieldset>
	</form:form>
</div>

</tiles:putAttribute>
</tiles:insertTemplate>