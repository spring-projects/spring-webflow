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
package org.springframework.webflow.definition.registry;

import org.springframework.webflow.definition.FlowDefinition;

/**
 * A holder holding a reference to a Flow definition. Provides a layer of indirection, enabling things like
 * "hot-reloadable" flow definitions.
 * 
 * @see FlowDefinitionRegistry#registerFlowDefinition(FlowDefinitionHolder)
 * 
 * @author Keith Donald
 */
public interface FlowDefinitionHolder {

	/**
	 * Returns the <code>id</code> of the flow definition held by this holder. This is a <i>lightweight</i> method
	 * callers may call to obtain the id of the flow without triggering full flow definition assembly (which may be an
	 * expensive operation).
	 */
	public String getFlowDefinitionId();

	/**
	 * Returns a descriptive string that identifies the source of this FlowDefinition. This is also a lightweight method
	 * callers may call to obtain the logical resource where the flow definition resides without triggering flow
	 * definition assembly. Used for informational purposes.
	 * @return the flow definition resource string
	 */
	public String getFlowDefinitionResourceString();

	/**
	 * Returns the flow definition held by this holder. Calling this method the first time may trigger flow assembly
	 * (which may be expensive).
	 * @throws FlowDefinitionConstructionException if there is a problem constructing the target flow definition
	 */
	public FlowDefinition getFlowDefinition() throws FlowDefinitionConstructionException;

	/**
	 * Refresh the flow definition held by this holder. Calling this method typically triggers flow re-assembly, which
	 * may include a refresh from an externalized resource such as a file.
	 * @throws FlowDefinitionConstructionException if there is a problem constructing the target flow definition
	 */
	public void refresh() throws FlowDefinitionConstructionException;

	/**
	 * Indicates that the system is being shutdown and any resources flow resources should be released. After this
	 * method is called, calls to {@link #getFlowDefinition()} are undefined. Should only be called once. May be a no-op
	 * if the held flow was never constructed to begin with.
	 */
	public void destroy();

}