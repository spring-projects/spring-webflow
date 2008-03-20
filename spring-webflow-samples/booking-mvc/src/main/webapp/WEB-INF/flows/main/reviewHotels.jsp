<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>Hotel Results</h1>
</div>

<c:if test="${not empty hotels}">
<div class="section">
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
					<a href="${flowExecutionUrl}&_eventId=selectHotel&hotelId=${hotel.id}">View Hotel</a>
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
</div>
</c:if>

</tiles:putAttribute>
</tiles:insertTemplate>