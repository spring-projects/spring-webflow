<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/template.jsp">
<tiles:putAttribute name="content">

<form:form modelAttribute="searchCriteria">
<div class="section">
    <span class="errors">
    	<form:errors/>
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
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}" />
		<input type="submit" class="button" name="_eventId_findHotels" value="Find Hotels" style="width: 165px; height: 15px;" />
    </fieldset>
</div>

<div class="section">
	<c:if test="#{hotels.empty == 0}">
		No Hotels Found
	</c:if>
	<table>
		<thead>
			<tr>
				<th>Name</th>
				<th>Address</th>
				<th>City, State</th>
				<th>Zip</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="hotel" items="#{hotels}">
			<tr>
				<td>#{hotel.name}</td>
				<td>#{hotel.address}</td>
				<td>#{hotel.city}, #{hotel.state}, #{hotel.country}</td>
				<td>#{hotel.zip}</td>
				<td>
					<c:url var="viewHotelLink">
						<c:param name="hotelId" value="#{hotel.id}"/>
						<c:param name="_flowExecutionKey" value="#{flowExecutionKey}" />
					</c:url>
					<a href="#{viewHotelLink}">View Hotel</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<div class="section">
	<h1>Current Hotel Bookings</h1>
</div>

<div class="section">
	<c:if test="#{bookings.empty}">
		No Bookings Found
	</c:if>

	<table>
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
			<c:forEach var="booking" items="#{bookings}">
			<tr>
				<td>#{booking.hotel.name}</td>
				<td>#{booking.hotel.address}</td>
				<td>#{booking.hotel.city}, #{booking.hotel.state}</td>
				<td>#{booking.checkinDate}</td>
				<td>#{booking.checkoutDate}</td>
				<td>#{booking.id}</td>
				<td>
					<c:url var="cancelBookingLink">
						<c:param name="bookingId" value="#{booking.id}"/>
						<c:param name="_flowExecutionKey" value="#{flowExecutionKey}" />
					</c:url>
					<a href="#{cancelBookingLink}">Cancel</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</form:form>

</tiles:putAttribute>
</tiles:insertTemplate>