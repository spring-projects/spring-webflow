<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
			<td>
            	<div class="portlet-section-header">Search Criteria</div>
            </td>
		</tr>
		<tr>
			<td colspan="2">
				<hr>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<form:errors cssClass="portlet-msg-error"/>
			</td>
		</tr>
		<tr>
			<td>First Name</td>
			<td>
				<form:input path="firstName" />
			</td>
		</tr>
		<tr>
			<td>Last Name</td>
			<td>
				<form:input path="lastName" />
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="<c:out value="${flowExecutionKey}"/>">
				<input type="submit" class="portlet-form-button" name="_eventId_search" value="Search">
			</td>
		</tr>
	</table>
	</form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>
</body>
</html>