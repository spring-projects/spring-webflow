package org.springframework.binding.validation;

import java.util.Map;

import org.springframework.binding.message.Severity;

/**
 * A failure that occurred against a specific property on the object being violated.
 * 
 * @author Keith Donald
 */
public final class PropertyValidationFailure extends ValidationFailure {

	private String propertyName;

	/**
	 * Creates a new property validation failure.
	 * @param propertyName the property name
	 * @param code the failure code
	 * @param severity the severity
	 * @param arguments named failure arguments
	 * @param defaultMessage default message text
	 */
	public PropertyValidationFailure(String propertyName, String code, Severity severity, Map arguments,
			String defaultMessage) {
		super(code, severity, arguments, defaultMessage);
		this.propertyName = propertyName;
	}

	/**
	 * The name of the property.
	 */
	public String getPropertyName() {
		return propertyName;
	}

}
