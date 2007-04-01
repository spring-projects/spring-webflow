 <%-- make sure we have a session --%>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<html>
<body>

<f:view>
	<div align="left">
		<p>
			<h:form>
			    <h:commandLink value="Sell Item" action="flowId:sellitem-flow"/>
			</h:form>
		</p>
		<p>
			This Spring Web Flow sample application is the JSF-based version of the familiar "Sell item" sample.
			It illustrates the following concepts:
		</p>
		<ul>
			<li>Using Spring Web Flow with JSF</li>
			<li>Implementing a wizard using web flows.</li>
			<li>Using continuations to make the flow completely stable, no matter how browser navigation buttons are used.</li>
			<li>
				Using "conversation invalidation after completion" to prevent duplicate submits of the same sale
				while taking advantage of continuations to allow back button usage while the application transaction is in process.				</li>
			<li>"Always redirect on pause" to benefit from the POST+REDIRECT+GET pattern with no special coding.</li>
			<li>Using <A href="http://www.ognl.org/">OGNL</A> based conditional expressions.</li>
			<li>Use of subflows to compose a multi-step business process from independently reusable modules.</li>
		</ul>
	</div>
</f:view>

</body>	
</html>