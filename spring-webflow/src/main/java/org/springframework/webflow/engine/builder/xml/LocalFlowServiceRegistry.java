/*
 * Copyright 2002-2007 the original author or authors.
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
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowServiceLocator;

/**
 * Simple value object that holds a reference to a local artifact registry
 * of a flow definition that is in the process of being constructed.
 * <p>
 * Internal helper class of the {@link org.springframework.webflow.engine.builder.xml.XmlFlowBuilder}.
 * Package private to highlight it's non-public nature.
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
	 * The locations of the registry resource definitions. 
	 */
	private Resource[] resources;

	/**
	 * The local registry holding the artifacts local to the flow.
	 */
	private GenericApplicationContext context;

	/**
	 * Create a new registry, loading artifact definitions from
	 * given resources.
	 * @param flow the flow this registry is for (and scoped by)
	 * @param resources the registry resource definitions
	 */
	public LocalFlowServiceRegistry(Flow flow, Resource[] resources) {
		this.flow = flow;
		this.resources = resources;
	}

	/**
	 * Returns the flow this registry is for (and scoped by).
	 */
	public Flow getFlow() {
		return flow;
	}

	/**
	 * Returns the resources defining registry artifacts.
	 */
	public Resource[] getResources() {
		return resources;
	}

	/**
	 * Retuns the application context holding registry artifacts.
	 */
	public ApplicationContext getContext() {
		return context;
	}

	/**
	 * Initialize this registry of the local flow service locator.
	 * @param localFactory the local flow service locator
	 * @param rootFactory the root service locator
	 */
	public void init(LocalFlowServiceLocator localFactory, FlowServiceLocator rootFactory) {
		BeanFactory parent = null;
		if (localFactory.isEmpty()) {
			try {
				parent = rootFactory.getBeanFactory();
			}
			catch (UnsupportedOperationException e) {
				// can't link to a parent
			}
		}
		else {
			parent = localFactory.top().context;
		}
		context = createLocalFlowContext(parent, rootFactory);
		new XmlBeanDefinitionReader(context).loadBeanDefinitions(resources);
		context.refresh();
	}

	/**
	 * Create the flow local application context.
	 * @param parent the parent application context
	 * @param rootFactory the root service locator, used to obtain a resource
	 * loader
	 * @return the flow local application context
	 */
	private GenericApplicationContext createLocalFlowContext(BeanFactory parent, FlowServiceLocator rootFactory) {
		if (parent instanceof WebApplicationContext) {
			GenericWebApplicationContext context = new GenericWebApplicationContext();
			context.setServletContext(((WebApplicationContext)parent).getServletContext());
			context.setParent((WebApplicationContext)parent);
			context.setResourceLoader(rootFactory.getResourceLoader());
			return context;
		}
		else {
			GenericApplicationContext context = new GenericApplicationContext();
			if (parent instanceof ApplicationContext) {
				context.setParent((ApplicationContext)parent);
			}
			else {
				if (parent != null) {
					context.getBeanFactory().setParentBeanFactory(parent);
				}
			}
			context.setResourceLoader(rootFactory.getResourceLoader());
			return context;
		}
	}
}