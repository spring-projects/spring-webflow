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

/**
 * Trivial helper utility class for SWF within a JSF environment.
 * 
 * @author Keith Donald
 */
public class FlowFacesUtils {

	private static final String REPOSITORY_BEAN_NAME = "flowExecutionRepository";

	private static final String LOCATOR_BEAN_NAME = "flowDefinitionLocator";

	private static final String FACTORY_BEAN_NAME = "flowExecutionFactory";

	private static SimpleFlowExecutionRepository defaultRepository;

	private static FlowExecutionImplFactory defaultFactory;

	public static FlowDefinitionLocator getDefinitionLocator(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		try {
			return (FlowDefinitionLocator)ac.getBean(LOCATOR_BEAN_NAME, FlowDefinitionLocator.class);
		}
		catch (NoSuchBeanDefinitionException e) {
			String message = "No bean definition with id '" + LOCATOR_BEAN_NAME
					+ "' could be found; to use Spring Web Flow with JSF you must "
					+ "configure your context with a FlowDefinitionLocator "
					+ "exposing a registry of flow definitions.";
			throw new JsfFlowConfigurationException(message, e);
		}
	}

	public synchronized static FlowExecutionRepository getExecutionRepository(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		if (ac.containsBean(REPOSITORY_BEAN_NAME)) {
			return (FlowExecutionRepository)ac.getBean(REPOSITORY_BEAN_NAME, FlowExecutionRepository.class);
		}
		else {
			if (defaultRepository == null) {
				defaultRepository = new SimpleFlowExecutionRepository(new FlowExecutionImplStateRestorer(
						getDefinitionLocator(context)), new SessionBindingConversationManager());
			}
			return defaultRepository;
		}
	}

	public synchronized static FlowExecutionFactory getExecutionFactory(FacesContext context) {
		ApplicationContext ac = FacesContextUtils.getRequiredWebApplicationContext(context);
		if (ac.containsBean(FACTORY_BEAN_NAME)) {
			return (FlowExecutionFactory)ac.getBean(FACTORY_BEAN_NAME, FlowExecutionFactory.class);
		}
		else {
			if (defaultFactory == null) {
				defaultFactory = new FlowExecutionImplFactory();
			}
			return defaultFactory;
		}
	}
}