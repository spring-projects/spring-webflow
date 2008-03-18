package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;
import org.springframework.core.style.ToStringCreator;

/**
 * Indicates a type conversion occurred during a mapping operation.
 * 
 * @author Keith Donald
 */
public class TypeConversionError extends Result {

	private Object originalValue;

	private Class targetType;

	/**
	 * Creates a new type conversion error.
	 * @param originalValue the value that could not be converted
	 * @param targetType the target type of the conversion
	 */
	public TypeConversionError(Object originalValue, Class targetType) {
		this.originalValue = originalValue;
		this.targetType = targetType;
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
		return "typeMismatch";
	}

	// impl

	/**
	 * Returns the target type of the conversion attempt.
	 */
	public Class getTargetType() {
		return targetType;
	}

	public String toString() {
		return new ToStringCreator(this).append("originalValue", originalValue).append("targetType", targetType)
				.toString();
	}
}
