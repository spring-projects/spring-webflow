<%-- make sure we have a session --%>
<%@ page session="true" %>
<html>
<body>
	<div align="left">Sell Item - A Spring Web Flow Sample</div>
	<hr>
	<div align="left">
		<p>
			<a href="pos.htm?_flowId=sellitem-flow">Sell Item</A> (uses flow scope storage for 'undo' back button behavior)
		</p>
		<p>
			<a href="pos.htm?_flowId=sellitem-conversation-scope-flow">Sell Item 'Conversation' Alternate</A> (uses conversation scope storage for 'preserve' back button behavior)
		</p>
		<p>
			<a href="pos.htm?_flowId=sellitem-simple-flow">Sell Item 'Simple' Alternate</A> (a single top-level flow with no subflow)
		</p>
		<p>
			This Spring Web Flow sample application implements the example application
			discussed in the article <a href="http://www-128.ibm.com/developerworks/java/library/j-contin.html">
			Use continuations to develop complex Web applications</a>.  It illustrates the following concepts:
		</p>
		<ul>
			<li>Using the "_flowId" request parameter to let the view tell the web flow controller which flow needs to be started.</li>
			<li>Implementing a wizard using web flows.</li>
			<li>
				Use of the FormAction to perform form processing, including the FormAction's "setupForm" method to install custom	
				property editors for formatting text field values (shipDate).
			</li>
			<li>Using continuations to make the flow completely stable, no matter how browser navigation buttons are used.</li>
			<li>
				Using "conversation invalidation after completion" to prevent duplicate submits of the same sale
				while taking advantage of continuations to allow back button usage while the application transaction is in process.
			</li>
			<li>"Always redirect on pause" to benefit from the POST+REDIRECT+GET pattern with no special coding.</li>
			<li>Using <A href="http://www.ognl.org/">OGNL</A> based conditional expressions.</li>
			<li>Use of subflows to compose a multi-step business process from independently reusable modules.</li>
		</ul>
	</div>
	<hr/>
	<div align="right"></div>
</html>