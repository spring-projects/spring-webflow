<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Upload a File</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert">
		<img src="images/webflow-logo.jpg"/>
	</div>
	<h2>Select the file to upload</h2>
	<hr>
	<form name="submitForm" method="post" enctype="multipart/form-data">
	<table>
		<c:if test="${fileUploaded}">
			<p>File uploaded succesfully.</p>
			<c:if test="${!empty(file)}">
				<pre style="border: solid 1px;">${file}</pre>
			</c:if>
		</c:if>
		<tr>
			<td>
				File:
			</td>
			<td>
				<input type="file" name="file">
			</td>
		</tr>
		<tr>
			<td colspan="2"> </td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
				<input type="submit" class="button" name="_eventId_submit" value="Upload">
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