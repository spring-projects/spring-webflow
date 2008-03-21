<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">


<div class="section">
	<h1>Logout</h1>
	<p>You have successfully logged out.</p>
	<p><a href="<c:url value="/spring/main" />">Continue</a></p>
</div>


</tiles:putAttribute>
</tiles:insertTemplate>