<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<form:form modelAttribute="searchCriteria" action="search" method="get">
<div class="section">
    <span class="errors">
    	<form:errors path="*"/>
    </span>
	<h2>Search Hotels</h2>
	<fieldset>
		<div class="field">
			<div class="label">
				<label for="searchString">Search String:</label>
			</div>		
			<div class="input">
				<form:input id="searchString" path="searchString"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "searchString",
						widgetType : "dijit.form.ValidationTextBox",
						widgetAttrs : { promptMessage : "Search hotels by name, address, city, or zip." }}));
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="pageSize">Maximum results:</label>
			</div>
			<div class="input">	
				<form:select id="pageSize" path="pageSize">
					<form:option label="5" value="5"/>
					<form:option label="10" value="10"/>
					<form:option label="20" value="20"/>
				</form:select>
			</div>
		</div>
		<div class="buttonGroup">
			<input type="submit" value="Find Hotels" />
		</div>		
    </fieldset>
</div>
</form:form>

<security:authorize ifAllGranted="ROLE_USER">
<div class="section">
	<h2>Current Hotel Bookings</h2>

	<c:if test="${empty bookings}">
	<tr>
		<td colspan="7">No bookings found</td>
	</tr>
	</c:if>

	<c:if test="${!empty bookings}">
	<table class="summary">
		<thead>
			<tr>
				<th>Name</th>
				<th>Address</th>
				<th>City, State</th>
				<th>Check in Date</th>
				<th>Check out Date</th>
				<th>Confirmation Number</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="booking" items="${bookings}">
			<tr>
				<td>${booking.hotel.name}</td>
				<td>${booking.hotel.address}</td>
				<td>${booking.hotel.city}, ${booking.hotel.state}</td>
				<td>${booking.checkinDate}</td>
				<td>${booking.checkoutDate}</td>
				<td>${booking.id}</td>
				<td>
					<a href="deleteBooking?id=${booking.id}">Cancel</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>

</div>
</security:authorize>