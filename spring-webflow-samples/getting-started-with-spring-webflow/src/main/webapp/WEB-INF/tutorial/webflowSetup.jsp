<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Getting Started with Spring Web Flow: Setting up Web Flow in a Spring Web Application</title>
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
	Setting up Web Flow in a Spring Web Application
</h1>
<p>
This step covers the one-time configuration required to setup Web Flow up in a Spring web application.
If you prefer to go right to implementing your first flow, you can <a href="tutorial?execution=${flowExecutionKey}&_eventId=next">skip</a> this step.
</p>
<p>
In general, we recommend using a pre-configured project template like the one this tutorial uses as a starting point for your new projects.
When you do that, you will not have to configure Spring yourself--you simply implement your @Controllers and web flows and off you go.
This step does provides useful background information on setting up your own Web Flow project template, and understanding how things work behind the scenes.
</p>
<div id="question1" class="question">
	<h2>
		What does the configuration of a typical Spring web application look like?
	</h2>
	<p>
		The configuration of every Spring web application starts in web.xml.
		There, a Spring MVC DispatcherServlet is defined to process all requests into the application.
		The DispatcherServlet itself is configured using a Spring container.
		It is responsible for routing web requests to the proper application controllers, such as your Spring MVC @Controllers and web flows.
		A typical DispatcherServlet declaration is shown below:
	</p>
	<pre>
	<code>
    &lt;!-- The front controller of this Spring MVC application, responsible for handling all application requests --&gt;
    &lt;servlet&gt;
        &lt;servlet-name&gt;Spring MVC Dispatcher Servlet&lt;/servlet-name&gt;
        &lt;servlet-class&gt;org.springframework.web.servlet.DispatcherServlet&lt;/servlet-class&gt;
        &lt;init-param&gt;
            &lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;
            &lt;param-value&gt;
                /WEB-INF/spring/*.xml
            &lt;/param-value&gt;
        &lt;/init-param&gt;
        &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
    &lt;/servlet&gt;
    
    &lt;!-- Map all /app requests to the DispatcherServlet for handling --&gt;
    &lt;servlet-mapping&gt;
        &lt;servlet-name&gt;Spring MVC Dispatcher Servlet&lt;/servlet-name&gt;
        &lt;url-pattern&gt;/app/*&lt;/url-pattern&gt;
    &lt;/servlet-mapping&gt;
    </code>
	</pre>
	<p>
		This DispatcherServlet is configured to process requests into /app/*.
		The servlet's configuration is defined in the .xml files in /WEB-INF/spring.
	</p>
</div>
<div id="question2" class="question">
	<h2>
		How are Spring configuration files typically organized?
	</h2>
	<p>
		Inside /WEB-INF/spring, we generally recommend defining a configuration file for your application logic,
		and separate configuration files for framework infrastructure.  For example:
	</p>
	<pre>
	<code>
    /webapp
        /WEB-INF
            /spring
                app-config.xml
                mvc-config.xml
                webflow-config.xml
            web.xml
    </code>
	</pre>
	<p>
		The example above shows the configuration for a Spring web application spread across three files.
		The app-config.xml file configures your components that carry out application-specific controller, business, and data access logic.
		The mvc-config.xml file configures the Spring MVC framework infrastructure, including the properties of the DispatcherServlet.
		The webflow-config.xml file configures the Spring Web Flow infrastructure.
	</p>
	<p>
		We also generally recommend using annotations to configure your application components, and externalized XML to configure infrastructure.
		This is illustrated in app-config.xml by use of the component-scan directive to scan your classpath for application components to deploy:
	</p>
	<pre>
	<code>
    &lt;!-- Scans within the base package of the application for @Components to configure as beans --&gt;
    &lt;context:component-scan base-package="org.springframework.webflow.samples.gettingstarted" /&gt;
    </code>
	</pre>
	<p>
		With this technique, your Spring configuration is setup once and you generally never have to update your configuration files again as new components are added to your application.
	</p>
</div>
<div id="question3" class="question">
	<h2>
		How do I plug-in Spring Web Flow?
	</h2>
	<p>
		In webflow-config.xml, first define a flow-registry to register the flows you have defined in your application:
	</p>
	<pre>
	<code>
    &lt;!-- Registers the web flows that can be executed --&gt;	
    &lt;webflow:flow-registry id="flowRegistry" base-path="/WEB-INF/"&gt;
        &lt;webflow:flow-location-pattern value="**/*-flow.xml" /&gt;
    &lt;/webflow:flow-registry&gt;
    </code>
	</pre>
	<p>
		The example above scans /WEB-INF looking for -flow.xml files and registers them.
	</p>
	<p>
		Then, define a flow-executor that uses this registry to execute your flows:
	</p>
	<pre>
	<code>
    &lt;!-- Configures the engine that executes web flows in this application --&gt;
    &lt;webflow:flow-executor id="flowExecutor" flow-registry="flowRegistry" /&gt;
    </code>
	</pre>
	<p>
		Finally, in mvc-config.xml plug in adapters to hook the flow-executor into the Spring MVC DispatcherServlet request processing pipeline:
	</p>
	<pre>
	<code>
    &lt;!-- Maps requests to flows in the flowRegistry --&gt;
    &lt;bean id="flowMappings" class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping"&gt;
        &lt;property name="order" value="0" /&gt;
        &lt;property name="flowRegistry" ref="flowRegistry" /&gt;
    &lt;/bean&gt;
    
    &lt;!-- Enables Spring Web Flow as a Spring MVC request handler --&gt;
        &lt;bean class="org.springframework.webflow.mvc.servlet.FlowHandlerAdapter"&gt;
        &lt;property name="flowExecutor" ref="flowExecutor" /&gt;
    &lt;/bean&gt;
    </code>
	</pre>
	<p>
		We also recommend you turn on development mode while developing so you never have to redeploy your application to test changes:
	</p>
	<pre>
	<code>
    &lt;webflow:flow-builder-services id="flowBuilderServices" development="true" /&gt;
    </code>
	</pre>
