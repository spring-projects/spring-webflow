package org.springframework.webflow.validation;

import java.security.Principal;
import java.util.List;

import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.binding.validation.ValidationFailure;
import org.springframework.binding.validation.ValidationFailureMessageResolverFactory;
import org.springframework.binding.validation.ValidationFailureModelContext;
import org.springframework.webflow.execution.RequestContext;

public class DefaultValidationContext implements ValidationContext {

	private String modelName;

	private Object model;

	private String converterId;

	private RequestContext requestContext;

	private String eventId;

	private MappingResults mappingResults;

	private ValidationFailureMessageResolverFactory failureMessageResolverFactory;

	public DefaultValidationContext(RequestContext requestContext, String eventId, MappingResults mappingResults) {
		this.requestContext = requestContext;
		this.eventId = eventId;
		this.mappingResults = mappingResults;
	}

	public String getUserEvent() {
		return eventId;
	}

	public Principal getUserPrincipal() {
		return requestContext.getExternalContext().getCurrentUser();
	}

	public Object getUserValue(String property) {
		MappingResult result = getMappingResult(property);
		return result != null ? result.getOriginalValue() : null;
	}

	public void setProperty(String property) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void validate(Object constraint, Object propertyContext) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void validate(Object constraint) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void addDefaultFailure() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void addFailure(final ValidationFailure failure) {
		ValidationFailureModelContext modelContext = new ValidationFailureModelContext() {
			public String getModel() {
				return modelName;
			}

			public Object getInvalidValue() {
				return getUserValue(failure.getProperty());
			}

			public Class getPropertyType() {
				MappingResult result = getMappingResult(failure.getProperty());
				return result != null ? result.getMapping().getTargetExpression().getValueType(model) : null;
			}

			public String getPropertyConverter() {
				return converterId;
			}
		};
		getMessageContext().addMessage(failureMessageResolverFactory.createMessageResolver(failure, modelContext));
	}

	public MessageContext getMessageContext() {
		return requestContext.getMessageContext();
	}

	private MappingResult getMappingResult(String property) {
		if (mappingResults != null) {
			List results = mappingResults.getResults(new PropertyMappingResult(property));
			if (!results.isEmpty()) {
				return (MappingResult) results.get(0);
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