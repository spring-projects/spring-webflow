<HTML>
	<head>
		<title>Shipping Rate - An Ajax-enabled Spring Web Flow Sample</title>
		<script src="prototype.js" type="text/javascript"></script>
		<script src="swf_ajax.js" type="text/javascript"></script>
	</head>
	<BODY>
		<DIV align="left">Shipping Rate - An Ajax-enabled Spring Web Flow Sample</DIV>
		
		<HR>
		
		<DIV align="left">			
			<P>
				This sample application demonstrates use of Spring Web Flow
				in combination with Ajaxian techniques.  Specfically, it illustrates a
				wizard embedded in a zone of this page that makes Ajax calls to the server to
				participate in a executing flow.  To complete processing, this wizard takes
				the details about a shipment and calls a service to get the shipping rate.
				<p>
				 The techniques demonstrated are:
				<UL>
					<li>
						Using a JavaScript component to submit regular forms through an AJAX request, and inserting the HTML
						received from the server into a DIV tag.
					</li>
					<LI>
						Using the "_flowId" request parameter to let the view tell the web
						flow controller which flow needs to be started.
					</LI>
					<LI>
						Implementing a wizard using Spring Web Flow.
					</LI>
				</UL>
			</P>
			<p>
			Note: this sample has been tested successfully on Internet Explorer 6 and Safari 2.0.3.  There are currently known Javascript issues with use on Firefox 1.5.
			</p>
		</DIV>
		
		<HR>

		<div id="getRateWizard">
			<script type="text/javascript">
			window.onload = function() {
				new SimpleRequest('getRateWizard', 'rates.htm', 'get', '_flowId=getRate-flow');
			};
			</script>
		</div>
		
	</BODY>
</HTML>