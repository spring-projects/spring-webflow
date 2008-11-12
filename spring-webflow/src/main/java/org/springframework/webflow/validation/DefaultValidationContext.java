package org.springframework.webflow.validation;

import java.security.Principal;
import java.util.List;

import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.webflow.execution.RequestContext;

public class DefaultValidationContext implements ValidationContext {

	private RequestContext requestContext;

	private String eventId;

	private MappingResults mappingResults;

	public DefaultValidationContext(RequestContext requestContext, String eventId, MappingResults mappingResults) {
		this.requestContext = requestContext;
		this.eventId = eventId;
		this.mappingResults = mappingResults;
	}

	public MessageContext getMessageContext() {
		return requestContext.getMessageContext();
	}

	public String getUserEvent() {
		return eventId;
	}

	public Principal getUserPrincipal() {
		return requestContext.getExternalContext().getCurrentUser();
	}

	public Object getUserValue(String property) {
		if (mappingResults != null) {
			List results = mappingResults.getResults(new PropertyMappingResult(property));
			if (!results.isEmpty()) {
				MappingResult result = (MappingResult) results.get(0);
				return result.getOriginalValue();
			}
		}
		return null;
	}

	private static class PropertyMappingResult implements MappingResultsCriteria {

		private String property;

		public PropertyMappingResult(String property) {
			this.property = property;
		}

		public boolean test(MappingResult result) {
			if (property.equals(result.getMapping().getTargetExpression().getExpressionString())) {
				return true;
			} else {
				return false;
			}
		}
	}

}
