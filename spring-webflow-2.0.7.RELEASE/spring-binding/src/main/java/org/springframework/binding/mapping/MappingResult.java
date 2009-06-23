package org.springframework.binding.mapping;

/**
 * A single data mapping result. Each result has a unique {@link #getCode() code}, and provides context about the result
 * of a single data mapping operation.
 * 
 * @author Keith Donald
 */
public interface MappingResult {

	/**
	 * The mapping that executed for which this result pertains to.
	 */
	public Mapping getMapping();

	/**
	 * The mapping result code; for example, "success" , "typeMismatch", "propertyNotFound", or "evaluationException".
	 */
	public String getCode();

	/**
	 * Indicates if this result is an error result.
	 */
	public boolean isError();

	/**
	 * Get the cause of the error result
	 * @return the underyling cause, or null if this is not an error or there was no root cause.
	 */
	public Throwable getErrorCause();

	/**
	 * The original value of the source object that was to be mapped. May be null if this result is an error on the
	 * source object.
	 */
	public Object getOriginalValue();

	/**
	 * The actual value that was mapped to the target object. Null if this result is an error.
	 */
	public Object getMappedValue();

}