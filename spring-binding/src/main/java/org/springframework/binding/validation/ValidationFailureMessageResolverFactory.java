package org.springframework.binding.validation;

import org.springframework.binding.message.MessageResolver;

/**
 * Translates a validation failure into a resolvable message.
 * 
 * @author Keith Donald
 */
public interface ValidationFailureMessageResolverFactory {

	/**
	 * Creates a new message resolver for the validation failure.
	 * @param failure the validation failure
	 * @return the message resolver for the failure
	 */
	MessageResolver createMessageResolver(ValidationFailure failure);
}
