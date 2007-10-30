<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Spring Faces: Hotel Booking Sample Application</title>
	<style type="text/css" media="screen">
        @import url("resources/css-framework/css/tools.css");
        @import url("resources/css-framework/css/typo.css");
        @import url("resources/css-framework/css/forms.css");
        @import url("resources/css-framework/css/layout-navtop-localleft.css");        
        @import url("resources/css-framework/css/layout.css");
        @import url("resources/css/booking.css");
    </style>
</head>
<body class="spring">
<div id="page">
	<div id="header" class="clearfix spring">
		<div id="welcome">
			<div class="left">Spring Faces: Hotel Booking Sample Application</div>
		</div>
		<div id="branding" class="spring">
			<img src="<c:url value="/images/header.jpg"/>" alt="A room at the Westin Diplomat in Hollywood, Florida"/>
		</div>
	</div>
	<div id="content" class="clearfix spring">
		<div id="local" class="spring">
			<a href="http://www.thespringexperience.com">
				<img src="<c:url value="/images/diplomat.jpg"/>" alt="The Westin Diplomat in Hollywood, Florida"/>
			</a>
			<a href="http://www.thespringexperience.com">
				<img src="<c:url value="/images/tse.gif"/>" alt="The Spring Experience"/>
			</a>
			<p>
				The features illustrated in this sample are just the beginning.
				To see what is in store for developing rich web applications with Spring, join us at
				<a href="http://www.thespringexperience.com">The Spring Experience</a>
				December 12 - 15, 2007 at the Westin Diplomat in Hollywood, Florida.
			</p>
		</div>
		<div id="main">
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
		</div>
	</div>
	<div id="footer" class="clearfix spring">
		<a href="http://www.springframework.org"><img src="<c:url value="/images/powered-by-spring.png"/>" alt="Powered By Spring" /></a>
	</div>
</div>
</body>
</html>