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
package org.springframework.webflow.engine.support;

import java.io.Serializable;

import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.FlowInputMappingException;
import org.springframework.webflow.engine.FlowOutputMappingException;
import org.springframework.webflow.engine.SubflowAttributeMapper;
import org.springframework.webflow.execution.RequestContext;

/**
 * Simple flow attribute mapper that holds an input and output mapper strategy.
 * 
 * @author Keith Donald
 */
public final class GenericSubflowAttributeMapper implements SubflowAttributeMapper, Serializable {

	private final Mapper inputMapper;

	private final Mapper outputMapper;

	/**
	 * Create a new flow attribute mapper using given mapping strategies.
	 * @param inputMapper the input mapping strategy
	 * @param outputMapper the output mapping strategy
	 */
	public GenericSubflowAttributeMapper(Mapper inputMapper, Mapper outputMapper) {
		this.inputMapper = inputMapper;
		this.outputMapper = outputMapper;
	}

	public MutableAttributeMap createSubflowInput(RequestContext context) {
		if (inputMapper != null) {
			LocalAttributeMap input = new LocalAttributeMap();
			MappingResults results = inputMapper.map(context, input);
			if (results != null && results.hasErrorResults()) {
				throw new FlowInputMappingException(context.getActiveFlow().getId(), context.getCurrentState().getId(),
						results);
			}
			return input;
		} else {
			return new LocalAttributeMap();
		}
	}

	public void mapSubflowOutput(AttributeMap output, RequestContext context) {
		if (outputMapper != null && output != null) {
			MappingResults results = outputMapper.map(output, context);
			if (results != null && results.hasErrorResults()) {
				throw new FlowOutputMappingException(context.getActiveFlow().getId(),
						context.getCurrentState().getId(), results);
			}
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("inputMapper", inputMapper).append("outputMapper", outputMapper)
				.toString();
	}
}