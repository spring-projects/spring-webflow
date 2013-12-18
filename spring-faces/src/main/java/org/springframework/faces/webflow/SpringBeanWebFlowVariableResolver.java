/*
 * Copyright 2004-2013 the original author or authors.
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

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * JSF 1.1 {@code VariableResolver} that delegates to the
 * flow's local Spring bean factory (for resolving Spring beans) and then
 * to the original resolver of the underlying JSF implementation
 * (for resolving managed-bean objects as defined in {@code faces-config.xml}
 * as well as well-known implicit EL attributes).
 *
 * <p>Configure this resolver in your {@code faces-config.xml} file as follows:
 *
 * <pre>
 * &lt;application>
 *   ...
 *   &lt;variable-resolver>org.springframework.faces.webflow.SpringBeanWebFlowVariableResolver&lt;/variable-resolver>
 * &lt;/application></pre>
 *
 * All your JSF expressions can then implicitly refer to the names of
 * Spring-managed service layer beans, for example in property values of
 * JSF-managed beans:
 *
 * <pre>
 * &lt;managed-bean>
 *   &lt;managed-bean-name>myJsfManagedBean&lt;/managed-bean-name>
 *   &lt;managed-bean-class>example.MyJsfManagedBean&lt;/managed-bean-class>
 *   &lt;managed-bean-scope>session&lt;/managed-bean-scope>
 *   &lt;managed-property>
 *     &lt;property-name>mySpringManagedBusinessObject&lt;/property-name>
 *     &lt;value>#{mySpringManagedBusinessObject}&lt;/value>
 *   &lt;/managed-property>
 * &lt;/managed-bean></pre>
 *
 * with "mySpringManagedBusinessObject" defined as Spring bean in
 * applicationContext.xml:
 *
 * <pre>
 * &lt;bean id="mySpringManagedBusinessObject" class="example.MySpringManagedBusinessObject">
 *   ...
 * &lt;/bean></pre>
 * 
 * @author Jeremy Grelle
 */
public class SpringBeanWebFlowVariableResolver extends VariableResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	protected final VariableResolver originalVariableResolver;


	public SpringBeanWebFlowVariableResolver(VariableResolver originalVariableResolver) {
		Assert.notNull(originalVariableResolver, "Original JSF VariableResolver must not be null");
		this.originalVariableResolver = originalVariableResolver;
	}

	/**
	 * Return the original JSF VariableResolver that this resolver delegates to.
	 * Used to resolve standard JSF-managed beans.
	 */
	protected final VariableResolver getOriginalVariableResolver() {
		return this.originalVariableResolver;
	}


	/**
	 * Try to resolve the variable as Spring bean in the flow local bean factory.
	 * Then delegate to the original VariableResolver.
	 */
	@Override
	public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException {
		Object bean = resolveSpringBean(facesContext, name);
		if (bean != null) {
			return bean;
		}
		Object value = resolveOriginal(facesContext, name);
		if (value != null) {
			return value;
		}
		return null;

	}

	/**
	 * Resolve the attribute via the original JSF VariableResolver.
	 */
	protected Object resolveOriginal(FacesContext facesContext, String name) {
		Object value = getOriginalVariableResolver().resolveVariable(facesContext, name);
		if (value != null && logger.isTraceEnabled()) {
			logger.trace("Successfully resolved variable '" + name + "' via original VariableResolver");
		}
		return value;
	}

	/**
	 * Resolve the attribute as a Spring bean in the ApplicationContext.
	 */
	protected Object resolveSpringBean(FacesContext facesContext, String name) {
		BeanFactory bf = getBeanFactory(facesContext);
		if (bf.containsBean(name)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Successfully resolved variable '" + name + "' in Spring BeanFactory");
			}
			return bf.getBean(name);
		}
		else {
			return null;
		}
	}

	protected BeanFactory getBeanFactory(FacesContext facesContext) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		if (requestContext == null) {
			return EMPTY_BEAN_FACTORY;
		}
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		return beanFactory != null ? beanFactory : EMPTY_BEAN_FACTORY;
	}

}
