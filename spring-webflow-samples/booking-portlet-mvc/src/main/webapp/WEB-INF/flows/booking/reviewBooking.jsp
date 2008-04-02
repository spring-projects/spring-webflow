<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="section">
	<h1>Confirm Hotel Booking</h1>
</div>

<div class="section">
	<portlet:actionURL var="actionUrl">
		<portlet:param name="execution" value="${flowExecutionKey}" />
	</portlet:actionURL>
	<form:form id="confirm" modelAttribute="booking" action="${actionUrl}">
	<fieldset>
		<div class="field">
			<div class="label">Name:</div>
			<div class="output">${booking.hotel.name}</div>
		</div>
		<div class="field">
			<div class="label">Address:</div>
			<div class="output">${booking.hotel.address}</div>
		</div>
		<div class="field">
			<div class="label">City, State:</div>
			<div class="output">${booking.hotel.city}, ${booking.hotel.state}</div>
		</div>
		<div class="field">
			<div class="label">Zip:</div>
			<div class="output">${booking.hotel.zip}</div>
		</div>
		<div class="field">
			<div class="label">Country:</div>
			<div class="output">${booking.hotel.country}</div>
		</div>
        <div class="field">
            <div class="label">Total payment:</div>
            <div class="output">
            	<spring:bind path="total">${status.value}</spring:bind>
            </div>
        </div>
		<div class="field">
			<div class="label">Check In Date:</div>
			<div class="output">
				<spring:bind path="checkinDate">${status.value}</spring:bind>
			</div>
		</div>
		<div class="field">
			<div class="label">Check Out Date:</div>
			<div class="output">
				<spring:bind path="checkoutDate">${status.value}</spring:bind>
			</div>
		</div>
		<div class="field">
			<div class="label">Credit Card #:</div>
			<div class="output">${booking.creditCard}</div>
		</div>
		<div class="buttonGroup">
			<input type="submit" name="_eventId_confirm" value="Confim"/>&#160;
			<input type="submit" name="_eventId_revise" value="Revise"/>&#160;
			<input type="submit" name="_eventId_cancel" value="Cancel"/>&#160;
		</div>
	</fieldset>
	</form:form>
</div>
