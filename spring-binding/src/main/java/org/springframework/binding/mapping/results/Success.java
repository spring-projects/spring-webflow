package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;
import org.springframework.core.style.ToStringCreator;

/**
 * Indicates a successful mapping operation.
 * @author Keith Donald
 */
public class Success extends Result {

	private Object mappedValue;

	private Object originalValue;

	/**
	 * Creates a new success result.
	 * @param mappedValue the successfully mapped value
	 * @param originalValue the original value
	 */
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

	public String toString() {
		return new ToStringCreator(this).append("originalValue", originalValue).append("mappedValue", mappedValue)
				.toString();
	}
}
