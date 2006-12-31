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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;

/**
 * A factory for instances of the
 * {@link FlowExecutionImpl default flow execution} implementation.
 * 
 * @author Keith Donald
 */
public class FlowExecutionImplFactory implements FlowExecutionFactory {
	
	private static final Log logger = LogFactory.getLog(FlowExecutionImplFactory.class);

	/**
	 * The strategy for loading listeners that should observe executions of a
	 * flow definition. The default simply loads an empty static listener list.
	 */
	private FlowExecutionListenerLoader executionListenerLoader = StaticFlowExecutionListenerLoader.EMPTY_INSTANCE;

	/**
	 * System execution attributes that may influence flow execution behavior.
	 * The default is an empty map.
	 */
	private AttributeMap executionAttributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;
	
	/**
	 * Returns the attributes to apply to flow executions created by this factory.
	 * Execution attributes may affect flow execution behavior.
	 * @return flow execution attributes
	 */
	public AttributeMap getExecutionAttributes() {
		return executionAttributes;
	}

	/**
	 * Sets the attributes to apply to flow executions created by this factory.
	 * Execution attributes may affect flow execution behavior.
	 * @param executionAttributes flow execution system attributes
	 */
	public void setExecutionAttributes(AttributeMap executionAttributes) {
		Assert.notNull(executionAttributes, "The execution attributes map is required");
		this.executionAttributes = executionAttributes;
	}
	
	/**
	 * Sets the attributes to apply to flow executions created by this factory.
	 * Execution attributes may affect flow execution behavior.
	 * <p>
	 * Convenience setter that takes a simple <code>java.util.Map</code> to ease
	 * bean style configuration.
	 * @param executionAttributes flow execution system attributes
	 */
	public void setExecutionAttributesMap(Map executionAttributes) {
		Assert.notNull(executionAttributes, "The execution attributes map is required");
		this.executionAttributes = new LocalAttributeMap(executionAttributes);
	}
	
	/**
	 * Returns the strategy for loading listeners that should observe executions of
	 * a flow definition. Allows full control over what listeners should apply
	 * for executions of a flow definition.
	 */
	public FlowExecutionListenerLoader getExecutionListenerLoader() {
		return executionListenerLoader;
	}

	/**
	 * Sets the strategy for loading listeners that should observe executions of
	 * a flow definition. Allows full control over what listeners should apply
	 * for executions of a flow definition.
	 */
	public void setExecutionListenerLoader(FlowExecutionListenerLoader listenerLoader) {
		Assert.notNull(listenerLoader, "The listener loader is required");
		this.executionListenerLoader = listenerLoader;
	}

	public FlowExecution createFlowExecution(FlowDefinition flowDefinition) {
		Assert.isInstanceOf(Flow.class, flowDefinition, "Flow definition is of wrong type: ");
		if (logger.isDebugEnabled()) {
			logger.debug("Creating flow execution for flow definition with id '" + flowDefinition.getId() + "'");
		}
		FlowExecutionListener[] listeners = executionListenerLoader.getListeners(flowDefinition);
		return new FlowExecutionImpl((Flow)flowDefinition, listeners, executionAttributes);
	}
}