<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
  <head>
  	<title>Sell Item JSF - A Spring Web Flow Sample</title>
  </head>
  <body>
	<div align="left">Sell Item JSF - A Spring Web Flow Sample</div>
	<hr>
	<div align="left">
		<p>
			<a href="controller.faces?_flowId=sellitem-flow">Sell Item</A> (launch with a regular anchor tag)
		</p>
		<p>
			This Spring Web Flow sample application is the JSF-based version of the familiar "Sell item" sample.
			It illustrates the following concepts:
		</p>
		<ul>
			<li>Using Spring Web Flow with JSF</li>
			<li>Implementing a wizard using web flows.</li>
			<li>Use of conversation scope.</li>
			<li>Using expressions to apply dynamic flow navigation rules</li>
			<li>Using continuations to make the flow completely stable, no matter how browser navigation buttons are used.</li>
			<li>"Always redirect on pause" to benefit from the POST+REDIRECT+GET pattern with no special coding.</li>
			<li>
				Using "conversation invalidation after completion" to prevent duplicate submits of the same sale
				while taking advantage of continuations to allow back button usage while the application transaction is in process.
			</li>
		</ul>
	</div>
	<hr/>
	<div align="right"></div>
  </body>
</html>