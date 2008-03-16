package org.springframework.binding.mapping.results;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.mapping.Result;

public class TargetAccessError extends Result {

	private Object originalValue;

	private EvaluationException error;

	public TargetAccessError(Object originalValue, EvaluationException error) {
		this.originalValue = originalValue;
		this.error = error;
	}

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
