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
package org.springframework.webflow.execution;

import org.springframework.webflow.definition.FlowDefinition;

/**
 * An abstract factory for creating flow executions. A flow execution represents a runtime, top-level instance of a flow
 * definition.
 * <p>
 * This factory provides encapsulation of the flow execution implementation type, as well as its construction and
 * assembly process.
 * <p>
 * Flow execution factories are responsible for registering {@link FlowExecutionListener listeners} with the constructed
 * flow execution.
 * 
 * @see FlowExecution
 * @see FlowDefinition
 * @see FlowExecutionListener
 * 
 * @author Keith Donald
 */
public interface FlowExecutionFactory {

	/**
	 * Create a new flow execution product for the given flow definition.
	 * @param flowDefinition the flow definition
	 * @return the new flow execution, fully initialized and awaiting to be started
	 */
	public FlowExecution createFlowExecution(FlowDefinition flowDefinition);
}