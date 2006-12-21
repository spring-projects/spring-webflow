<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Personalized birthday cards</h2>
	<hr>
	<html:form action="flowAction" method="post">
	<table>
	<tr>
		<td colspan="2">
			<html:errors/>
		</td>
	</tr>
	<tr>
		<td>Would you like a card to be sent on your birthday?</td>
		<td>
			<html:checkbox property="sendCard"/>
			<!-- Checkboxes don't send anything when not selected, so this marker is used to detect
			     a false submission -->
			<input type="hidden" name="_sendCard" value="marker"/>
		</td>
	</tr>
	<tr>
		<td>If so, enter your email address:</td>
		<td>
			<html:text property="emailAddress" size="25" maxlength="128"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="buttonBar">
			<html:image src="images/submit.gif" property="_eventId_submit" value="Calculate Age"/>
			<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
		</td>
	</tr>
	</html:form>
	</table>
</div>

<%@ include file="includeBottom.jsp" %>