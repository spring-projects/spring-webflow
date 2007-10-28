<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<tiles:insertTemplate template="/template.jsp">
<tiles:putAttribute name="content">

<div class="section">
	<h1>Confirm Hotel Booking</h1>
</div>

<div class="section">
	<h:form id="confirm">
	<fieldset>
		<div class="entry">
			<div class="label">Name:</div>
			<div class="output">#{hotel.name}</div>
		</div>
		<div class="entry">
			<div class="label">Address:</div>
			<div class="output">#{hotel.address}</div>
		</div>
		<div class="entry">
			<div class="label">City, State:</div>
			<div class="output">#{hotel.city}, #{hotel.state}</div>
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
            <div class="label">Total payment:</div>
            <div class="output">
                <h:outputText value="#{booking.total}">
                    <f:convertNumber type="currency" 
                                     currencySymbol="$"/>
                </h:outputText>
            </div>
        </div>
		<div class="entry">
			<div class="label">Check In Date:</div>
			<div class="output"><h:outputText value="#{booking.checkinDate}"/></div>
		</div>
		<div class="entry">
			<div class="label">Check Out Date:</div>
			<div class="output"><h:outputText value="#{booking.checkoutDate}"/></div>
		</div>
		<div class="entry">
			<div class="label">Credit Card #:</div>
			<div class="output">#{booking.creditCard}</div>
		</div>
		<div class="entry">
			<div class="label">&#160;</div>
			<div class="input">
				<h:commandButton id="confirm" value="Confirm" action="confirm"/>&#160;
    			<h:commandButton id="revise" value="Revise" action="revise"/>&#160;
    			<h:commandButton id="cancel" value="Cancel" action="cancel"/>
			</div>
		</div>
	</fieldset>
	</h:form>
</div>

</tiles:putAttribute>
</tiles:insertTemplate>
