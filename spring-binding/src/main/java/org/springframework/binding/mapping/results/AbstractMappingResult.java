/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.core.style.ToStringCreator;

/**
 * Convenient base class for {@link MappingResult} implementations.
 * 
 * @author Keith Donald
 */
public abstract class AbstractMappingResult implements MappingResult {

	private transient Mapping mapping;


	/**
	 * Creates a new mapping result.
	 * @param mapping the mapping this result is for.
	 */
	public AbstractMappingResult(Mapping mapping) {
		this.mapping = mapping;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public abstract String getCode();

	public abstract boolean isError();

	public abstract Throwable getErrorCause();

	public abstract Object getOriginalValue();

	public abstract Object getMappedValue();

	public String toString() {
		ToStringCreator creator = new ToStringCreator(this).append("mapping", mapping).append("code", getCode())
				.append("error", isError());
		if (isError()) {
			creator.append("errorCause", getErrorCause());
		}
		creator.append("originalValue", getOriginalValue());
		creator.append("mappedValue", getMappedValue());
		return creator.toString();
	}

}