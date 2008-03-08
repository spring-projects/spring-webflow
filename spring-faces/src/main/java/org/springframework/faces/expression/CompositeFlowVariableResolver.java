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

import javax.el.CompositeELResolver;
import javax.faces.el.VariableResolver;

import org.springframework.util.ClassUtils;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;
import org.springframework.webflow.expression.el.SpringSecurityELResolver;

/**
 * Assembles {@link RequestContextELResolver} and {@link ScopeSearchingELResolver} into a composite that may be used
 * with JSF 1.1 and higher for variable resolution.
 * 
 * @author Jeremy Grelle
 */
public class CompositeFlowVariableResolver extends ELDelegatingVariableResolver {

	private static final CompositeELResolver composite = new CompositeELResolver();

	static {
		composite.add(new RequestContextELResolver());
		if (ClassUtils.isPresent("org.springframework.security.context.SecurityContextHolder")) {
			composite.add(new SpringSecurityELResolver());
		}
		composite.add(new SpringBeanWebFlowELResolver());
		composite.add(new ImplicitFlowVariableELResolver());
		composite.add(new ScopeSearchingELResolver());
	}

	public CompositeFlowVariableResolver(VariableResolver nextResolver) {
		super(nextResolver, composite);
	}
}
