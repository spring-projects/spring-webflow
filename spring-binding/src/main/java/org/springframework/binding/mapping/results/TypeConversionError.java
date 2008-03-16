package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;

public class TypeConversionError extends Result {

	private Object originalValue;

	private Class targetType;

	public TypeConversionError(Object originalValue, Class targetType) {
		this.originalValue = originalValue;
		this.targetType = targetType;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Class getTargetType() {
		return targetType;
	}

	public Object getMappedValue() {
		return null;
	}

	public boolean isError() {
		return true;
	}

	public String getErrorCode() {
		return "typeMismatch";
	}

}
