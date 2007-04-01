<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<html>
<head>
<title>Enter your Birthdate</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="style.css" type="text/css">
</head>
<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Enter your birth date</h2>
	<hr>
	<html:form action="flowAction" method="post">
	<table>
	<tr>
		<td colspan="2">
			<html:errors/>
		</td>
	</tr>
	<tr>
		<td>Your name</td>
		<td>
			<html:text property="name" size="25" maxlength="30"/>
		</td>
	</tr>
	<tr>
		<td>Your birth date (DD-MM-YYYY)</td>
		<td>
			<html:text property="date" size="10" maxlength="10"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="buttonBar">
			<html:image src="images/submit.gif" property="_eventId_submit" value="Next"/>
			<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
		</td>
	</tr>
	</table>
	</html:form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>
</body>
</html>