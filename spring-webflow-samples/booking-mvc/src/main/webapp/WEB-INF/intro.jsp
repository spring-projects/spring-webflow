<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>Welcome to Spring Travel</h1>
	<p>
	 	This reference application shows how to use Spring MVC and Web Flow together with JavaServerPages (JSP) and Tiles to power web applications.
	</p>
	<p>
		The key features illustrated in this sample include:
	</p>
	<ul>
		<li>A declarative navigation model enabling full browser button support and dynamic navigation rules</li>
		<li>A fine-grained state management model, including support for ConversationScope and ViewScope</li>
		<li>Modularization of web application functionality by domain use case, illustrating project structure best-practices</li>
		<li>Managed persistence contexts with the Java Persistence API (JPA)</li>
		<li>Unified Expression Language (EL) integration</li>
		<li>Spring Security integration</li>
		<li>Declarative page authoring with JSP, JSTL, and Spring MVC's form tag library
		<li>Applying reusable page layouts with Tiles</li>
		<li>Exception handling support across all layers of the application</li>
		<li>Spring IDE tooling integration, with support for graphical flow modeling and visualization</li>
	</ul>
	<p align="right">
		<a href="main">Start your Spring Travel experience</a>
	</p>
</div>

</tiles:putAttribute>
</tiles:insertTemplate>
