<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Getting Started with Spring Web Flow: Creating Your First Web Flow</title>
</head>
<body>
<h1>
	Creating Your First Web Flow
</h1>
<h2>
	How are flows authored?
</h2>
<p>
	Flow definitions are typically authored in XML documents.
	A flow is typically packaged in its own directory inside /WEB-INF, and co-located with its dependent resources such as page templates and message resources.
	For example, the tutorial flow you are using right now lives in the /WEB-INF/tutorial directory along with its JSP page templates.
</p>
<h2>
	Why XML?
</h2>
<p>
	XML is a good format for expressing structure.
	Since a flow definition primarily captures the navigation structure between your pages, XML is a good fit.
	XML is not an appropriate for general programming.
	This is why your flows should invoke actions written in Java or Groovy to carry out application behaviors.
</p>
<h2>
	Step-by-step instructions for creating your first flow using the Eclipse-based SpringSource Tool Suite IDE:
</h2>
<h3>Setup the project development environment</h3>
<ol>
	<li>
		Download <a href="http://www.springsource.com/products/sts">SpringSource Tool Suite</a> (STS) and the latest <a href="http://www.springsource.org/download#webflow">Spring Web Flow 2.0.x release</a>.
		After downloading completes, extract both archives to your directory of choice.
	</li>
	<li>
		Open STS and access File -&gt; Import... -&gt; Existing Projects Into Workspace.  Select Browse... and navigate to where you extracted the Spring Web Flow release.
		Select the spring-webflow-samples/getting-started-with-spring-webflow folder and import the project.
	</li>
	<li>
		After the project import, if you do not already have a servlet container such as Tomcat installed on your workstation you can uses STS to install an embedded Tomcat instance.
		To do this, select the SpringSource logo on the tool bar, access the Configuration tab, and select Create Server Instance.
	</li>
	<li>
		Next, right-click on the project in your Package Explorer view, and select Run On Server.
		Select the server runtime you wish to deploy to and finish to deploy the project.
		You should see the tutorial welcome page appear in the embedded web browser.
		Alternatively, you can access the application in an external browser such as Firefox at <a href="http://localhost:8080/getting-started-with-spring-webflow">http://localhost:8080/getting-started-with-spring-webflow</a>
	</li>
</ol>
<h3>Create your first helloworld flow</h3>
<ol>
	<li>
		Once the application is running, create a new directory for your flow inside /WEB-INF; name the directory helloworld.
		Next, right-click on the directory and access New -&gt; Spring Web Flow Definition.
		Enter the filename helloworld-flow.xml and finish.
		The flow definition will be generated for you with an initial view-state named start.
	</li>
	<li>
		Go ahead and create a start.jsp in your flow directory and paste in the following:
		<pre>
	&lt;html&gt;
		&lt;head&gt;
			&lt;title&gt;Hello world!&lt;/title&gt;
		&lt;/head&gt;
		&lt;h1&gt;
			Hello world!
		&lt;/h1&gt;
	&lt;/html&gt;
		</pre>
	</li>
	<li>
		From the Servers view, restart your server, then startup your flow by accessing <a href="http://localhost:8080/app/helloworld">http://localhost:8080/app/helloworld</a>.
		You should see your Hello world! message display.
		Note you only have to restart your server when you add new flows to the system.  When you change flows, changes will be refreshed automatically.
	</li>
</ol>
<h3>Add a navigation rule</h3>
<ol>
	<li>
		Next, try transitioning your flow from one state to another to implement a navigation rule.  In your helloworld flow, add the following transition to your start view-state:
		<pre>
	&lt;transition on="submit" to="page2" /&gt;
		</pre>
		Then define your page2 view-state:
		<pre>
	&lt;view-state id="page2"&gt;
	&lt;/view-state&gt;
		</pre>
		And the corresponding page2.jsp:
		<pre>
	&lt;html&gt;
		&lt;head&gt;
			&lt;title&gt;Hello world!&lt;/title&gt;
		&lt;/head&gt;
		&lt;h1&gt;
			This is page 2!
		&lt;/h1&gt;
	&lt;/html&gt;
		</pre>
		Finally, create a button on your start.jsp that raises the submit event to trigger the state transition:
		<pre>
	&lt;form method="post"&gt;
		&lt;input type="submit" name="_eventId_submit" value="Submit" /&gt;
	&lt;/form&gt;
		</pre>
		Click the button and you should be taken to page 2.
	</li>
