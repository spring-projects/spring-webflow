<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Getting Started with Spring Web Flow: Creating Your First Web Flow</title>
	<link rel="stylesheet" href="<c:url value="/resources/dijit/themes/tundra/tundra.css"/>" type="text/css" />
	<link rel="stylesheet" href="<c:url value="/resources/styles/tutorial.css"/>" type="text/css" />
	<link rel="stylesheet" href="<c:url value="/resources/dojox/highlight/resources/highlight.css"/>" type="text/css" />
	<link rel="stylesheet" href="<c:url value="/resources/dojox/highlight/resources/pygments/ide.css"/>" type="text/css" />
	<script type="text/javascript" src="<c:url value="/resources/dojo/dojo.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/spring/Spring.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/spring/Spring-Dojo.js"/>"></script>	
</head>
<body class="tundra">
<a href="http://www.springsource.org"><img src="<c:url value="/resources/images/logo.png"/>"/></a>
<h1>
	Creating Your First Web Flow
</h1>
<div id="section1" class="section">
	<h2>Create your project</h2>
	<div id="environments">
		<div id="sts">
			<ol>
				<li>
					Download the Eclipse IDE for Spring Developers, called the <a href="http://www.springsource.com/products/sts">SpringSource Tool Suite</a> (STS).
					After downloading completes, extract the archive to your directory of choice.
				</li>
				<li>
					Open STS and access File -&gt; New -&gt; Other... -&gt; SpringSource Tool Suite -&gt; Template Project.
					Select "Web Flow Project" and enter helloworld for your project name.
					Select the Embedded Apache Tomcat as your project's targeted server runtime.
					Finish the new project wizard.
				</li>
				<li>
					Next, right-click on your project in your Package Explorer view, and select Run On Server.
					You should see your application's welcome page appear in the embedded web browser.
					Alternatively, you can access your application in an external browser such as Firefox at <a href="http://localhost:8080/helloworld">http://localhost:8080/helloworld</a>
				</li>
			</ol>
		</div>
		<div id="command-line">
			<ol>
				<li>TODO</li>
			</ol>
		</div>
	</div>
</div>
<div id="section2" class="section">
	<h2>Create your first helloworld flow</h2>
	<ol>
		<li>
			Create a new directory for your flow inside <span class="file">/src/main/webapp/WEB-INF</span>; name the directory <span class="file">helloworld</span>.
			Right-click on the directory and access New -&gt; Spring Web Flow Definition.
			Enter the filename <span class="file">helloworld-flow.xml</span> and finish.
			The flow definition will be generated for you with an initial view-state named start.
		</li>
		<li>
			Go ahead and create a <span class="file">start.jsp</span> in your flow directory and paste in the following:
			<pre>
			<code>
    &lt;html&gt;
        &lt;head&gt;
            &lt;title&gt;Hello world!&lt;/title&gt;
        &lt;/head&gt;
        &lt;h1&gt;Hello world!&lt;/h1&gt;
    &lt;/html&gt;
    		</code>
			</pre>
		</li>
		<li>
			From the Servers view, restart your server, then startup your flow by accessing <a href="http://localhost:8080/getting-started-with-spring-webflow/app/helloworld">http://localhost:8080/getting-started-with-spring-webflow/app/helloworld</a>.
			You should see your Hello world! message display.
			Note you only have to restart your server when you add new flows to the system.  When you change flows, changes will be refreshed automatically.
		</li>
	</ol>
</div>
<div id="section3" class="section">
	<h2>Add a page navigation rule</h2>
	<ol>
		<li>
			Next, try transitioning your flow from one state to another to implement a navigation rule.  In your helloworld flow, add the following transition to your start view-state:
			<pre>
			<code>
    &lt;transition on="submit" to="page2" /&gt;
    		</code>
			</pre>
			Then define your page2 view-state:
			<pre>
			<code>
    &lt;view-state id="page2"&gt;
    &lt;/view-state&gt;
    		</code>
			</pre>
			And the corresponding page2.jsp:
			<pre>
			<code>
    &lt;html&gt;
        &lt;head&gt;
            &lt;title&gt;Hello world!&lt;/title&gt;
        &lt;/head&gt;
        &lt;h1&gt;This is page 2!&lt;/h1&gt;
    &lt;/html&gt;
    		</code>
			</pre>
			Finally, create a button on your <span class="file">start.jsp</span> that raises the submit event to trigger the state transition:
			<pre>
			<code>
    &lt;form method="post"&gt;
        &lt;input type="submit" name="_eventId_submit" value="Submit" /&gt;
    &lt;/form&gt;
    		</code>
			</pre>
			Click the button and you should be taken to page 2.
		</li>
	</ol>
