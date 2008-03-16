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
package org.springframework.binding.mapping.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.style.ToStringCreator;

/**
 * Generic attributes mapper implementation that allows mappings to be configured programatically.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 * @author Colin Sampaleanu
 */
public class DefaultMapper implements Mapper {

	/**
	 * The ordered list of mappings to apply.
	 */
	private List mappings = new LinkedList();

	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Add a mapping to this mapper.
	 * @param mapping the mapping to add
	 * @return this, to support convenient call chaining
	 */
	public DefaultMapper addMapping(DefaultMapping mapping) {
		mappings.add(mapping);
		return this;
	}

	/**
	 * Returns this mapper's list of mappings.
	 * @return the list of mappings
	 */
	public Mapping[] getMappings() {
		return (Mapping[]) mappings.toArray(new Mapping[mappings.size()]);
	}

	public MappingResults map(Object source, Object target) {
		DefaultMappingContext context = new DefaultMappingContext(source, target, conversionService);
		Iterator it = mappings.iterator();
		while (it.hasNext()) {
			DefaultMapping mapping = (DefaultMapping) it.next();
			mapping.map(context);
		}
		return context.toResult();
	}

	public String toString() {
		return new ToStringCreator(this).append("mappings", mappings).toString();
	}
}