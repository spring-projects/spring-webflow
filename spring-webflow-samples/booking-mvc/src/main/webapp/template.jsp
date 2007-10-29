<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Spring Faces: Hotel Booking Sample Application</title>
	<style type="text/css" media="screen">
        @import url("/swf-booking-mvc/spring/resources/css-framework/css/tools.css");
        @import url("/swf-booking-mvc/spring/resources/css-framework/css/typo.css");
        @import url("/swf-booking-mvc/spring/resources/css-framework/css/forms.css");
        @import url("/swf-booking-mvc/spring/resources/css-framework/css/layout-navtop-localleft.css");        
        @import url("/swf-booking-mvc/spring/resources/css-framework/css/layout.css");
        @import url("/swf-booking-mvc/spring/resources/css/booking.css");
    </style>
</head>
<body class="tundra spring">
<div id="page">
	<div id="header" class="clearfix spring">
		<div id="welcome">
			<div class="left">Spring Web Flow + Spring MVC: Hotel Booking Sample Application</div>
			<div class="right">
	            Welcome, ${user.name}
			</div>
		</div>
		<div id="branding" class="spring">
			<img src="/swf-booking-mvc/spring/resources/images/header.jpg" />
		</div>
	</div>
	<div id="content" class="clearfix spring">
		<div id="local" class="spring">
			<a href="http://www.thespringexperience.com">
				<img src="/swf-booking-mvc/spring/resources/images/diplomat.jpg"/>
			</a>
			<a href="http://www.thespringexperience.com">
				<img src="/swf-booking-mvc/spring/resources/images/tse.gif"/>
			</a>
			<p>
				The features illustrated in this sample are just the beginning.
				To see what is in store for developing rich web applications with Spring, join us at
				<a href="http://www.thespringexperience.com">The Spring Experience</a>
				December 12 - 15, 2007 at the Westin Diplomat in Hollywood, Florida.
			</p>
		</div>
		<div id="main">
			<tiles:insertAttribute name="content" />
		</div>
	</div>
	<div id="footer" class="clearfix spring">
		<a href="http://www.springframework.org">
			<img src="/swf-booking-mvc/spring/resources/images/powered-by-spring.png"/>
		</a>
	</div>
</div>
</body>
</html>