</ol>
<h3>Add a dynamic navigation rule</h3>
<p>
	Web flow excels at implementing dynamic navigation logic that takes a user through different paths based on what they enter or who they are.
</p>
<ol>
	<li>
		Try implementing a dynamic navigation rule by adding a bound checkbox to your start.jsp.
		Do this by first replacing the contents of your start.jsp with the following snippet:
		<pre>
	&lt;%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %&gt;
	&lt;html&gt;
		&lt;head&gt;
			&lt;title&gt;Hello world!&lt;/title&gt;
		&lt;/head&gt;
		&lt;h1&gt;
			Hello world!
		&lt;/h1&gt;
		&lt;form:form method="post" modelAttribute="helloWorldForm" &gt;
			&lt;form:checkbox path="selected" /&gt;
			&lt;input type="submit" name="_eventId_submit" value="Submit" /&gt;
		&lt;/form:form&gt;		
	&lt;/html&gt;
		</pre>
	</li>
	<li>
		Next, create a HelloWorldForm class in the org.springframework.webflow.samples.helloworld package with the following code:
		<pre>
	package org.springframework.webflow.samples.helloworld;
	
	import java.io.Serializable;
	
	public class HelloworldForm implements Serializable {
	    private boolean selected = true;
	    
	    public boolean isSelected() {
	        return selected;
	    }
	    
	    public void setSelected(boolean selected) {
	        this.selected = selected;
	    }
	}
		</pre>
	</li>
	<li>
		In your helloworld-flow, declare the HelloWorldForm as a flow variable:
		<pre>
	&lt;var name="helloWorldForm" class="org.springframework.webflow.samples.helloworld.HelloWorldForm" /&gt;
		</pre>
		Then update your start view-state to use this variable as its data model, enabling automatic model binding and validation:
		<pre>
	&lt;view-state id="start" model="helloWorldForm"&gt; ...	
		</pre>
		<p>
			Refresh your flow and the checkbox should render checked since the default value for HelloWorldForm.selected is true.
			Uncheck the box and submit and go back in your browser and the unchecked status should be preserved.
		</p>
	</li>
	<li>
		Add a navigation decision that says if selected goto page2 else goto a new page3:
		<pre>
	&lt;view-state id="start"&gt;
		&lt;transition on="submit" to="isSelected" /&gt;
	&lt;/view-state&gt;
	
	&lt;decision-state id="isSelected"&gt;
		&lt;if test="helloWorldForm.selected" then="page2" else="page3" /&gt;
	&lt;/decision-state&gt;

	...
    
	&lt;view-state id="page3"&gt;
	&lt;/view-state&gt;	

		</pre>
		Click the button with the checkbox selected and you should be taken to page2.
		Click the button with the checkbox de-selected and you should be taken to page3 (you'll need to create a JSP or you'll get a 404).
	</li>
</ol>
<h3>Finish your helloworld flow</h3>
<ol>
	<li>
		Finish up your helloworld flow by adding another button on the start.jsp that ends the flow:
		<pre>
	&lt;input type="submit" name="_eventId_finish" value="Finish" /&gt;
		</pre>
		In your start view-state, declare the finish transition:
		<pre>
	&lt;transition on="finish" to="finished" /&gt;
		</pre>
		And finally define the end-state:
		<pre>
	&lt;end-state id="finished" view="externalRedirect:welcome" /&gt;
		</pre>
		Click the Finish button and you should be taken back to the application welcome screen.
	</li>
</ol>
	<a href="tutorial?execution=${flowExecutionKey}&_eventId=next">Next &gt;</a>
</body>
</html>