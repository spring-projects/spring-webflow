/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.faces.webflow.el;

import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.DelegatingVariableResolver;
import org.springframework.web.jsf.FacesContextUtils;
import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom property resolver that resolves flow session scope attributes of the current flow execution. This resolver
 * will also create and set the attribute value to a bean from the root Spring Web Application Context if the value does
 * not already exist, allowing for lazy-initialized binding variables.
 * 
 * Designed mainly to be used with the {@link FlowVariableResolver}. This is the original property resolver implemented
 * with Spring Web Flow 1.0. In general, prefer {@link DelegatingFlowVariableResolver} or
 * {@link FlowExecutionVariableResolver} over use of this class. Also, consider use of the
 * {@link DelegatingVariableResolver} as an alternative to accessing lazy-initialized binding variables managed by a
 * Spring application context that uses custom bean scopes.
 * 
 * @author Colin Sampaleanu
 * @author Keith Donald
 */
public class FlowPropertyResolver extends AbstractFlowExecutionPropertyResolver {

	/**
	 * Creates a new flow execution property resolver that resolves flow scope attributes.
	 * @param resolverDelegate the resolver to delegate to when the property is not a flow execution attribute
	 */
	public FlowPropertyResolver(PropertyResolver resolverDelegate) {
		super(resolverDelegate);
	}

	protected Class doGetAttributeType(FlowExecution execution, String attributeName) {
		// we want to access flow scope of the active session (conversation)
		Object value = execution.getActiveSession().getScope().get(attributeName);
		// note that MyFaces returns Object.class for a null value here, but
		// as I read the JSF spec, null should be returned when the object
		// type can not be determined this certainly seems to be the case
		// for a map value which doesn' even exist
		return (value == null) ? null : value.getClass();
	}

	protected Object doGetAttribute(FlowExecution execution, String attributeName) {
		Object value = execution.getActiveSession().getScope().get(attributeName);
		if (value == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Assert.notNull(facesContext, "The current FacesContext must be present during property resolution stage");
			BeanFactory beanFactory = getWebApplicationContext(facesContext);
			if (beanFactory.containsBean(attributeName)) {
				// note: this resolver doesn't care, but this should be
				// a stateless bean with singleton scope or a stateful bean with prototype scope
				value = beanFactory.getBean(attributeName);
				execution.getActiveSession().getScope().put(attributeName, value);
			}
		}
		return value;
	}

	protected void doSetAttribute(FlowExecution execution, String attributeName, Object attributeValue) {
		execution.getActiveSession().getScope().put(attributeName, attributeValue);
	}

	/**
	 * Retrieve the web application context to delegate bean name resolution to. Default implementation delegates to
	 * FacesContextUtils.
	 * @param facesContext the current JSF context
	 * @return the Spring web application context (never <code>null</code>)
	 * @see FacesContextUtils#getRequiredWebApplicationContext
	 */
	protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}
}