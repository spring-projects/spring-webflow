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

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * ELResolver that checks request, session, and application scopes for existing JSF-managed beans. This allows
 * traditional JSF-managed beans (defined in faces-config.xml) to be resolved through expressions in a flow definition.
 * The preferred approach is to instead use Spring to configure such beans, but this is meant to ease migration for
 * users with existing JSF artifacts. Note that this resolver does not have access to the JSF managed bean definitions
 * and will not trigger the bean's instantiation if it has not already been initialized by JSF.
 * 
 * @author Jeremy Grelle
 */
public class LegacyJSFBeanResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getRequestMap().get(property.toString()).getClass();
			} else if (requestContext.getExternalContext().getSessionMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getSessionMap().get(property.toString()).getClass();
			} else if (requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getApplicationMap().get(property.toString()).getClass();
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getRequestMap().get(property.toString());
			} else if (requestContext.getExternalContext().getSessionMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getSessionMap().get(property.toString());
			} else if (requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return requestContext.getExternalContext().getApplicationMap().get(property.toString());
			}
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())
					|| requestContext.getExternalContext().getSessionMap().contains(property.toString())
					|| requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				return true;
			}
		}
		return false;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getRequestMap().put(property.toString(), value);
			} else if (requestContext.getExternalContext().getSessionMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getSessionMap().put(property.toString(), value);
			} else if (requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getApplicationMap().put(property.toString(), value);
			}
		}
	}

}
