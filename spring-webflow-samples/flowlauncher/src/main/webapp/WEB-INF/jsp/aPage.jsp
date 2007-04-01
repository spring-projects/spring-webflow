<%@ page contentType="text/html" %>
<%@ page session="false" %>
<html>
<head>
	<title>Sample A Flow</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="style.css" type="text/css">
</head>

<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo"> 
</div>

<div id="content">
	Sample A Flow
	<hr>
	Flow input was: ${input}
	<p>
		From Sample A you may terminate Sample A and launch Sample B from an end state of Sample A.
		You may also pass Sample B input.  This can be done using:
	</p>
	<table>
		<tr>
			<td>an anchor:</td>
			<td>
				<a href="flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=end-A-and-launch-B&input=someInputForSampleB">
					End Sample A and Launch Sample B
				</a>
			</td>
		</tr>
		<tr>
			<td valign="top">or a form:</td>
			<td>
				<form action="flowController.htm" method="post">
				<table>
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
				</table>
				</form>
			</td>
		</tr>
	</table>		
	<p>
		Alternatively, you may spawn Sample B as a sub flow of Sample A. In this case a flow
		attribute mapper maps the input stored in the FlowScope of Sample A down to the spawning subflow
		Here again you have the option of using:
	</p>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="flowController.htm?_flowExecutionKey=${flowExecutionKey}&_eventId=launch-B-as-subflow">
				Launch Sample B as a Sub Flow
			</a>
		</td>
	</tr>
	<tr>
		<td valign="top">or a form:</td>
		<td>
			<form action="flowController.htm" method="post">
			<table>
			<tr>
				<td class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
					<input type="submit" name="_eventId_launch-B-as-subflow" value="Launch Sample B as a Sub Flow">
				</td>
			</tr>
			</table>
			</form>
		</td>
	</tr>
	</table>
	<p>
		Yet another option is to launch Sample B as a top-level flow without involving Sample A as:
	</p>
	<table>
	<tr>
		<td>an anchor:</td>
		<td>
			<a href="flowController.htm?_flowId=sampleB&input=someInputForSampleB">
				Launch Sample B
			</a>
		</td>
	</tr>
	<tr>
		<td valign="top">or a form:</td>
		<td>
			<form action="flowController.htm" method="post">
			<table>
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
			</table>
			</form>
		</td>
	</tr>
	</table>
	<form action="index.html">
		<INPUT type="submit" value="Home">
	</form>
</div>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>

</body>
</html>