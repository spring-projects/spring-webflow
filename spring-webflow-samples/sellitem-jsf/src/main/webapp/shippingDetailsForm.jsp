<%@ include file="includeTop.jsp" %>

<f:view>

	<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Enter shipping information</h2>
	<hr>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td><h:outputText value="#{flowScope.sale.price}"/></td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td><h:outputText value="#{flowScope.sale.itemCount}"/></td>
	</tr>
	<tr class="readOnly">
		<td>Category:</td><td><h:outputText value="#{flowScope.sale.category}"/></td>
	<tr class="readOnly">
		<td>Shipping:</td><td><h:outputText value="#{flowScope.sale.shipping}"/></td>
	</tr>
	
	<h:form id="shippingForm">
		<tr>
			<td>Shipping type:</td>
			<td>
				<h:selectOneMenu value="#{flowScope.sale.shippingType}">
					<f:selectItem itemLabel="Standard (10 extra cost)" itemValue="S"/>
					<f:selectItem itemLabel="Express (20 extra cost)" itemValue="E"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
				<h:commandButton type="submit" value="Next" action="submit" immediate="false" /></td>
			</td>
		</tr>
		</h:form>
	</table>
	</div>
	
</f:view>

<%@ include file="includeBottom.jsp" %>