</div>
<div id="section4" class="section">
	<h2>Add a dynamic page navigation rule</h2>
	<p>
		Web flow excels at implementing dynamic navigation logic that takes a user through different paths based on what they enter or who they are.
	</p>
	<ol>
		<li>
			Try implementing a dynamic navigation rule by first adding a bound checkbox to your <span class="file">start.jsp</span>.
			Do this by replacing its contents with the following snippet:
			<pre>
			<code>
    &lt;%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %&gt;
    &lt;html&gt;
        &lt;head&gt;
            &lt;title&gt;Hello world!&lt;/title&gt;
        &lt;/head&gt;
        &lt;h1&gt;Hello world!&lt;/h1&gt;
        &lt;form:form method="post" modelAttribute="helloWorldForm"&gt;
            &lt;form:checkbox path="selected" /&gt;
            &lt;input type="submit" name="_eventId_submit" value="Submit" /&gt;
        &lt;/form:form&gt;		
    &lt;/html&gt;
    		</code>
			</pre>
		</li>
		<li>
			Next, create a <span class="file">HelloWorldForm</span> class in the <span class="file">org.springframework.webflow.samples.helloworld</span> package with the following code:
			<pre>
			<code>
    package org.springframework.webflow.samples.helloworld;
		
    import java.io.Serializable;
		
    public class HelloWorldForm implements Serializable {
        private boolean selected = true;
        
        public boolean isSelected() {
            return selected;
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
    		</code>
			</pre>
		</li>
		<li>
			At the top of your helloworld-flow, declare the HelloWorldForm as a flow variable:
			<pre>
			<code>
    &lt;var name="helloWorldForm" class="org.springframework.webflow.samples.helloworld.HelloWorldForm" /&gt;
    		</code>
			</pre>
			Then update your start view-state to use this variable as its data model, enabling automatic model binding and validation:
			<pre>
			<code>
    &lt;view-state id="start" model="helloWorldForm"&gt; ...
    		</code>	
			</pre>
			<p>
				Refresh your flow and the checkbox should render checked since the default value for the HelloWorldForm selected property is true.
				Uncheck the box and submit and go back in your browser and the unchecked status should be preserved.
			</p>
		</li>
		<li>
			Now insert a decision state that says if the checkbox is selected goto page2, else goto a new page3:
			<pre>
			<code>
    &lt;decision-state id="isSelected"&gt;
        &lt;if test="helloWorldForm.selected" then="page2" else="page3" /&gt;
    &lt;/decision-state&gt;
	
    &lt;view-state id="page2"&gt;
    &lt;/view-state&gt;	
	    
    &lt;view-state id="page3"&gt;
    &lt;/view-state&gt;
    		</code>	
			</pre>
			Be sure to update your start view-state to transition to the isSelected decision state instead of page2 directly:
			<pre>
			<code>
    &lt;view-state id="start"&gt;
        &lt;transition on="submit" to="isSelected" /&gt;
    &lt;/view-state&gt;
    		</code>		
			</pre>
			Click the Submit button with the checkbox selected and you should be taken to page2.
			Click the button with the checkbox de-selected and you should be taken to page3 (you'll need to create a JSP or you'll get a 404).
		</li>
	</ol>
</div>
<div id="section5" class="section">
	<h2>Finish your helloworld flow</h2>
	<ol>
		<li>
			Finish up your helloworld flow by adding another button on the <span class="file">start.jsp</span> that ends the flow:
			<pre>
			<code>
    &lt;input type="submit" name="_eventId_finish" value="Finish" /&gt;
    		</code>
			</pre>
			In your start view-state, declare the finish transition:
			<pre>
			<code>
    &lt;transition on="finish" to="finished" /&gt;
    		</code>
			</pre>
			And finally define the end-state:
			<pre>
			<code>
    &lt;end-state id="finished" view="externalRedirect:welcome" /&gt;
    		</code>
			</pre>
			Click the Finish button and you should be taken back to the application welcome screen.
		</li>
	</ol>
</div>
<div id="section6" class="section">
	<h2>Visualize the flow</h2>
	<ol>
		<li>
			In your IDE, navigate to your <span class="file">helloworld-flow.xml</span> in the Spring Explorer view, or within the Spring Elements node of the Project Explorer view.
			Right-click on the file and select Open Graphical Editor.  Your graph should look similar to the visualization below:<br/><br/>
			<img src="<c:url value="/resources/images/tutorial/helloworld-flow.png"/>"/>
		</li>
	</ol>
</div>
<div id="commandBar">
	<p>
		<a href="tutorial?execution=${flowExecutionKey}&_eventId=next">Next &gt;</a>
	</p>
</div>
</body>
<script type="text/javascript">
	dojo.require("dojox.highlight.languages.xml");
	dojo.addOnLoad(function(){
	    dojo.query("code").forEach(dojox.highlight.init);
	});

    Spring.addDecoration(new Spring.ElementDecoration({
        elementId : "sts",
        widgetType : "dijit.layout.ContentPane",
        widgetAttrs : {
            title: "SpringSource Tool Suite",
            selected: "true"
        }
    }));
    Spring.addDecoration(new Spring.ElementDecoration({
        elementId : "command-line",
        widgetType : "dijit.layout.ContentPane",
        widgetAttrs : {
        	title: "Command line"
    	}
    }));
    Spring.addDecoration(new Spring.ElementDecoration({
        elementId : "environments",
        widgetType : "dijit.layout.TabContainer",
        widgetAttrs : {
			style: "width: 100%; height: 200px;"
    	}
    }));

	dojo.query('.section > h2').forEach(function(titleElement){
		Spring.addDecoration(new Spring.ElementDecoration({
			elementId : titleElement.parentNode.id,
			widgetType : 'dijit.TitlePane',
			widgetAttrs : { 
				title : titleElement.innerHTML,
				open : false
			}
		}));
	}).style('display','none');
</script>
</html>