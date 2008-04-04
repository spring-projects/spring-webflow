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
package org.springframework.webflow.engine.support;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.engine.VariableValueFactory;
import org.springframework.webflow.execution.RequestContext;

/**
 * A bean-factory backed variable value factory. Relies on an autowire-capable bean factory to wire variable value
 * dependencies on value creation and restoration.
 * 
 * @author Keith Donald
 */
public class BeanFactoryVariableValueFactory implements VariableValueFactory {

	/**
	 * The class of variable value. Typically a Java bean.
	 */
	private Class type;

	/**
	 * The backing bean factory that will create and restore variable instances.
	 */
	private AutowireCapableBeanFactory beanFactory;

	/**
	 * Creates a new bean factory variable factory.
	 * @param type the variable class
	 * @param beanFactory the bean factory that will create and restore variable instances.
	 */
	public BeanFactoryVariableValueFactory(Class type, AutowireCapableBeanFactory beanFactory) {
		this.type = type;
		this.beanFactory = beanFactory;
	}

	public Object createInitialValue(RequestContext context) {
		return beanFactory.createBean(type);
	}

	public void restoreReferences(Object value, RequestContext context) {
		if (value != null) {
			beanFactory.autowireBean(value);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("type", type).toString();
	}

}