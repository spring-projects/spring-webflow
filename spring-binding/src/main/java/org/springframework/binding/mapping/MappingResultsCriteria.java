package org.springframework.binding.mapping;

/**
 * A predicate used to select mapping result objects in a call to
 * {@link MappingResults#getResults(MappingResultsCriteria)}.
 * @author Keith Donald
 */
public interface MappingResultsCriteria {

	/**
	 * Tests if the mapping result meets this criteria.
	 * @param result the result
	 * @return true if so, false if not
	 */
	public boolean test(MappingResult result);
}
