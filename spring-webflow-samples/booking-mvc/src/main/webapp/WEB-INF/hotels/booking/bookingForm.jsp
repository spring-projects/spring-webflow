<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form modelAttribute="booking">
	<fieldset>
		<div class="field">
			<div class="label">Name:</div>
			<div class="output">${booking.hotel.name}</div>
		</div>
		<div class="field">
			<div class="label">Address:</div>
			<div class="output">${booking.hotel.address}</div>
		</div>
		<div class="field">
			<div class="label">City, State:</div>
			<div class="output">${booking.hotel.city}, ${booking.hotel.state}</div>
		</div>
		<div class="field">
			<div class="label">Zip:</div>
			<div class="output">${booking.hotel.zip}</div>
		</div>
		<div class="field">
			<div class="label">Country:</div>
			<div class="output">${booking.hotel.country}</div>
		</div>
        <div class="field">
            <div class="label">Nightly rate:</div>
            <div class="output">
	        	<spring:bind path="booking.hotel.price">${status.value}</spring:bind>
            </div>
        </div>
        <div class="field">
			<div class="label">
				<label for="checkinDate">Check In Date:</label>
			</div>
			<div class="input">
				<form:input path="checkinDate"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "checkinDate",
						widgetType : "dijit.form.DateTextBox",
						widgetAttrs : { value : dojo.date.locale.parse(dojo.byId("checkinDate").value, {selector : "date", datePattern : "yyyy-MM-dd"}), required : true }}));  
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="checkoutDate">Check Out Date:</label>
			</div>
			<div class="input">
				<form:input path="checkoutDate"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "checkoutDate",
						widgetType : "dijit.form.DateTextBox",
						widgetAttrs : { value : dojo.date.locale.parse(dojo.byId("checkoutDate").value, {selector : "date", datePattern : "yyyy-MM-dd"}), required : true }}));  
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="beds">Room Preference:</label>
			</div>
			<div class="input">
				<form:select id="beds" path="beds">
					<form:option label="One king-size bed" value="1"/>
					<form:option label="Two double beds" value="2"/>
					<form:option label="Three beds" value="3"/>
				</form:select>
			</div>
		</div>
		<div class="field">
			<div class="label">
				Smoking Preference:
			</div>
			<div id="radio" class="input">
				<form:radiobutton id="smoking" path="smoking" label="Smoking" value="true"/>
				<form:radiobutton id="non-smoking" path="smoking" label="Non Smoking" value="false"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : 'smoking',
						widgetType : "dijit.form.RadioButton",
						widgetModule : "dijit.form.CheckBox",
						widgetAttrs : { value : true }}));
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : 'non-smoking',
						widgetType : "dijit.form.RadioButton",
						widgetModule : "dijit.form.CheckBox",
						widgetAttrs : { value : false }}));
				</script>
				
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="creditCard">Credit Card #:</label>
			</div>
			<div class="input">
				<form:input path="creditCard"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "creditCard",
						widgetType : "dijit.form.ValidationTextBox",
						widgetAttrs : { required : true, invalidMessage : "A 16-digit credit card number is required.", 
						regExp : "[0-9]{16}"  }}));
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="creditCardName">Credit Card Name:</label>
			</div>
			<div class="input">
				<form:input path="creditCardName" maxlength="40"/>
				<script type="text/javascript">
					Spring.addDecoration(new Spring.ElementDecoration({
						elementId : "creditCardName",
						widgetType : "dijit.form.ValidationTextBox",
						widgetAttrs : { required : true }}));
				</script>
			</div>
		</div>
		<div class="field">
			<div class="label">
				<label for="creditCardExpiryMonth">Expiration Date:</label>
			</div>
			<div class="input">
				<form:select id="creditCardExpiryMonth" path="creditCardExpiryMonth">
					<form:option label="Jan" value="1"/>
					<form:option label="Feb" value="2"/>
					<form:option label="Mar" value="3"/>
					<form:option label="Apr" value="4"/>
					<form:option label="May" value="5"/>
					<form:option label="Jun" value="6"/>
					<form:option label="Jul" value="7"/>
					<form:option label="Aug" value="8"/>
					<form:option label="Sep" value="9"/>
					<form:option label="Oct" value="10"/>
					<form:option label="Nov" value="11"/>
					<form:option label="Dec" value="12"/>
				</form:select>
				<form:select path="creditCardExpiryYear">
					<form:option label="2008" value="1"/>
					<form:option label="2009" value="2"/>
					<form:option label="2010" value="3"/>
					<form:option label="2011" value="4"/>
					<form:option label="2012" value="5"/>
				</form:select>
			</div>
		</div>
		<div class="buttonGroup">
			<input type="submit" id="proceed" name="_eventId_proceed" value="Proceed" 
				onclick="Spring.remoting.submitForm('proceed', 'booking', {fragments:'messages,bookingForm'}); return false;"/>&#160;
			<script type="text/javascript">
				Spring.addDecoration(new Spring.ValidateAllDecoration({elementId:'proceed', event:'onclick'}));
			</script>
			<input type="submit" name="_eventId_cancel" value="Cancel"/>&#160;			
		</div>
	</fieldset>
</form:form>