<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ page import="org.springframework.webflow.samples.phonebook.Person" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<portlet:defineObjects/>

<html>
<head>
<title>Enter Search Criteria</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "style.css") %>" type="text/css">
</head>
<body>

<div id="logo">
	<img src="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/images/spring-logo.jpg") %>" height="73" alt="Logo" border="0"> 
</div>

<div id="content">
	
	<div id="insert">
		<img src="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/images/webflow-logo.jpg") %>"/>
	</div>
	
	<form action="<portlet:actionURL/>" method="post">
	<table>
		<tr>
			<td class="portlet-section-subheader">Person Details</td>
		</tr>
		<tr>
			<td colspan="2"><hr></td>
		</tr>
		<tr>
			<td><b>First Name</b></td>
			<td><c:out value="${person.firstName}"/></td>
		</tr>
		<tr>
			<td><b>Last Name</b></td>
			<td><c:out value="${person.lastName}"/></td>
		</tr>
		<tr>
			<td><b>User Id</B></td>
			<td><c:out value="${person.userId}"/></td>
		</tr>
		<tr>
			<td><b>Phone</b></td>
			<td><c:out value="${person.phone}"/></td>
		</tr>
		<tr>
			<td colspan="2">
				<br>
				<b>Colleagues:</b>
				<br>
				<c:forEach var="colleague" items="${person.colleagues}">
					<a href="
                    	<portlet:actionURL>
							<portlet:param name="_flowExecutionKey" value="<%= (String)request.getAttribute("flowExecutionKey") %>" />
						    <portlet:param name="_eventId" value="select" />
						    <portlet:param name="id" value="<%= ((Person)pageContext.getAttribute("colleague")).getId().toString() %>"/>
						</portlet:actionURL>">
  			            <c:out value="${colleague.firstName}"/> <c:out value="${colleague.lastName}"/><br>
					</a>
				</c:forEach>				
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="<c:out value="${flowExecutionKey}"/>">
				<input type="submit" class="button" name="_eventId_back" value="Back">
			</td>
		</tr>
	</table>
	</form>
</DIV>