<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>Welcome to the Spring Web Flow + Spring MVC Sample Application</h1>
	<p>
	 	This hotel booking sample application illustrates Spring Web Flow in a Spring MVC environment.
		The key features illustrated in this sample include:
	</p>
	<ul>
		<li>A unified navigation model</li>
		<li>A robust state management model</li>
		<li>Modularization of web application functionality by domain responsibility</li>
		<li>Flow-managed persistence contexts with the Java Persistence API (JPA)</li>
		<li>OGNL Expression Language (EL) integration</li>
		<li>Spring IDE integration, with support for graphical flow modeling</li>
	</ul>
	<p align="right">
		<a href="main">Start your hotel booking experience</a>
	</p>
</div>

</tiles:putAttribute>
</tiles:insertTemplate>
