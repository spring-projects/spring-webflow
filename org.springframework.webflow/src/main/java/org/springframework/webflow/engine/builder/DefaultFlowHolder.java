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
package org.springframework.webflow.engine.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;

/**
 * A flow definition holder that can detect changes on an underlying flow definition resource and refresh that resource
 * automatically.
 * <p>
 * This class is thread-safe.
 * <p>
 * Note that this {@link FlowDefinition} holder uses a {@link FlowAssembler}. This class bridges the <i>abstract</i>
 * world of {@link FlowDefinition flow definitions} with the <i>concrete</i> world of flow implementations.
 * 
 * @see FlowAssembler
 * @see FlowDefinition
 * 
 * @author Keith Donald
 */
public class DefaultFlowHolder implements FlowDefinitionHolder {

	private static final Log logger = LogFactory.getLog(DefaultFlowHolder.class);

	/**
	 * The flow definition assembled by this assembler, initially null.
	 */
	private FlowDefinition flowDefinition;

	/**
	 * The flow assembler.
	 */
	private FlowAssembler assembler;

	/**
	 * A flag indicating whether or not this holder is in the middle of the assembly process.
	 */
	private boolean assembling;

	/**
	 * Creates a new refreshable flow definition holder that uses the configured assembler (GOF director) to drive flow
	 * assembly, on initial use and on any resource change or refresh.
	 * @param assembler the flow assembler to use
	 */
	public DefaultFlowHolder(FlowAssembler assembler) {
		Assert.notNull(assembler, "The FlowAssembler is required");
		this.assembler = assembler;
	}

	public String getFlowDefinitionId() {
		return assembler.getFlowBuilderContext().getFlowId();
	}

	public String getFlowDefinitionResourceString() {
		return assembler.getFlowBuilder().getFlowResourceString();
	}

	public synchronized FlowDefinition getFlowDefinition() throws FlowDefinitionConstructionException {
		if (assembling) {
			// must return early assembly result for when a flow calls itself recursively
			return getFlowBuilder().getFlow();
		}
		if (flowDefinition == null) {
			logger.debug("Assembling the flow for the first time");
			assembleFlow();
		} else {
			if (flowDefinition.inDevelopment() && getFlowBuilder().hasFlowChanged()) {
				logger.debug("The flow under development has changed; reassembling...");
				assembleFlow();
			}
		}
		return flowDefinition;
	}

	public synchronized void refresh() throws FlowDefinitionConstructionException {
		assembleFlow();
	}

	public void destroy() {
		if (flowDefinition != null) {
			flowDefinition.destroy();
		}
	}

	// internal helpers

	private void assembleFlow() throws FlowDefinitionConstructionException {
		try {
			assembling = true;
			flowDefinition = assembler.assembleFlow();
		} catch (FlowBuilderException e) {
			throw new FlowDefinitionConstructionException(assembler.getFlowBuilderContext().getFlowId(), e);
		} finally {
			assembling = false;
		}
	}

	private FlowBuilder getFlowBuilder() {
		return assembler.getFlowBuilder();
	}

	public String toString() {
		return new ToStringCreator(this).append("flowBuilder", assembler.getFlowBuilder()).toString();
	}

}