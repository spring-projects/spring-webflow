<%@ include file="includeTop.jsp" %>

<div id="content">
	Sample A Flow
	<hr>
	Flow input was: ${input}<br>
	<br>
	From Sample A you may terminate Sample A and launch Sample B from an end state of Sample A.
	You may also pass Sample B input.  This can be done using:
	<ul>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="<c:url value="/flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=end-A-and-launch-B&input=someInputForSampleB"/>">
				End Sample A and Launch Sample B
			</a>
		</td>
	</tr>
	<tr>
		<td valign="top">or a form:</td>
		<td>
			<table>
			<form action="flowController.htm" method="post">
			<tr>
				<td>
					<input type="text" name="input" value="someInputForSampleB">
				</td>
			</tr>
			<tr>
				<td class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
					<input type="submit" name="_eventId_end-A-and-launch-B" value="End Sample A and Launch Sample B">
				</td>
			</tr>
			</form>
			</table>
		</td>
	</tr>
	</table>
	</ul>
	Alternatively, you may spawn Sample B as a sub flow of Sample A. In this case a flow
	attribute mapper maps the input stored in the FlowScope of Sample A down to the spawning subflow
	Here again you have the option of using:
	<ul>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="<c:url value="/flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=launch-B-as-subflow"/>">
				Launch Sample B as a Sub Flow
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
					<input type="submit" name="_eventId_launch-B-as-subflow" value="Launch Sample B as a Sub Flow">
				</td>
			</tr>
			</form>
			</table>
		</td>
	</tr>
	</table>
	</ul>
	Yet another option is to launch Sample B as a top-level flow without involving Sample A as:
	<ul>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="<c:url value="/flowController.htm?_flowId=sampleB&input=someInputForSampleB"/>">
				Launch Sample B
			</a>
		</td>
	</tr>
	<tr>
		<td valign="top">or a form:</td>
		<td>
			<table>
			<form action="flowController.htm" method="post">
			<tr>
				<td>
					<input type="text" name="input" value="someInputForSampleB">
				</td>
			</tr>
			<tr>
				<td class="buttonBar">
					<input type="hidden" name="_flowId" value="sampleB">
					<input type="submit" value="Launch Sample B">
				</td>
			</tr>
			</form>
			</table>
		</td>
	</tr>
	</table>
	</ul>
	<form action="<c:url value="/index.html"/>">
		<INPUT type="submit" value="Home">
	</form>
</div>

<%@ include file="includeBottom.jsp" %>