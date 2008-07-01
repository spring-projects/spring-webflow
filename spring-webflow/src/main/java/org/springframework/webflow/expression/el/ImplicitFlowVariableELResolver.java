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
package org.springframework.webflow.expression.el;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.binding.expression.el.DefaultELContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Resolves "implicit" or well-known flow variables; for example "flowScope" in an expression like #{flowScope.foo}. The
 * list of implicit flow variables consists of:
 * 
 * <pre>
 * requestParameters
 * requestScope
 * flashScope
 * viewScope
 * flowScope
 * conversationScope
 * messageContext
 * externalContext
 * flowExecutionContext
 * flowExecutionUrl
 * currentUser
 * currentEvent
 * </pre>
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class ImplicitFlowVariableELResolver extends ELResolver {

	private RequestContext requestContext;

	public ImplicitFlowVariableELResolver() {
	}

	public ImplicitFlowVariableELResolver(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Class getCommonPropertyType(ELContext context, Object base) {
		if (base == null) {
			return Object.class;
		} else {
			return null;
		}
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return ImplicitVariables.value(context, requestContext, property).getClass();
		} else {
			return null;
		}
	}

	public Object getValue(ELContext context, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return ImplicitVariables.value(context, requestContext, property);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base != null) {
			return false;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base != null) {
			return;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			throw new PropertyNotWritableException("The implicit flow variable " + property + " is not writable.");
		}
	}

	protected RequestContext getRequestContext() {
		return requestContext != null ? requestContext : RequestContextHolder.getRequestContext();
	}

	private static final class ImplicitVariables {
		private static final Map vars = new HashMap();

		private static final PropertyResolver requestContextResolver = new PropertyResolver() {
			protected Object doResolve(ELContext elContext, RequestContext requestContext, Object property) {
				return elContext.getELResolver().getValue(elContext, requestContext, property);
			}
		};

		private static final PropertyResolver externalContextResolver = new PropertyResolver() {
			protected Object doResolve(ELContext elContext, RequestContext requestContext, Object property) {
				return elContext.getELResolver().getValue(elContext, requestContext.getExternalContext(), property);
			}
		};

		private static final PropertyResolver currentEventResolver = new PropertyResolver() {
			protected Object doResolve(ELContext elContext, RequestContext requestContext, Object property) {
				return requestContext.getCurrentEvent();
			}
		};

		static {
			vars.put("requestParameters", requestContextResolver);
			vars.put("requestScope", requestContextResolver);
			vars.put("flashScope", requestContextResolver);
			vars.put("viewScope", requestContextResolver);
			vars.put("flowScope", requestContextResolver);
			vars.put("conversationScope", requestContextResolver);
			vars.put("messageContext", requestContextResolver);
			vars.put("externalContext", requestContextResolver);
			vars.put("flowExecutionContext", requestContextResolver);
			vars.put("flowExecutionUrl", requestContextResolver);
			vars.put("currentUser", externalContextResolver);
			vars.put("currentEvent", currentEventResolver);
		}

		public static boolean matches(Object property) {
			return vars.containsKey(property);
		}

		public static Object value(ELContext elContext, RequestContext requestContext, Object property) {
			PropertyResolver resolver = (PropertyResolver) vars.get(property);
			return resolver.resolve(requestContext, property);
		}
	}

	private static abstract class PropertyResolver {

		private static final BeanELResolver elPropertyResolver = new BeanELResolver();

		public Object resolve(RequestContext context, Object property) {
			ELContext elContext = new DefaultELContext(elPropertyResolver, null, null);
			return doResolve(elContext, context, property);
		}

		protected abstract Object doResolve(ELContext elContext, RequestContext requestContext, Object property);
	}
}
