<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert">
		<img src="images/webflow-logo.jpg"/>
	</div>
	<h2>Select the file to upload</h2>
	<hr>
	<table>
		<c:if test="${fileUploaded}">
			<p>File uploaded succesfully.</p>
			<c:if test="${!empty(file)}">
				<pre style="border: solid 1px;">${file}</pre>
			</c:if>
		</c:if>
		<form name="submitForm" method="post" enctype="multipart/form-data">
		<tr>
			<td>
				File:
			</td>
			<td>
				<input type="file" name="file">
			</td>
		</tr>
		<tr>
			<td colspan="2"> </td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
				<input type="submit" class="button" name="_eventId_submit" value="Upload">
			</td>
		</tr>
		</form>
	</table>
</div>

<%@ include file="includeBottom.jsp" %>