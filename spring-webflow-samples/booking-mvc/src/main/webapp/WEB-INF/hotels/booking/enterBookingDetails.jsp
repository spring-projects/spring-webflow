<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="heading"class="section">
	<h2>Book Hotel</h2>
</div>

<div id="bookingDetails" class="section">
	<tiles:insertAttribute name="messages"/>
	<tiles:insertAttribute name="bookingForm"/>	
</div>
