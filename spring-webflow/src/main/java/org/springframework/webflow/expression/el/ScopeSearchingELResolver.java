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

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that searches the current request context for variables to resolve. The search algorithm looks in
 * request scope first, then flash scope, then flow scope, then conversation scope.
 * 
 * Suitable for use along side other variable resolvers to support EL binding expressions like "#{bean.property}" where
 * "bean" could be a property in any supported scope.
 * 
 * @author Jeremy Grelle
 */
public class ScopeSearchingELResolver extends ELResolver {

	private RequestContext requestContext;

	public ScopeSearchingELResolver() {
	}

	public ScopeSearchingELResolver(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		if (base == null) {
			return Object.class;
		} else {
			return null;
		}
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(attributeName).getClass();
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(attributeName).getClass();
		} else if (requestContext.inViewState() && requestContext.getViewScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getViewScope().get(attributeName).getClass();
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(attributeName).getClass();
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(attributeName).getClass();
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(attributeName);
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(attributeName);
		} else if (requestContext.inViewState() && requestContext.getViewScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getViewScope().get(attributeName);
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(attributeName);
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(attributeName);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return false;
		}
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.inViewState() && requestContext.getViewScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return;
		}
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getRequestScope().put(attributeName, value);
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlashScope().put(attributeName, value);
		} else if (requestContext.inViewState() && requestContext.getViewScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getViewScope().put(attributeName, value);
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlowScope().put(attributeName, value);
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getConversationScope().put(attributeName, value);
		}
	}

	protected RequestContext getRequestContext() {
		return requestContext != null ? requestContext : RequestContextHolder.getRequestContext();
	}

}