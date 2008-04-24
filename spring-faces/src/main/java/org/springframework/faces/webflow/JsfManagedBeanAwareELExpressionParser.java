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

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.el.ActionMethodELResolver;
import org.springframework.webflow.expression.el.FlowResourceELResolver;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;

/**
 * A JSF-specific ExpressionParser that allows beans managed by either JSF, Spring, or Web Flow to be referenced in
 * expressions in the FlowDefinition.
 * 
 * @author Jeremy Grelle
 */
public class JsfManagedBeanAwareELExpressionParser extends ELExpressionParser {

	public JsfManagedBeanAwareELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory);
		putContextFactory(RequestContext.class, new RequestContextELContextFactory());
	}

	private static class RequestContextELContextFactory implements ELContextFactory {
		public ELContext getELContext(Object target) {
			RequestContext context = (RequestContext) target;
			List customResolvers = new ArrayList();
			customResolvers.add(new RequestContextELResolver(context));
			customResolvers.add(new FlowResourceELResolver(context));
			customResolvers.add(new ImplicitFlowVariableELResolver(context));
			customResolvers.add(new ScopeSearchingELResolver(context));
			customResolvers.add(new SpringBeanWebFlowELResolver(context));
			customResolvers.add(new ActionMethodELResolver());
			customResolvers.add(new JsfManagedBeanResolver());
			ELResolver resolver = new DefaultELResolver(customResolvers);
			return new WebFlowELContext(resolver);
		}
	}

	private static class WebFlowELContext extends ELContext {

		private ELResolver resolver;

		public WebFlowELContext(ELResolver resolver) {
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

}
