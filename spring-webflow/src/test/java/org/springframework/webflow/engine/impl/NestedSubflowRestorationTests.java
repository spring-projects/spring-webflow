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

import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.continuation.SerializedFlowExecutionContinuation;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Tests dealing with restoration of nested subflows.
 * 
 * @author Erwin Vervaet
 */
public class NestedSubflowRestorationTests extends AbstractXmlFlowExecutionTests implements FlowDefinitionLocator {

	protected FlowDefinitionResource getFlowDefinitionResource() {
		return new FlowDefinitionResource(new ClassPathResource("nestedSubflow.xml", NestedSubflowRestorationTests.class));
	}
	
	public FlowDefinition getFlowDefinition(String id)
			throws NoSuchFlowDefinitionException, FlowDefinitionConstructionException {
		return getFlowDefinition();
	}
	
	public void testNestedFlows() {
		startFlow();
		assertFlowExecutionActive();
		assertActiveFlowEquals("nestedSubflow");
		assertCurrentStateEquals("view1");
		signalEvent("start");
		assertFlowExecutionActive();
		assertActiveFlowEquals("subflowDef3");
		assertCurrentStateEquals("view4");
		
		FlowExecution flowExecution = getFlowExecution();
		flowExecution = new SerializedFlowExecutionContinuation(flowExecution, false).unmarshal();
		flowExecution = new FlowExecutionImplStateRestorer(this).restoreState(flowExecution, null);
		updateFlowExecution(flowExecution);
		
		signalEvent("continue");
		assertFlowExecutionEnded();
	}

}
