package org.springframework.binding.validation;

/**
 * Provides additional model context regarding a validation failure. Used by a
 * {@link ValidationFailureMessageResolverFactory} to resolve failure messages.
 */
public interface ValidationFailureModelContext {

	/**
	 * The name of the model object that was validated.
	 */
	public String getModel();

	/**
	 * When reporting a property validation failure, the invalid user entered value.
	 */
	public Object getInvalidValue();

	/**
	 * When reporting a property validation failure, the type of the property that failed to validate.
	 */
	public Class getPropertyType();

	/**
	 * When reporting a property validation failure, the id of the custom converter used to format the UI display value.
	 */
	public String getPropertyConverter();

}