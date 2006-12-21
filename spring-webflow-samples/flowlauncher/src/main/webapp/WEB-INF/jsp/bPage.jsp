<%@ include file="includeTop.jsp" %>

<div id="content">
	Sample B Flow
	<hr>
	Flow input was: ${input}<BR>
	<c:if test="${!flowExecutionContext.activeSession.root}">
	<br>
	Sample B is now running as a sub flow within Sample A.  This means we can end Sample B and
	return to the parent flow.  We can do this using either:
	<ul>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="<c:url value="/flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=end"/>">
				End Sample B
			</a>
		</td>
	</tr>
	<tr>
		<td valign="top">or a form:</td>
		<td>
			<table>
			<form action="flowController.htm" method="post">
			<tr>
				<td class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
					<input type="submit" name="_eventId_end" value="End Sample B">
				</td>
			</tr>
			</form>
			</table>
		</td>
	</tr>
	</table>
	</ul>
	</c:if>
	<hr>
	<form action="<c:url value="/index.html"/>">
		<input type="submit" value="Home">
	</form>
</div>

<%@ include file="includeBottom.jsp" %>