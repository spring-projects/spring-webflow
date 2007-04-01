<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>HigherLower - Enter Guess</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="content">
	Sample B Flow
	<hr>
	Flow input was: ${input}
	<c:if test="${!flowExecutionContext.activeSession.root}">
	<p>
		Sample B is now running as a sub flow within Sample A.  This means we can end Sample B and
		return to the parent flow.  We can do this using either:
	</p>
	<table>
		<tr>
			<td>an anchor:</td>
			<td>
				<a href="flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=end">
					End Sample B
				</a>
			</td>
		</tr>
		<tr>
			<td valign="top">or a form:</td>
			<td>
				<form action="flowController.htm" method="post">
				<table>
				<tr>
					<td class="buttonBar">
						<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
						<input type="submit" name="_eventId_end" value="End Sample B">
					</td>
				</tr>
				</table>
				</form>
			</td>
		</tr>
	</table>
	</c:if>
	<hr>
	<form action="index.html">
		<input type="submit" value="Home">
	</form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>