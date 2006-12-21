<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert">
		<img src="images/webflow-logo.jpg"/>
	</div>
	<form action="phonebook.htm" method="post">
	<table>
		<tr>
			<td>Person Details</td>
		</tr>
		<tr>
			<td colpan="2"><hr></td>
		</tr>
		<tr>
			<td><b>First Name</b></td>
			<td>${person.firstName}</td>
		</tr>
		<tr>
			<td><b>Last Name</b></td>
			<td>${person.lastName}</td>
		</tr>
		<tr>
			<td><b>User Id</B></td>
			<td>${person.userId}</td>
		</tr>
		<tr>
			<td><b>Phone</b></td>
			<td>${person.phone}</td>
		</tr>
		<tr>
			<td colspan="2">
				<br>
				<b>Colleagues:</b>
				<br>
				<c:forEach var="colleague" items="${person.colleagues}">
					<a href="phonebook.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=select&id=${colleague.id}">
						${colleague.firstName} ${colleague.lastName}<br>
					</a>
				</c:forEach>				
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
				<input type="submit" class="button" name="_eventId_back" value="Back">
			</td>
		</tr>
	</table>
	</form>
</DIV>