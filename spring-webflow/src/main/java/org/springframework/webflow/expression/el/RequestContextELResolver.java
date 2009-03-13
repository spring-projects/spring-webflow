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
import javax.el.PropertyNotWritableException;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that resolves the current RequestContext under the variable {@link #REQUEST_CONTEXT_VARIABLE_NAME}.
 * Allows for accessing any property of the RequestContext instance. For example:
 * "#{flowRequestContext.conversationScope.myProperty}".
 * 
 * @author Jeremy Grelle
 */
public class RequestContextELResolver extends ELResolver {

	/**
	 * Name of the request context variable.
	 */
	public static final String REQUEST_CONTEXT_VARIABLE_NAME = "flowRequestContext";

	private RequestContext context;

	public RequestContextELResolver() {
	}

	public RequestContextELResolver(RequestContext context) {
		this.context = context;
	}

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		if (base == null) {
			return RequestContext.class;
		}
		return null;
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return RequestContext.class;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return getRequestContext();
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			throw new PropertyNotWritableException("The RequestContext cannot be set with an expression.");
		}
	}

	protected RequestContext getRequestContext() {
		return context != null ? context : RequestContextHolder.getRequestContext();
	}

}