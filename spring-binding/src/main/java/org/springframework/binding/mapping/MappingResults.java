package org.springframework.binding.mapping;

import java.util.List;

/**
 * Exposes information about the results of a mapping transaction.
 * 
 * @author Keith Donald
 */
public interface MappingResults {

	/**
	 * The source object that was mapped from.
	 */
	public Object getSource();

	/**
	 * The target object that was mapped to.
	 */
	public Object getTarget();

	/**
	 * A list of all the mapping results between the source and target.
	 */
	public List getAllResults();

	/**
	 * Whether some results were errors. Returns true if mapping errors occurred.
	 */
	public boolean hasErrorResults();

	/**
	 * A list of all error results that occurred.
	 */
	public List getErrorResults();

	/**
	 * Get all results that meet the given result criteria.
	 * @param criteria the mapping result criteria
	 */
	public List getResults(MappingResultsCriteria criteria);

}