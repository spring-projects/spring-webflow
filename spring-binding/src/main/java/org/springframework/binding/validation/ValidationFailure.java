package org.springframework.binding.validation;

import java.util.Map;

import org.springframework.binding.message.Severity;
import org.springframework.util.Assert;

/**
 * An indication of a validation failure. A failure is generated when a validation constraint is violated; for example
 * "required", "maxLength", "invalidFormat", or "range". A failure has a severity describing the intensity of the
 * violation. A failure may have additional arguments that can be used as named parameters in a UI display message. A
 * failure can also have a default message to display if no suitable message can be resolved.
 */
public class ValidationFailure {

	private String propertyName;

	private String constraint;

	private Severity severity;

	private Map arguments;

	private String defaultMessage;

	/**
	 * Creates a new validation failure
	 * @param propertyName the property that failed to validate (may be null to indicate a general failure)
	 * @param constraint the name of the validation constraint that failed (required)
	 * @param severity the severity of the failure (required)
	 * @param arguments named failure arguments (may be null)
	 * @param defaultMessage the default message text (may be null)
	 */
	public ValidationFailure(String propertyName, String constraint, Severity severity, Map arguments,
			String defaultMessage) {
		Assert.hasText(constraint, "The constraint is required");
		Assert.notNull(severity, "The severity is required");
		this.propertyName = propertyName;
		this.constraint = constraint;
		this.severity = severity;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * The name of the property that failed to validate. May be null to indicate a general failure against the validated
	 * object.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * The validation constraint that caused this failure to be reported.
	 */
	public String getConstraint() {
		return constraint;
	}

	/**
	 * The severity of the failure, which measures the impact of the constraint violation.
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * An map of arguments that can be used as named parameters in the message associated with this validation failure.
	 * Each constraint that can fail defines a set of arguments that are specific to it. For example, a range constriant
	 * might define arguments of "min" and "max" of Integer values. In the message bundle, you then might see
	 * "range=The ${label} field value must be between ${min} and ${max}".
	 */
	public Map getArguments() {
		return arguments;
	}

	/**
	 * The default message to display if no suitable display message can be resolved for this failure.
	 */
	public String getDefaultMessage() {
		return defaultMessage;
	}

}
