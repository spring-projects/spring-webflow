<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Mastermind</h2>
	<h3>Guess a four digit number!</h3>
	<hr>
	<p>Note: each guess must be 4 unique digits!</p>
	<p>Number of guesses so far: ${game.data.guesses}</p>

	<%@include file="mastermind.guessHistoryTable.jsp" %>
			
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

<%@ include file="includeBottom.jsp" %>