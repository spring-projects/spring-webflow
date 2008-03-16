package org.springframework.binding.mapping.results;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.mapping.Result;

public class SourceAccessError extends Result {

	private EvaluationException error;

	public SourceAccessError(EvaluationException error) {
		this.error = error;
	}

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
		return "sourceAccess";
	}

}
