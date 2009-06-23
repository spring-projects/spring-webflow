<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form modelAttribute="searchCriteria" action="search" method="get">
<div class="section">
    <span class="errors">
    	<form:errors path="*"/>
    </span>
	<h2>Search Hotels</h2>
	<fieldset>
		<div class="field">
			<div class="label">
				<label for="searchString">Search String:</label>
			</div>		
			<div class="input">
				<form:input id="searchString" path="searchString"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "searchString",
						widgetType : "dijit.form.ValidationTextBox",
						widgetAttrs : { promptMessage : "Search hotels by name, address, city, or zip." }}));
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="pageSize">Maximum results:</label>
			</div>
			<div class="input">	
				<form:select id="pageSize" path="pageSize">
					<form:option label="5" value="5"/>
					<form:option label="10" value="10"/>
					<form:option label="20" value="20"/>
				</form:select>
			</div>
		</div>
		<div class="buttonGroup">
			<input type="submit" value="Find Hotels" />
		</div>		
    </fieldset>
</div>
</form:form>