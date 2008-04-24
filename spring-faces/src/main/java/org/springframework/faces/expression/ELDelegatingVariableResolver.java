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
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

/**
 * A JSF 1.1 {@link VariableResolver} that delegates to a wrapped Unified EL resolver chain for variable resolution.
 * 
 * @author Jeremy Grelle
 */
public abstract class ELDelegatingVariableResolver extends VariableResolver {

	private VariableResolver nextResolver;

	private ELContext elContext;

	public ELDelegatingVariableResolver(VariableResolver nextResolver, ELResolver delegate) {
		this.nextResolver = nextResolver;
		this.elContext = new SimpleELContext(delegate);
	}

	public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException {
		Object result = elContext.getELResolver().getValue(elContext, null, name);
		if (elContext.isPropertyResolved()) {
			return result;
		} else {
			return nextResolver.resolveVariable(facesContext, name);
		}
	}
}
