<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Spring Faces: Hotel Booking Sample Application</title>
	<link href="/swf-booking-jsf/css/screen.css" rel="stylesheet" type="text/css" />
	<tiles:insertAttribute name="headIncludes" />
</head>
<body>

<div id="document">
	<div id="header">
		<div id="title">Spring Faces: Hotel Booking Sample Application</div>
		<div id="status">
            Welcome #{user.name}
		</div>
	</div>
	<div id="container">
		<div id="sidebar">
			<a href="http://www.thespringexperience.com">
				<c:url value="/images/diplomat.jpg" />
			</a>
			<a href="http://www.thespringexperience.com">
				<c:url value="/images/tse.gif" />
			</a>
			<p>
				The features illustrated in this sample are just the beginning.
				To see what is in store for developing rich web applications with Spring, join us at
				<a href="http://www.thespringexperience.com">The Spring Experience</a>
				December 12 - 15, 2007 at the Westin Diplomat in Hollywood, Florida.
			</p>
		</div>
		<div id="content">
			<tiles:insertAttribute name="content" />
		</div>
		<div id="footer">
			<a href="http://www.springframework.org"><img src="/swf-booking-jsf/images/powered-by-spring.png" /></a>
		</div>
	</div>
</div>

</body>
</html>