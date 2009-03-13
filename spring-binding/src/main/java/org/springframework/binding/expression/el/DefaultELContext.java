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
package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * A generic ELContext implementation.
 * 
 * @author Keith Donald
 */
public class DefaultELContext extends ELContext {

	private VariableMapper variableMapper;

	private ELResolver resolver;

	private FunctionMapper functionMapper;

	/**
	 * Creates a new default el context.
	 * @param resolver the el resolver to use
	 * @param variableMapper the variable mappter
	 * @param functionMapper the function mapper
	 */
	public DefaultELContext(ELResolver resolver, VariableMapper variableMapper, FunctionMapper functionMapper) {
		this.resolver = resolver;
		this.variableMapper = variableMapper;
		this.functionMapper = functionMapper;
	}

	public ELResolver getELResolver() {
		return resolver;
	}

	public VariableMapper getVariableMapper() {
		return variableMapper;
	}

	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}

}
