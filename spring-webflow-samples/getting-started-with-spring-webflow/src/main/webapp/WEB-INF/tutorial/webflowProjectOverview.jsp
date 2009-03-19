<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Getting Started with Spring Web Flow: Overview of the Spring Web Flow Project</title>
</head>
<body>
<h1>
	Overview of the Spring Web Flow Project
</h1>
<h2>
	What is Spring Web Flow?
</h2>
<p>
	Spring Web Flow is a framework for implementing stateful web controllers.
	It is a Spring Project and part of Spring's open-source Web Stack.
</p>
<h2>
	When do I use Web Flow?
</h2>
<p>
Use Spring Web Flow when you need to implement a flow that guides your users through a series of screens to complete a business goal.
Web Flow provides a <i>flow definition language</i> for authoring flows that define screen navigation rules.
The framework also cares for managing conversational state and preventing duplicate transactions.
</p>
<h2>
	How does Web Flow relate to other Spring projects?
</h2>
<p>
Spring Web Flow builds on the Spring Framework project, which includes the Spring MVC web framework.
Concretely, Spring Web Flow plugs into Spring MVC as a Controller technology.
Then, a typical Spring-powered web application is implemented using a mix of annotated Spring MVC @Controllers and web flows.
In general, use @Controllers for implementing simple, single-request user interactions, and web flows for stateful, multi-step user interactions.
</p>
<p>
A number of other Spring projects also integrate with Spring Web Flow.
The Spring Faces project uses Spring Web Flow as the controller framework to support implementing Spring-powered web applications that use JavaServerFaces (JSF) as the view technology.
Spring Web Flow also provides integration with Spring Security for securing web flows.
</p>
<p>
How Spring Web Flow fits into Spring's layered, a-la-carte "Web Stack" is illustrated below:
<img src="<c:url value="/resources/images/tutorial/spring-web-stack.png" />" />
</p>
<p>
	<a href="tutorial?execution=${flowExecutionKey}&_eventId=next">Next &gt;</a>
</p>
</body>
</html>