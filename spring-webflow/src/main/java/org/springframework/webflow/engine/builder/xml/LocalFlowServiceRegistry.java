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
package org.springframework.webflow.engine.builder.xml;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.webflow.engine.Flow;

/**
 * Simple object that holds a reference to a local bean factory housing services needed by a flow definition at
 * execution time.
 * <p>
 * Internal helper class of the {@link org.springframework.webflow.engine.builder.xml.XmlFlowBuilder}. Package private
 * to highlight it's non-public nature.
 * 
 * @see org.springframework.webflow.engine.builder.xml.XmlFlowBuilder
 * @see org.springframework.webflow.engine.builder.xml.LocalFlowServiceLocator
 * 
 * @author Keith Donald
 */
class LocalFlowServiceRegistry {

	/**
	 * The flow this registry is for (and scoped by).
	 */
	private Flow flow;

	/**
	 * The local registry holding the artifacts local to the flow.
	 */
	private BeanFactory beanFactory;

	/**
	 * Create a new local service registry.
	 * @param flow the flow this registry is for (and scoped by)
	 * @param beanFactory the actual backing registry - a Spring bean factory
	 */
	public LocalFlowServiceRegistry(Flow flow, BeanFactory beanFactory) {
		this.flow = flow;
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns the flow this registry is for (and scoped by).
	 */
	public Flow getFlow() {
		return flow;
	}

	/**
	 * Returns the bean factory acting as the physical registry.
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}