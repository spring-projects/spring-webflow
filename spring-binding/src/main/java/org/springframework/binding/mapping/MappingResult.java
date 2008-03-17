package org.springframework.binding.mapping;

/**
 * A single mapping result within a {@link MappingResults} transaction.
 */
public class MappingResult {

	private Mapping mapping;

	private Result result;

	/**
	 * Creates a new mapping result.
	 * @param mapping the mapping that executed
	 * @param result the result of executing the mapping
	 */
	public MappingResult(Mapping mapping, Result result) {
		this.mapping = mapping;
		this.result = result;
	}

	/**
	 * Returns the mapping that executed.
	 */
	public Mapping getMapping() {
		return mapping;
	}

	/**
	 * Returns the result of executing the mapping.
	 */
	public Result getResult() {
		return result;
	}

}
