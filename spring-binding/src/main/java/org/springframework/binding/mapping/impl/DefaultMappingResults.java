/*
 * Copyright 2004-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.mapping.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;

/**
 * Default mapping results implementation.
 * @author Keith Donald
 */
public class DefaultMappingResults implements MappingResults {

	private transient Object source;

	private transient Object target;

	private List<MappingResult> mappingResults;

	/**
	 * Creates a new mapping results object.
	 * @param source the source
	 * @param target the target
	 * @param mappingResults the actual results produced by {@link DefaultMapper}
	 */
	public DefaultMappingResults(Object source, Object target, List<MappingResult> mappingResults) {
		this.source = source;
		this.target = target;
		this.mappingResults = mappingResults;
	}

	public Object getSource() {
		return source;
	}

	public Object getTarget() {
		return target;
	}

	public List<MappingResult> getAllResults() {
		return Collections.unmodifiableList(mappingResults);
	}

	public boolean hasErrorResults() {
		for (MappingResult result : mappingResults) {
			if (result.isError()) {
				return true;
			}
		}
		return false;
	}

	public List<MappingResult> getErrorResults() {
		List<MappingResult> errorResults = new ArrayList<>();
		for (MappingResult result : mappingResults) {
			if (result.isError()) {
				errorResults.add(result);
			}
		}
		return Collections.unmodifiableList(errorResults);
	}

	public List<MappingResult> getResults(MappingResultsCriteria criteria) {
		List<MappingResult> results = new ArrayList<>();
		for (MappingResult result : mappingResults) {
			if (criteria.test(result)) {
				results.add(result);
			}
		}
		return Collections.unmodifiableList(results);
	}

	public String toString() {
		return "Mapping Results = " + mappingResults.toString();
	}
}
