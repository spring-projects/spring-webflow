<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Mastermind - Enter Guess</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Mastermind</h2>
	<h3>Guess a four digit number!</h3>
	<hr>
	<p>Note: each guess must be 4 unique digits!</p>
	<p>Number of guesses so far: ${game.data.guesses}</p>

	<c:if test="${!empty game.guessHistory}">
		<h4>Guess history:</h4>
		<table border="1">
			<tr>
				<th>Guess</th>
				<th>Right Position</th>
				<th>Present But Wrong Position</th>
			</tr>
			<c:forEach var="guessData" items="${game.guessHistory}">
			<tr>
				<td>${guessData.guess}</td>
				<td>${guessData.rightPosition}</td>
			    <td>${guessData.correctButWrongPosition}</td>
			</tr>
			</c:forEach>
		</table>
	</c:if>		
	
	<form name="guessForm" method="post">
		<c:if test="${game.result == 'INVALID'}">
			<div class="error">Your guess was invalid: it must be a 4 digit number (e.g 1234), and each digit must be unique.</div>
		</c:if>
		<table>
		    <tr>
		    	<td>Guess:</td>
		    	<td align="left">
		    		<input type="text" name="guess" value="${param.guess}">
		    	</td>
		    </tr>
			<tr>
				<td colspan="2" class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
					<input type="submit" class="button" name="_eventId_submit" value="Guess">
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