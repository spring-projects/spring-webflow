package org.springframework.binding.validation;

import java.util.Map;

import org.springframework.binding.message.Severity;

/**
 * A failure that occurred during validation. Each failure has a code that uniquely identifies the validation constraint
 * that was violated; for example "required", or "maxLength". A failure has a severity describing the intensity of the
 * violation. A failure may have additional arguments that can be used as named parameters in a UI display message. A
 * failure can also have a default message to display if no suitable message can be resolved.
 */
public class ValidationFailure {

	private String code;

	private Severity severity;

	private Map arguments;

	private String defaultMessage;

	/**
	 * Creates a new validation failure
	 * @param code the failure code
	 * @param severity the severity
	 * @param arguments named failure arguments
	 * @param defaultMessage the default message text
	 */
	public ValidationFailure(String code, Severity severity, Map arguments, String defaultMessage) {
		this.code = code;
		this.severity = severity;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * The failure code, uniquely identifying the validation constraint that failed.
	 */
	public String getCode() {
		return code;
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
