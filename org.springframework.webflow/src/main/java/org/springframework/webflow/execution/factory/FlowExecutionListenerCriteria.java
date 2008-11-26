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
package org.springframework.webflow.execution.factory;

import org.springframework.webflow.definition.FlowDefinition;

/**
 * Strategy interface that determines if a flow execution listener should attach to executions of a specific flow
 * definition.
 * <p>
 * This selection strategy is typically used by the {@link FlowExecutionListenerLoader} to determine which listeners
 * should apply to which flow definitions.
 * 
 * @see org.springframework.webflow.execution.FlowExecutionListener
 * @see org.springframework.webflow.execution.factory.FlowExecutionListenerLoader
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowExecutionListenerCriteria {

	/**
	 * Do the listeners guarded by this criteria object apply to the provided flow definition?
	 * @param definition the flow definition
	 * @return true if yes, false if no
	 */
	public boolean appliesTo(FlowDefinition definition);
}