package org.springframework.binding.mapping.results;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.mapping.Result;
import org.springframework.core.style.ToStringCreator;

/**
 * Indicates an exception occurred accessing the source object to be mapped. Used to report source
 * {@link PropertyNotFoundException} errors and general {@link EvaluationException} errors.
 * @author Keith Donald
 */
public class SourceAccessError extends Result {

	private EvaluationException error;

	/**
	 * Creates a new source access error.
	 * @param error the underlying evaluation exception that occurred
	 */
	public SourceAccessError(EvaluationException error) {
		this.error = error;
	}

	/**
	 * Returns the backing source evaluation exception that occurred.
	 */
	public EvaluationException getException() {
		return error;
	}

	public Object getOriginalValue() {
		return null;
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
			return "evaluationException";
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("errorCode", getErrorCode()).append("details", error.getMessage())
				.toString();
	}
}
