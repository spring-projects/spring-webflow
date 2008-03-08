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
package org.springframework.faces.webflow;

import javax.el.CompositeELResolver;
import javax.faces.el.VariableResolver;

import org.springframework.faces.expression.ELDelegatingVariableResolver;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;
import org.springframework.webflow.expression.el.SpringSecurityELResolver;

/**
 * For resolving flow request context variables with JSF 1.1 or >.
 * 
 * @author Jeremy Grelle
 */
public class FlowVariableResolver extends ELDelegatingVariableResolver {

	private static final CompositeELResolver composite = new CompositeELResolver();

	static {
		composite.add(new RequestContextELResolver());
		composite.add(new ImplicitFlowVariableELResolver());
		composite.add(new ScopeSearchingELResolver());
		if (ClassUtils.isPresent("org.springframework.security.context.SecurityContextHolder")) {
			composite.add(new SpringSecurityELResolver());
		}
		composite.add(new SpringBeanWebFlowELResolver());
	}

	public FlowVariableResolver(VariableResolver nextResolver) {
		super(nextResolver, composite);
	}
}
