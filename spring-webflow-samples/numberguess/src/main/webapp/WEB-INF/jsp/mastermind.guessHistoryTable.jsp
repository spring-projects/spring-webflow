			<c:if test="${!empty game.guessHistory}">
				<h4>Guess history:</h4>
				<table border="1">
				    <th>Guess</th>
				    <th>Right Position</th>
				    <th>Present But Wrong Position</th>
				    <c:forEach var="guessData" items="${game.guessHistory}">
				    	<tr>
				    		<td>${guessData.guess}</td>
				    		<td>${guessData.rightPosition}</td>
				    		<td>${guessData.correctButWrongPosition}</td>
				    	</tr>
				    </c:forEach>
				</table>
			</c:if>