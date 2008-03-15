/*
 * Copyright 2004-2007 the original author or authors.
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

/**
 * Generic attributes mapper implementation that allows mappings to be configured programatically.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 * @author Colin Sampaleanu
 */
public class DefaultAttributeMapper implements AttributeMapper {

	/**
	 * The ordered list of mappings to apply.
	 */
	private List mappings = new LinkedList();

	/**
	 * Add a mapping to this mapper.
	 * @param mapping the mapping to add
	 * @return this, to support convenient call chaining
	 */
	public DefaultAttributeMapper addMapping(Mapping mapping) {
		mappings.add(mapping);
		return this;
	}

	/**
	 * Add a set of mappings.
	 * @param mappings the mappings
	 */
	public void addMappings(Mapping[] mappings) {
		if (mappings == null) {
			return;
		}
		this.mappings.addAll(Arrays.asList(mappings));
	}

	/**
	 * Returns this mapper's list of mappings.
	 * @return the list of mappings
	 */
	public Mapping[] getMappings() {
		return (Mapping[]) mappings.toArray(new Mapping[mappings.size()]);
	}

	public void map(Object source, Object target, MappingContext context) throws AttributeMappingException {
		boolean mappingFailure = false;
		if (mappings != null) {
			Iterator it = mappings.iterator();
			while (it.hasNext()) {
				Mapping mapping = (Mapping) it.next();
				boolean result = mapping.map(source, target, context);
				if (!result && !mappingFailure) {
					mappingFailure = true;
				}
			}
		}
		if (mappingFailure) {
			throw new AttributeMappingException();
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("mappings", mappings).toString();
	}
}