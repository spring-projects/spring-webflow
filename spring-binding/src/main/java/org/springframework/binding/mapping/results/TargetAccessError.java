package org.springframework.binding.mapping.results;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.mapping.Result;

/**
 * Indicates an exception occurred accessing the target object to be mapped to. Used to report source
 * {@link PropertyNotFoundException} errors and general {@link EvaluationException} errors.
 * @author Keith Donald
 */
public class TargetAccessError extends Result {

	private Object originalValue;

	private EvaluationException error;

	/**
	 * Creates a new target access error.
	 * @param originalValue the value that was attempted to be mapped
	 * @param error the underlying evaluation exception that occurred
	 */
	public TargetAccessError(Object originalValue, EvaluationException error) {
		this.originalValue = originalValue;
		this.error = error;
	}

	/**
	 * Returns the backing target evaluation exception that occurred.
	 */
	public EvaluationException getException() {
		return error;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Object getMappedValue() {
		return null;
	}

	public boolean isError() {
		return true;
	}

	public String getErrorCode() {
		if (error instanceof PropertyNotFoundException) {
			return "propertyNotFound";
		} else {
			return "targetAccess";
		}
	}
}
