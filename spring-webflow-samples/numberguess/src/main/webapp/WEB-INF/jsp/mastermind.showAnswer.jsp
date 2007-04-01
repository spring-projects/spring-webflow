<%@ page contentType="text/html" %>
<%@ page session="false" %>
<html>
<head>
	<title>Mastermind - You Guessed It!</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>You guessed it!</h2>
	<table>
		<tr>
			<td>Total number of guesses:</td>
			<td>${game.data.guesses}</td>
		</tr>
		<tr>
			<td>Elapsed time in seconds:</td>
			<td>${game.data.duration}</td>
		</tr>
		<tr>
			<td>Answer:</td>
			<td>${game.data.answer}</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<form action="play.htm">
					<input type="hidden" name="_flowId" value="mastermind">
					<input type="submit" value="Play Again!">
				</form>
			</td>
		</tr>
	</table>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>