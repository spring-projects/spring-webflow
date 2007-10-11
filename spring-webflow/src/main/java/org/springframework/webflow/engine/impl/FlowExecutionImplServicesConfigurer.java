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
package org.springframework.webflow.engine.impl;

import org.springframework.binding.message.DefaultMessageContextFactory;
import org.springframework.binding.message.MessageContextFactory;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;

abstract class FlowExecutionImplServicesConfigurer {

	/**
	 * System execution attributes that may influence flow execution behavior. The default is an empty map.
	 */
	private AttributeMap executionAttributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;

	/**
	 * The strategy for loading listeners that should observe executions of a flow definition. The default simply loads
	 * an empty static listener list.
	 */
	private FlowExecutionListenerLoader executionListenerLoader = StaticFlowExecutionListenerLoader.EMPTY_INSTANCE;

	/**
	 * The factory for message contexts for tracking flow execution messages.
	 */
	private MessageContextFactory messageContextFactory = new DefaultMessageContextFactory(new StaticMessageSource());

	/**
	 * Sets the attributes to apply to flow executions created by this factory. Execution attributes may affect flow
	 * execution behavior.
	 * @param executionAttributes flow execution system attributes
	 */
	public void setExecutionAttributes(AttributeMap executionAttributes) {
		Assert.notNull(executionAttributes, "The execution attributes map is required");
		this.executionAttributes = executionAttributes;
	}

	/**
	 * Sets the strategy for loading listeners that should observe executions of a flow definition. Allows full control
	 * over what listeners should apply for executions of a flow definition.
	 */
	public void setExecutionListenerLoader(FlowExecutionListenerLoader executionListenerLoader) {
		Assert.notNull(executionListenerLoader, "The execution listener loader is required");
		this.executionListenerLoader = executionListenerLoader;
	}

	/**
	 * Sets the strategy for creating message contexts that track flow execution messages.
	 */
	public void setMessageContextFactory(MessageContextFactory messageContextFactory) {
		this.messageContextFactory = messageContextFactory;
	}

	/**
	 * Called by subclasses to apply the configured set of standard services to the flow execution.
	 * @param execution the flow execution
	 */
	protected void configureServices(FlowExecutionImpl execution) {
		execution.setAttributes(executionAttributes);
		execution.setListeners(executionListenerLoader.getListeners(execution.getDefinition()));
		execution.setMessageContextFactory(messageContextFactory);
	}
}