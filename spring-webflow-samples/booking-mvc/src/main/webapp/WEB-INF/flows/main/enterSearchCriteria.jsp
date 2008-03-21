<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<form:form modelAttribute="searchCriteria">
<div class="section">
    <span class="errors">
    	<form:errors path="*"/>
    </span>
	<h1>Search Hotels</h1>
	<fieldset> 
		<form:input path="searchString"/>
		<label for="pageSize">Maximum results:</label>
		<form:select path="pageSize">
			<form:option label="5" value="5"/>
			<form:option label="10" value="10"/>
			<form:option label="20" value="20"/>
		</form:select>
		<input type="submit" class="button" name="_eventId_search" value="Find Hotels" />
    </fieldset>
</div>
</form:form>

<security:authorize ifAllGranted="ROLE_USER">
	<div class="section">
		<h1>Current Hotel Bookings</h1>
		
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
						<a href="${flowExecutionUrl}&_eventId=cancelBooking&bookingId=${booking.id}">Cancel</a>
					</td>
				</tr>
				</c:forEach>
				<c:if test="${empty bookings}">
				<tr>
					<td colspan="7">No booking history</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</security:authorize>

</tiles:putAttribute>
</tiles:insertTemplate>