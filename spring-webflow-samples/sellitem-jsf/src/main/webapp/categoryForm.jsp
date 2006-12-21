<%@ include file="includeTop.jsp" %>

<f:view>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Select category</h2>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td><h:outputText value="#{flowScope.sale.price}"/></td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td><h:outputText value="#{flowScope.sale.itemCount}"/></td>
	</tr>

	<h:form id="categoryForm">
		<tr>
			<td>Category:</td>
			<td>
				<h:selectOneMenu value="#{flowScope.sale.category}">
					<f:selectItem itemLabel="None (0.02 discount rate)" itemValue=""/>
					<f:selectItem itemLabel="Cat. A (0.1 discount rate when more than 100 items)" itemValue="A"/>
					<f:selectItem itemLabel="Cat. B (0.2 discount rate when more than 200 items)" itemValue="B"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td>Is shipping required?:</td>
			<td>
				<h:selectBooleanCheckbox value="#{flowScope.sale.shipping}"/>
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