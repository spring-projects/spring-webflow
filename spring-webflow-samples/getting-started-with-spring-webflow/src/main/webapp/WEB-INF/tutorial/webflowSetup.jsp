<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Getting Started with Spring Web Flow: Setting up Web Flow in a Spring Web Application</title>
</head>
<body>
<h1>
	Setting up Web Flow in a Spring Web Application
</h1>
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
<pre class="code">
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
</pre>
<p>
	This DispatcherServlet is configured to process requests into /app/*.
	The servlet's configuration is defined in the .xml files in /WEB-INF/spring.
</p>
<h2>
	How are Spring configuration files typically organized?
</h2>
<p>
	Inside /WEB-INF/spring, we generally recommend defining a configuration file for your application logic,
	and separate configuration files for framework infrastructure.  For example:
</p>
<pre>
	/webapp
		/WEB-INF
			/spring
				app-config.xml
				mvc-config.xml
				webflow-config.xml		
</pre>
<p>
	The example above shows the configuration for a Spring web application spread across three files.
	app-config.xml configures your components that carry out application-specific controller, business, and data access logic.
	mvc-config.xml configures the Spring MVC framework infrastructure, including the properties of the DispatcherServlet.
	webflow-config configures the Spring Web Flow infrastructure, which plugs into Spring MVC.
</p>
<h2>
	How do I plug-in Spring Web Flow?
</h2>
<p>
	In webflow-config.xml, first define a flow-registry to register the flows you have defined in your application:
</p>
<pre>
	&lt;!-- Registers the web flows that can be executed --&gt;	
	&lt;webflow:flow-registry id="flowRegistry" base-path="/WEB-INF/"&gt;
		&lt;webflow:flow-location-pattern value="**/*-flow.xml" /&gt;
	&lt;/webflow:flow-registry&gt;
</pre>
<p>
	The example above scans /WEB-INF looking for -flow.xml files and registers them.
</p>
<p>
	Then, define a flow-executor that uses this registry to execute your flows:
</p>
<pre>
	&lt;!-- Configures the engine that executes web flows in this application --&gt;
	&lt;webflow:flow-executor id="flowExecutor" flow-registry="flowRegistry" /&gt;
</pre>
<p>
	Finally, in mvc-config.xml plug in adapters to hook the flow-executor into the Spring MVC DispatcherServlet request processing pipeline:
</p>
<pre>
	&lt;!-- Maps requests to flows in the flowRegistry --&gt;
	&lt;bean id="flowMappings" class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping"&gt;
		&lt;property name="order" value="0" /&gt;
		&lt;property name="flowRegistry" ref="flowRegistry" /&gt;
	&lt;/bean&gt;

	&lt;!-- Enables Spring Web Flow as a Spring MVC request handler --&gt;
	&lt;bean class="org.springframework.webflow.mvc.servlet.FlowHandlerAdapter"&gt;
		&lt;property name="flowExecutor" ref="flowExecutor" /&gt;
	&lt;/bean&gt;
</pre>
<p>
	We also recommend you turn on development mode while developing so you never have to redeploy your application to test changes:
</p>
<pre>
	&lt;webflow:flow-builder-services id="flowBuilderServices" development="true" /&gt;
</pre>
<p>
	<a href="tutorial?execution=${flowExecutionKey}&_eventId=next">Next &gt;</a>
</p>
</body>
</html>