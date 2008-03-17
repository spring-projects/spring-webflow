package org.springframework.binding.mapping;

/**
 * Exposes information about the result of a single mapping operation.
 * @author Keith Donald
 */
public abstract class Result {

	/**
	 * The original value of the source object that was to be mapped. Null if this result is an error on the source
	 * object.
	 */
	public abstract Object getOriginalValue();

	/**
	 * The actual value that was mapped to the target object. Null if this result is an error.
	 */
	public abstract Object getMappedValue();

	/**
	 * Indicates if this result was an error.
	 */
	public abstract boolean isError();

	/**
	 * If this result was an error, the logical mapping error code; for example "propertyNotFound". Null if this result
	 * is not an error result.
	 */
	public abstract String getErrorCode();
}
