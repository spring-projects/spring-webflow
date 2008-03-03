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
package org.springframework.webflow.engine.support;

import java.io.Serializable;

import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.core.style.ToStringCreator;

/**
 * Simple flow attribute mapper that holds an input and output mapper strategy.
 * 
 * @author Keith Donald
 */
public final class GenericSubflowAttributeMapper extends AbstractSubflowAttributeMapper implements Serializable {

	private final AttributeMapper inputMapper;

	private final AttributeMapper outputMapper;

	/**
	 * Create a new flow attribute mapper using given mapping strategies.
	 * @param inputMapper the input mapping strategy
	 * @param outputMapper the output mapping strategy
	 */
	public GenericSubflowAttributeMapper(AttributeMapper inputMapper, AttributeMapper outputMapper) {
		this.inputMapper = inputMapper;
		this.outputMapper = outputMapper;
	}

	protected AttributeMapper getInputMapper() {
		return inputMapper;
	}

	protected AttributeMapper getOutputMapper() {
		return outputMapper;
	}

	public String toString() {
		return new ToStringCreator(this).append("inputMapper", inputMapper).append("outputMapper", outputMapper)
				.toString();
	}
}