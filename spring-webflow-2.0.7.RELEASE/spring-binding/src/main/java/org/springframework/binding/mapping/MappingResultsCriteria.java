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
