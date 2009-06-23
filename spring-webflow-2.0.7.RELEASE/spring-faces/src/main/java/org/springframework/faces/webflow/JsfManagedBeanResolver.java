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

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * ELResolver that checks request, session, and application scopes for existing JSF-managed beans. This allows
 * traditional JSF-managed beans (defined in faces-config.xml) to be resolved through expressions in a flow definition.
 * The preferred approach is to instead use Spring to configure such beans, but this is meant to ease migration for
 * users with existing JSF artifacts. This resolver will delegate to a temporary FacesContext so that JSF managed bean
 * initialization will be triggered if the bean has not already been initialized by JSF.
 * 
 * @author Jeremy Grelle
 */
public class JsfManagedBeanResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null) {
			Object bean = getFacesBean(property);
			if (bean != null) {
				context.setPropertyResolved(true);
				return bean.getClass();
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			Object bean = getFacesBean(property);
			if (bean != null) {
				context.setPropertyResolved(true);
				return bean;
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
			}
		}
		return false;
	}

	/**
	 * Sets a bean value if a corresponding key is found in one of the ExternalContext scopes.
	 */
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

	/**
	 * This resolver is only meant to be called from the Flow Execution, thus it assumes that the FacesContext will not
	 * be available and creates a temporary one on the fly.
	 * @return The initialized FacesContext.
	 */
	private FacesContext getFacesContext() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Assert.notNull(requestContext, "RequestContext cannot be null - This resolver is only intended to be invoked "
				+ "from an active Flow Execution.");
		FacesContext facesContext = FlowFacesContext.newInstance(requestContext, FlowLifecycle.newInstance());
		return facesContext;
	}

	/**
	 * Uses a temporary FacesContext to try and resolve a JSF Managed Bean
	 * @param beanName - The name of the bean to resolve.
	 * @return The JSF Managed Bean instance if found.
	 */
	private Object getFacesBean(Object beanName) {
		FacesContext facesContext = getFacesContext();
		Object result = null;
		try {
			ValueBinding vb = facesContext.getApplication().createValueBinding("#{" + beanName + "}");
			result = vb.getValue(facesContext);
		} finally {
			facesContext.release();
		}
		return result;
	}
}
