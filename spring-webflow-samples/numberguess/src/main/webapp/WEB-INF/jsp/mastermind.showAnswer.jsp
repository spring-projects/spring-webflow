<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Show answer</h2>
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
<%@ include file="includeBottom.jsp" %>