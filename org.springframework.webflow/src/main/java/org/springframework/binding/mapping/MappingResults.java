/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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