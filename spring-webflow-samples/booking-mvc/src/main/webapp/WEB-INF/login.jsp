<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/WEB-INF/layouts/standard.jsp">
<tiles:putAttribute name="content">

<h1>Login Required</h1>

<c:if test="${not empty param.login_error}">
	<div class="errors">
		Your login attempt was not successful, try again.<br /><br />
		Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
	</div>
</c:if>

<div class="section">
	<p>Valid username/passwords are:</p>
	<ul>
		<li>keith/melbourne</li>
	    <li>erwin/leuven</li>
	    <li>jeremy/atlanta</li>
	    <li>scott/rochester</li>
	</ul>
</div>

<div class="section">
	<form name="f" action="<c:url value="/spring/login-process" />" method="post">
		<fieldset>
			<div class="field">
				<div class="label">User:</div>
				<div class="output">
					<input type="text" name="j_username" <c:if test="${not empty param.login_error}">value="<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>"</c:if>>
				</div>
			</div>
			<div class="field">
				<div class="label">Password:</div>
				<div class="output">
					<input type="password" name="j_password" />
				</div>
			</div>
			<div class="field">
				<div class="label">Don't ask for my password for two weeks:</div>
				<div class="output">
					<input type="checkbox" name="_spring_security_remember_me"> 
				</div>
			</div>
		</fieldset>
		<div class="buttonGroup">
			<input name="submit" type="submit" value="Login" />
		</div>
	</form>
</div>


</tiles:putAttribute>
</tiles:insertTemplate>