</div>
<div id="question4" class="question">
	<h2>
		How do Spring MVC @Controllers and Web Flows co-exist in the same application?
	</h2>
	<p>
		A typical Spring web application consists of a mix of stateless MVC @Controllers and stateful web flows, which are two distinct types of handlers.
		When a web request comes in for a resource, the DispatcherServlet figures out which handler should be invoked.
		This is done by consulting an ordered chain of HandlerMapping objects configured in your mvc-config.xml.
		Generally, the first HandlerMapping consulted is the FlowHandlerMapping, which determines if the requested resource should be handled by a web flow.
		If no flow handler is found, the next HandlerMapping in the chain is queried.
		This is generally the DefaultAnnotationHandlerMapping, which consults explicit @RequestMapping rules defined inside annotated Spring MVC Controllers.
	</p>
	<p>
		Setting up the HandlerMapping chain is a one-time configuration step, and makes it easy to plug in different types of handlers and mapping strategies.
		A typical HandlerMapping chain for Spring web applications looks like:
	</p>
	<pre>
	<code>
    &lt;!-- Maps requests to flows in the flowRegistry; for example, a request for resource /hotels/booking maps to a flow with id "hotels/booking"
         If no flow is found with that id, Spring MVC proceeds to the next HandlerMapping (order=1 below). --&gt;
    &lt;bean id="flowMappings" class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping"&gt;
        &lt;property name="order" value="0" /&gt;
        &lt;property name="flowRegistry" ref="flowRegistry" /&gt;
    &lt;/bean&gt;

    &lt;!-- Maps requests to @Controllers based on @RequestMapping("path") annotation values
         If no annotation-based path mapping is found, Spring MVC proceeds to the next HandlerMapping (order=2 below). --&gt;
    &lt;bean class="org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping"&gt;
        &lt;property name="order" value="1" /&gt;
    &lt;/bean&gt;
    </code>
	</pre>
	<p>
		Once a request has been mapped to a handler object such as a @Controller of web flow, the DispatcherServlet uses the HandlerAdapter registered for that kind of handler to invoke it.
		This decouples the DispatcherServlet from specific handler implementations, which allows Spring MVC to support different controller technologies in an extensible manner.
		As a one-time configuration step, a typical Spring web application registers HandlerAdapters that know how to invoke @Controllers and web flows when they are mapped:
	</p>
	<pre>
	<code>
    &lt;!-- Enables annotated @Controllers; responsible for invoking an annotated POJO @Controller when one is mapped. --&gt;
    &lt;bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter" /&gt;
    
    &lt;!-- Enables web flows; responsible for calling the Spring Web Flow system to execute a flow when one is mapped. --&gt;
    &lt;bean class="org.springframework.webflow.mvc.servlet.FlowHandlerAdapter"&gt;
        &lt;property name="flowExecutor" ref="flowExecutor" /&gt;
    &lt;/bean&gt;
    </code>	
	</pre>
	<p>
		To illustrate a typical DispatcherServlet pipeline, the following graphic illustrates the sequence of events that happen in this application when the /tutorial resource is requested, which is handled by the web flow you are interacting with right now:<br>
		<img src="<c:url value="/resources/images/tutorial/dispatcher-servlet-flow-handler.png" />" />
	</p>
	<p>
		In this scenario, the FlowHandlerMapping returned the tutorial flow which was then invoked by the FlowHandlerAdapter.
	</p>
	<p>
		The following graphic shows the sequence in this application when the /welcome resource is requested, which is handled by the annotated WelcomeController:<br>
		<img src="<c:url value="/resources/images/tutorial/dispatcher-servlet-annotated-controller-handler.png" />" />
	</p>
	<p>
		In this scenario, the FlowHandlerMapping returned null because the /welcome resource was not mapped to a web flow.
		The DefaultAnnotationHandlerMapping was then queried and returned the WelcomeController, which was invoked by the AnnotationMethodHandlerAdapter.
	</p>
	<p>
		The main point to understand here is there is one-time configuration that enables full customization of the DispatcherServlet processing pipeline.
		Once this configuration is established, you simply create new controllers and web flows, and they get picked up and hooked into the pipeline automatically.
		No other configuration is required.
	</p>
</div>
<div id="commandBar">
	<p>
		<a href="tutorial?execution=${flowExecutionKey}&_eventId=next">Next &gt;</a>
	</p>
</div>
</body>
<script type="text/javascript">
	dojo.query('.question > h2').forEach(function(titleElement){
		Spring.addDecoration(new Spring.ElementDecoration({
			elementId : titleElement.parentNode.id,
			widgetType : 'dijit.TitlePane',
			widgetAttrs : { 
				title : titleElement.innerHTML,
				open : false
			}
		}));
	}).style('display','none');

	dojo.require("dojox.highlight.languages.xml");
	dojo.addOnLoad(function(){
	    dojo.query("code").forEach(dojox.highlight.init);
	});
</script>
</html>