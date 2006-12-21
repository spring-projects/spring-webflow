/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;

import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom property resolve that resolves active flow session scope properties
 * from a thread bound flow execution.
 * <p>
 * TODO: this class probably needs to delegate to a strategy object pulled out
 * of the appcontext, to provide ability to override and configure, as JSF
 * provides no other way to customize and configure this instance.
 * 
 * @author Colin Sampaleanu
 */
public class FlowPropertyResolver extends PropertyResolver {

	/**
	 * The standard property resolver to delegate to if this one doesn't apply.
	 */
	private final PropertyResolver resolverDelegate;

	/**
	 * Create a new PropertyResolver, using the given original PropertyResolver.
	 * <p>
	 * A JSF implementation will automatically pass its original resolver into
	 * the constructor of a configured resolver, provided that there is a
	 * corresponding constructor argument.
	 * 
	 * @param resolverDelegate the original VariableResolver
	 */
	public FlowPropertyResolver(PropertyResolver resolverDelegate) {
		this.resolverDelegate = resolverDelegate;
	}

	public Class getType(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getType(base, index);
		}
		else {
			// can't access flow scope by index, so can't determine type. Return
			// null per JSF spec
			return null;
		}
	}

	public Class getType(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getType(base, property);
		}
		if (property == null) {
			throw new PropertyNotFoundException("Unable to get value from Flow, as property (key) is null");
		}
		if (!(property instanceof String)) {
			throw new PropertyNotFoundException("Unable to get value from Flow map, as key is non-String");
		}
		FlowExecution execution = (FlowExecution)base;
		// we want to access flow scope of the active session (conversation)
		Object value = execution.getActiveSession().getScope().get((String)property);
		// note that MyFaces returns Object.class for a null value here, but
		// as I read the JSF spec, null should be returned when the object
		// type can not be determined this certainly seems to be the case
		// for a map value which doesn' even exist
		return (value == null) ? null : value.getClass();
	}

	public Object getValue(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getValue(base, index);
		}
		else {
			throw new ReferenceSyntaxException("Cannot apply an index value to Flow map");
		}
	}

	public Object getValue(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getValue(base, property);
		}
		if (!(property instanceof String)) {
			throw new PropertyNotFoundException("Unable to get value from Flow map, as key is non-String");
		}
		FlowExecution execution = (FlowExecution)base;
		String attributeName = (String)property;
		Object value = execution.getActiveSession().getScope().get(attributeName);
		if (value == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Assert.notNull(context, "FacesContext must exist during property resolution stage");
			WebApplicationContext wac = getWebApplicationContext(context);
			if (wac.containsBean(attributeName)) {
				// note: this resolver doesn't care, but this should normally be
				// either a stateless singleton bean, or a stateful/stateless
				// prototype
				value = wac.getBean(attributeName);
				execution.getActiveSession().getScope().put(attributeName, value);
			}
		}
		return value;
	}

	public boolean isReadOnly(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.isReadOnly(base, index);
		}
		return false;
	}

	public boolean isReadOnly(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.isReadOnly(base, property);
		}
		return false;
	}

	public void setValue(Object base, int index, Object value) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			resolverDelegate.setValue(base, index, value);
		}
		throw new ReferenceSyntaxException("Can not apply an index value to Flow map");
	}

	public void setValue(Object base, Object property, Object value) throws EvaluationException,
			PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			resolverDelegate.setValue(base, property, value);
			return;
		}
		if (property == null || !(property instanceof String)
				|| (property instanceof String && ((String)property).length() == 0)) {
			throw new PropertyNotFoundException(
					"Attempt to set Flow attribute with null name, empty name, or non-String name");
		}
		FlowExecution execution = (FlowExecution)base;
		execution.getActiveSession().getScope().put((String)property, value);
	}

	/**
	 * Retrieve the web application context to delegate bean name resolution to.
	 * <p>
	 * Default implementation delegates to FacesContextUtils.
	 * 
	 * @param facesContext the current JSF context
	 * @return the Spring web application context (never <code>null</code>)
	 * @see FacesContextUtils#getRequiredWebApplicationContext
	 */
	protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}
}