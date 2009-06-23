<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<h2>Hotel Results</h2>

<table class="summary">
	<thead>
		<tr>
			<th>Name</th>
			<th>Address</th>
			<th>City, State</th>
			<th>Zip</th>
			<th>Action</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="hotel" items="${hotels}">
			<tr>
				<td>${hotel.name}</td>
				<td>${hotel.address}</td>
				<td>${hotel.city}, ${hotel.state}, ${hotel.country}</td>
				<td>${hotel.zip}</td>
				<td>
					<portlet:actionURL var="actionUrl">
						<portlet:param name="execution" value="${flowExecutionKey}" />
						<portlet:param name="_eventId" value="select" />
						<portlet:param name="hotelId" value="${hotel.id}" />
					</portlet:actionURL>
					<a href="${actionUrl}">View Hotel</a>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${empty hotels}">
		<tr>
			<td colspan="5">No hotels found</td>
		</tr>
		</c:if>
	</tbody>
</table>
