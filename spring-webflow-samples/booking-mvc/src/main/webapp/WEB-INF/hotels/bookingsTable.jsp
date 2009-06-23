<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<div id="bookings" "class="section">
<security:authorize ifAllGranted="ROLE_USER">
	<h2>Current Hotel Bookings</h2>

	<c:if test="${empty bookingList}">
	<tr>
		<td colspan="7">No bookings found</td>
	</tr>
	</c:if>

	<c:if test="${!empty bookingList}">
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
			<c:forEach var="booking" items="${bookingList}">
			<tr>
				<td>${booking.hotel.name}</td>
				<td>${booking.hotel.address}</td>
				<td>${booking.hotel.city}, ${booking.hotel.state}</td>
				<td>${booking.checkinDate}</td>
				<td>${booking.checkoutDate}</td>
				<td>${booking.id}</td>
				<td>
					<a id="cancelLink_${booking.id}" href="deleteBooking?id=${booking.id}">Cancel</a>
					<script type="text/javascript">
						Spring.addDecoration(new Spring.AjaxEventDecoration({
							elementId:"cancelLink_${booking.id}",
							event:"onclick",
							params: {fragments:"bookingsTable"}
						}));
					</script>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
</security:authorize>

</div>