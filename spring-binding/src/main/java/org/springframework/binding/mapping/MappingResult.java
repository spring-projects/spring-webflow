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

import org.springframework.core.style.ToStringCreator;

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

	public String toString() {
		return new ToStringCreator(this).append("mapping", mapping).append("result", result).toString();
	}
}
