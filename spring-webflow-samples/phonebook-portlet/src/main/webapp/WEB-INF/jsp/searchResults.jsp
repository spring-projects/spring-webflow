<%@ include file="includeTop.jsp" %>

<%@ page import="org.springframework.webflow.samples.phonebook.Person" %>

<div id="content">
	<div id="insert">
		<img src="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/images/webflow-logo.jpg") %>"/>
	</div>
	<form action="<portlet:actionURL/>" method="post">
	<table>
		<tr>
			<td>
			    <div class="portlet-section-header">Search Results</div>
			</td>
		</tr>
		<tr>
			<td>
				<hr>
			</td>
		</tr>
		<tr>
			<td>
				<table border="1">
					<tr>
						<th>First Name</th>
						<th>Last Name</th>
						<th>User Id</th>
						<th>Phone</th>
					</tr>
					<c:forEach var="person" items="${results}">
						<tr>
							<td><c:out value="${person.firstName}"/></td>
							<td><c:out value="${person.lastName}"/></td>
							<td>
								<a href="
                                    <portlet:actionURL>
						                <portlet:param name="_flowExecutionKey" value="<%= (String)request.getAttribute("flowExecutionKey") %>" />
						            	<portlet:param name="_eventId" value="select" />
						            	<portlet:param name="id" value="<%= ((Person)pageContext.getAttribute("person")).getId().toString() %>"/>
						            </portlet:actionURL>">
							        <c:out value="${person.userId}"/>
								</a>
							</td>
							<td><c:out value="${person.phone}"/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<tr>
			<td class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="<c:out value="${flowExecutionKey}"/>">
				<input type="submit" class="button" name="_eventId_newSearch" value="New Search">
			</td>
		</tr>
	</table>
	</form>
</div>

<%@ include file="includeBottom.jsp" %>