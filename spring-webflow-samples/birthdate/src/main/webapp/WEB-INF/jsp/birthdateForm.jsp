<%@ include file="includeTop.jsp" %>

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
	</html:form>
	</table>
</div>

<%@ include file="includeBottom.jsp" %>