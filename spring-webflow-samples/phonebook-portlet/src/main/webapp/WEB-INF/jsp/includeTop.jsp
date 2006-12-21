<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<portlet:defineObjects/>

<html>
<head>
<title>Search the Phonebook</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "style.css") %>" type="text/css">
</head>
<body>

<div id="logo">
	<img src="<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/images/spring-logo.jpg") %>" height="73" alt="Logo" border="0"> 
</div>

<div id="navigation">

</div>