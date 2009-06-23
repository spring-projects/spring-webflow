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
package org.springframework.faces.expression;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * A minimal {@link ELContext} implementation.
 * 
 * @author Jeremy Grelle
 */
class SimpleELContext extends ELContext {

	private ELResolver resolver;

	public SimpleELContext(ELResolver resolver) {
		this.resolver = resolver;
	}

	public ELResolver getELResolver() {
		return resolver;
	}

	public FunctionMapper getFunctionMapper() {
		return null;
	}

	public VariableMapper getVariableMapper() {
		return null;
	}
}
