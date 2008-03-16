package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;

public class Success extends Result {

	private Object originalValue;
	private Object mappedValue;

	public Success(Object mappedValue, Object originalValue) {
		this.mappedValue = mappedValue;
		this.originalValue = originalValue;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Object getMappedValue() {
		return mappedValue;
	}

	public boolean isError() {
		return false;
	}

	public String getErrorCode() {
		return null;
	}
}
