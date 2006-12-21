<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert">
	    <img src="<c:url value='/images/webflow-logo.jpg'/>" alt="Web Flow Logo">
	</div>
	<h2>Add a new item</h2>
	<hr>
	<!-- Tell webflow what executing flow we are participating in -->
	<form action="${flowExecutionKey}" method="post"/>
	<table>
		<tr>
			<td>
				Item:
			</td>
			<td>
				<input type="text" name="data">
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<!-- Tell webflow what event happened -->
				<input type="submit" name="_eventId_submit" value="Submit">
			</td>
		</tr>
	</table>
    </form>
</div>

<%@ include file="includeBottom.jsp" %>