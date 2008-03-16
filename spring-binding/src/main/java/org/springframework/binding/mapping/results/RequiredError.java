package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;

public class RequiredError extends Result {

	private Object originalValue;

	public RequiredError(Object originalValue) {
		this.originalValue = originalValue;
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
		return "required";
	}
}
