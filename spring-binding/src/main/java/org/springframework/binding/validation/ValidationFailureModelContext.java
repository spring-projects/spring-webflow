package org.springframework.binding.validation;

/**
 * Provides additional model context regarding a validation failure. Used by a
 * {@link ValidationFailureMessageResolverFactory} to resolve failure messages.
 */
public class ValidationFailureModelContext {

	private String objectName;

	private Class propertyType;

	private String propertyTypeConverter;

	/**
	 * Creates a new validation model context.
	 * @param objectName the object name
	 * @param propertyType the property type (may be null)
	 * @param propertyTypeConverter the id of the property type converter (may be null)
	 */
	public ValidationFailureModelContext(String objectName, Class propertyType, String propertyTypeConverter) {
		this.objectName = objectName;
		this.propertyType = propertyType;
		this.propertyTypeConverter = propertyTypeConverter;
	}

	/**
	 * The name of the object being validated.
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * When reporting a property validation failure, the type of the property that failed to validate.
	 */
	public Class getPropertyType() {
		return propertyType;
	}

	/**
	 * When reporting a property validation failure, the id of the custom converter used to format the UI display value.
	 */
	public String getPropertyTypeConverter() {
		return propertyTypeConverter;
	}

}
