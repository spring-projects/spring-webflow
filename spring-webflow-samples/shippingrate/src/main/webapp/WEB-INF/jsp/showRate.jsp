<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form action="rates.htm" method="post">

	<fieldset>
		<legend>Rate details</legend>
		<label>Your shipping rate is: <span>${rate}</span>
	</fieldset>
	
</form>