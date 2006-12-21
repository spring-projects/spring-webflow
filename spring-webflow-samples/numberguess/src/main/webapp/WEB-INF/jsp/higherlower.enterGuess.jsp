<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>The Number Guess Game</h2>
	<h3>Guess a number between 0 and 100!</h3>
	<hr>
	<form name="guessForm" method="post">
		<table>
			<tr class="readOnly">
				<td>Number of guesses:</td>
				<td><b>${game.guesses}</b></td>
			</tr>
			<tr class="readOnly">
				<td>Your last guess was:</td>
				<td><b><i>${game.result}</i></b></td>
			</tr>
		    <tr>
		    	<td>Guess:</td>
		    	<td>
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

<%@ include file="includeBottom.jsp" %>