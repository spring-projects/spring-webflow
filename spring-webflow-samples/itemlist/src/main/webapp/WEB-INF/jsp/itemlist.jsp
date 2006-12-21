<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert">
	    <img src="<c:url value='/images/webflow-logo.jpg'/>" alt="Web Flow Logo">
	</div>
	<h2>Your item list</h2>
	<hr>
    <!-- Tell webflow what executing flow we're participating in -->
	<form action="${flowExecutionKey}" method="post"/>
	<table>
		<tr>
			<td>
				<table border="1" width="300px">
					<c:forEach var="item" items="${list}">
						<tr><td>${item}</td></tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<tr>
			<td class="buttonBar">
				<!-- Tell webflow what event happened -->
				<input type="submit" name="_eventId_add" value="Add New Item">
			</td>
		</tr>
	</table>
    </form>
</div>

<%@ include file="includeBottom.jsp" %>