<%@ page contentType="text/html" %>
<%@ include file="includeTop.jsp"%>

<f:view>

	<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg" /></div>

	<!-- display any errors from sale Validator -->
    <c:if test="${not empty sale}">
      <spring:bind path="sale.*">
        <c:forEach items="${status.errorMessages}" var="curError">
	      <div class="error">${curError}</div>
        </c:forEach>
      </spring:bind>
    </c:if>

	<h2>Enter price and item count</h2>
	<hr>
	<table>
		<h:form id="priceAndItemCountForm">
			<tr>
				<td>Price:</td>
				<td><h:inputText id="price" value="#{flowScope.sale.price}"
					required="true">
					  <f:validateDoubleRange minimum="0.01"/>
					</h:inputText>
					&nbsp;&nbsp;
					<h:message for="price" style="color: red"/>
				</td>
			</tr>
			<tr>
				<td>Item count:</td>
				<td><h:inputText id="itemCount" value="#{flowScope.sale.itemCount}"
					required="true">

					  <f:validateLongRange minimum="1"/>
					</h:inputText>
					&nbsp;&nbsp;
					<h:message for="itemCount" style="color: red"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="buttonBar">
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<h:commandButton type="submit" value="Next" action="submit" immediate="false" /></td>
			</tr>
		</h:form>
	</table>
	</div>

</f:view>

<%@ include file="includeBottom.jsp"%>
