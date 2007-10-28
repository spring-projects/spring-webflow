<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/template.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>View Hotel</h1>
</div>

<div class="section">
	<div class="entry">
		<div class="label">Name:</div>
		<div class="output">#{hotel.name}</div>
	</div>
	<div class="entry">
		<div class="label">Address:</div>
		<div class="output">#{hotel.address}</div>
	</div>
	<div class="entry">
		<div class="label">City:</div>
		<div class="output">#{hotel.city}</div>
	</div>
	<div class="entry">
		<div class="label">State:</div>
		<div class="output">#{hotel.state}</div>
	</div>
	<div class="entry">
		<div class="label">Zip:</div>
		<div class="output">#{hotel.zip}</div>
	</div>
	<div class="entry">
		<div class="label">Country:</div>
		<div class="output">#{hotel.country}</div>
	</div>
    <div class="entry">
        <div class="label">Nightly rate:</div>
        <div class="output">
            <h:outputText value="#{hotel.price}">
                <f:convertNumber type="currency" currencySymbol="$"/>
            </h:outputText>
        </div>
    </div>
</div>

<div class="section">
	<h:form id="hotel">
	<fieldset class="buttonBox">
		<h:commandButton id="book" action="book" value="Book Hotel"/>&#160;
		<h:commandButton id="cancel" action="cancel" value="Back to Search"/>
	</fieldset>
	</h:form>
</div>

</titles:putAttribute>
</tiles:insertTemplate>