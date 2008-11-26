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
package org.springframework.webflow.execution.repository.support;

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;

/**
 * A strategy used by repositories to restore transient flow execution state during execution restoration.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionStateRestorer {

	/**
	 * Restore the transient state of the flow execution.
	 * @param execution the flow execution, newly deserialized and needing restoration
	 * @param definition the root flow definition for the execution, typically not part of the serialized form
	 * @param key the flow execution key, typically not part of the serialized form
	 * @param conversationScope the execution's conversation scope, which is typically not part of the serialized form
	 * since it could be shared by multiple physical flow execution <i>copies</i> all sharing the same logical
	 * conversation
	 * @param subflowDefinitionLocator for locating the definitions of any subflows started by the execution
	 * @return the restored flow execution
	 */
	public FlowExecution restoreState(FlowExecution execution, FlowDefinition definition, FlowExecutionKey key,
			MutableAttributeMap conversationScope, FlowDefinitionLocator subflowDefinitionLocator);
}