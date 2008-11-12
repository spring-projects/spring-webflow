package org.springframework.webflow.validation;

import java.security.Principal;

import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.webflow.execution.RequestContext;

public class DefaultValidationContext implements ValidationContext {

	private RequestContext requestContext;

	private String eventId;

	public DefaultValidationContext(RequestContext requestContext, String eventId) {
		this.requestContext = requestContext;
		this.eventId = eventId;
	}

	public MessageContext getMessageContext() {
		return requestContext.getMessageContext();
	}

	public String getUserEvent() {
		if (eventId != null) {
			return eventId;
		} else if (requestContext.getCurrentEvent() != null) {
			return requestContext.getCurrentEvent().getId();
		} else {
			return null;
		}
	}

	public Principal getUserPrincipal() {
		return requestContext.getExternalContext().getCurrentUser();
	}

	public Object getUserValue(String field) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
