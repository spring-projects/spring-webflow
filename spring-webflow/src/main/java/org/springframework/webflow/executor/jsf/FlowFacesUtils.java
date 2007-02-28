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
package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * Trivial helper utility class for SWF within a JSF environment. Used mainly to
 * locate Web Flow services needed to run the JSF integration.
 * 
 * @author Keith Donald
 */
public class FlowFacesUtils {

	/**
	 * Bean name of a custom flow executor implementation.
	 * 
	 * Note the flow executor object is used only at configuration time to
	 * extract other lower-level services needed by the JSF integration (flow
	 * execution repository, flow execution factory). The runtime FlowExecutor
	 * interface is never used by this JSF integration.
	 */
	private static final String FLOW_EXECUTOR_BEAN_NAME = "flowExecutor";

	/**
	 * Bean name of a custom flow execution repository implementation.
	 */
	private static final String FLOW_EXECUTION_REPOSITORY_BEAN_NAME = "flowExecutionRepository";

	/**
	 * Bean name of a custom flow definition locator implementation.
	 */
	private static final String FLOW_DEFINITION_LOCATOR_BEAN_NAME = "flowDefinitionLocator";

	/**
	 * Bean name of a custom flow execution factory implementation.
	 */
	private static final String FLOW_EXECUTION_FACTORY_BEAN_NAME = "flowExecutionFactory";

	/**
	 * The default flow execution repository implementation to use.
	 */
	private static FlowExecutionRepository defaultRepository;

	/**
	 * The default flow execution factory implementation to use.
	 */
	private static FlowExecutionFactory defaultFactory;

	/**
	 * Returns the locator for flow definitions to use in a JSF environment.
	 * Searches for a bean in the root web application context named
	 * {@link #FLOW_DEFINITION_LOCATOR_BEAN_NAME}. A bean of type
	 * {@link FlowDefinitionLocator} must exist by this name.
	 * @param context the faces context
	 * @return the flow definition locator
	 */
	public static FlowDefinitionLocator getDefinitionLocator(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		try {
			return (FlowDefinitionLocator) ac.getBean(FLOW_DEFINITION_LOCATOR_BEAN_NAME, FlowDefinitionLocator.class);
		}
		catch (NoSuchBeanDefinitionException e) {
			String message = "No bean definition with id '" + FLOW_DEFINITION_LOCATOR_BEAN_NAME
					+ "' could be found; to use Spring Web Flow with JSF you must "
					+ "configure your context with a FlowDefinitionLocator bean with this id "
					+ "exposing a registry of flow definitions.";
			throw new JsfFlowConfigurationException(message, e);
		}
	}

	/**
	 * Returns the flow execution repository to use in a JSF environment.
	 * Searches for a bean in the root web application context named
	 * {@link #FLOW_EXECUTION_REPOSITORY_BEAN_NAME}. If no such bean exists
	 * with this name, falls back on the repository configured by a bean with
	 * name {@link #FLOW_EXECUTOR_BEAN_NAME}. If no bean exists with that name,
	 * uses the default 'simple' repository implementation.
	 * @param context the faces context
	 * @return the flow execution repository
	 */
	public synchronized static FlowExecutionRepository getExecutionRepository(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		if (ac.containsBean(FLOW_EXECUTION_REPOSITORY_BEAN_NAME)) {
			return (FlowExecutionRepository) ac.getBean(FLOW_EXECUTION_REPOSITORY_BEAN_NAME,
					FlowExecutionRepository.class);
		}
		else {
			if (defaultRepository == null) {
				FlowExecutor flowExecutor = getFlowExecutor(context);
				if (flowExecutor != null && flowExecutor instanceof FlowExecutorImpl) {
					defaultRepository = ((FlowExecutorImpl) flowExecutor).getExecutionRepository();
				}
				else {
					defaultRepository = new SimpleFlowExecutionRepository(new FlowExecutionImplStateRestorer(
							getDefinitionLocator(context)), new SessionBindingConversationManager());
				}
			}
			return defaultRepository;
		}
	}

	/**
	 * Returns the flow execution factory to use in a JSF environment. Searches
	 * for a bean in the root web application context named
	 * {@link #FLOW_EXECUTION_FACTORY_BEAN_NAME}. If no such bean exists with
	 * this name, falls back on the repository configured by a bean with name
	 * {@link #FLOW_EXECUTOR_BEAN_NAME}. If no bean exists with that name, uses
	 * the default factory implementation.
	 * @param context the faces context
	 * @return the flow execution factory
	 */
	public synchronized static FlowExecutionFactory getExecutionFactory(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		if (ac.containsBean(FLOW_EXECUTION_FACTORY_BEAN_NAME)) {
			return (FlowExecutionFactory) ac.getBean(FLOW_EXECUTION_FACTORY_BEAN_NAME, FlowExecutionFactory.class);
		}
		else {
			if (defaultFactory == null) {
				FlowExecutor flowExecutor = getFlowExecutor(context);
				if (flowExecutor != null && flowExecutor instanceof FlowExecutorImpl) {
					defaultFactory = ((FlowExecutorImpl) flowExecutor).getExecutionFactory();
				}
				else {
					defaultFactory = new FlowExecutionImplFactory();
				}
			}
			return defaultFactory;
		}
	}

	/**
	 * Returns the flow executor providing access to services used by the Spring
	 * Web Flow JSF integration. Searches for a bean in the root web application
	 * context named {@link #FLOW_EXECUTOR_BEAN_NAME}. If no such bean exists
	 * returns null.
	 * @param context the faces context
	 * @return the flow executor, or null if no such bean exists
	 */
	private synchronized static FlowExecutor getFlowExecutor(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		if (ac.containsBean(FLOW_EXECUTOR_BEAN_NAME)) {
			return (FlowExecutor) ac.getBean(FLOW_EXECUTOR_BEAN_NAME, FlowExecutor.class);
		}
		else {
			return null;
		}
	}
}