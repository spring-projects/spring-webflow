package org.springframework.binding.validation;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;

/**
 * Translates a validation failure into a message resolver that can be used to add a {@link Message} to a
 * {@link MessageContext} .
 * 
 * @author Keith Donald
 */
public interface ValidationFailureMessageResolverFactory {

	/**
	 * Creates a new message resolver for the validation failure.
	 * @param failure a validation failure reported by the validator
	 * @param modelContext additional information about the model object that failed to validate
	 * @return the resolver of the failure message
	 */
	public MessageResolver createMessageResolver(ValidationFailure failure, ValidationFailureModelContext modelContext);

}
