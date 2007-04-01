<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<html>
<head>
<title>Sell Item - Enter Price and Item Count</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="style.css" type="text/css">
</head>
<body>

<div id="logo">
	<img src="images/spring-logo.jpg" alt="Logo" border="0"> 
</div>

<f:view>
<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg" /></div>

	<h2>Enter price and item count</h2>
	<hr>
	<table>
		<h:form id="priceAndItemCountForm">
			<tr>
				<td>Price:</td>
				<td>
					<h:inputText id="price" value="#{flowScope.sale.price}" required="true">
					  <f:validateDoubleRange minimum="0.01"/>
					</h:inputText>
				</td>
				<td>
					<h:message for="price" errorClass="error"/>
				</td>
			</tr>
			<tr>
				<td>Item count:</td>
				<td>
					<h:inputText id="itemCount" value="#{flowScope.sale.itemCount}" required="true">
					  <f:validateLongRange minimum="1"/>
					</h:inputText>
				</td>
				<td>
					<h:message for="itemCount" errorClass="error"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<h:commandButton type="submit" value="Next" action="submit" immediate="false" />
				</td>
				<td></td>
			</tr>
		</h:form>
	</table>
</div>
</f:view>

<div id="copyright">
	<p>&copy; Copyright 2004-2007, <a href="http://www.springframework.org">www.springframework.org</a>, under the terms of the Apache 2.0 software license.</p>
</div>
</body>
</html>