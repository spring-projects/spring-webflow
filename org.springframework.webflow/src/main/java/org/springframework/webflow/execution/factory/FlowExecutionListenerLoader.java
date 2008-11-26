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
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionListener;

/**
 * A strategy interface for loading the set of FlowExecutionListener's that should apply to executions of a given flow
 * definition. Typically used by a {@link FlowExecutionFactory} as part of execution creation.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionListenerLoader {

	/**
	 * Get the flow execution listeners that apply to the given flow definition.
	 * @param flowDefinition the flow definition
	 * @return the listeners that apply
	 */
	public FlowExecutionListener[] getListeners(FlowDefinition flowDefinition);
}