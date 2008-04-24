<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="hotelResults" class="section">
<c:if test="${not empty hotels}">
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
					<td><a href="show?id=${hotel.id}">View Hotel</a></td>
				</tr>
			</c:forEach>
			<c:if test="${empty hotels}">
				<tr>
					<td colspan="5">No hotels found</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	<div class="buttonGroup">
		<c:if test="${searchCriteria.page > 0}">
			<a id="prevResultsLink" href="search?searchString=${searchCriteria.searchString}&pageSize=${searchCriteria.pageSize}&page=${searchCriteria.page - 1}">Previous Results</a>
			<script>
				Spring.addDecoration(new Spring.AjaxEventDecoration({
					elementId: "prevResultsLink",
					event: "onclick",
					params: {fragments: "hotelResults"}
				}));
			</script>
		</c:if>
		<c:if test="${not empty hotels}">
			<a id="moreResultsLink" href="search?searchString=${searchCriteria.searchString}&pageSize=${searchCriteria.pageSize}&page=${searchCriteria.page + 1}">More Results</a>
			<script>
				Spring.addDecoration(new Spring.AjaxEventDecoration({
					elementId: "moreResultsLink",
					event: "onclick",
					params: {fragments: "hotelResults"}		
				}));
			</script>
		</c:if>		
	</div>
</c:if>
</div>