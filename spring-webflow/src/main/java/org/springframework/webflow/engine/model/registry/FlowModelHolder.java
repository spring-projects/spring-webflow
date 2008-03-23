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
package org.springframework.webflow.engine.model.registry;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;

/**
 * A holder holding a reference to a Flow model. Provides a layer of indirection, enabling things like "hot-reloadable"
 * flow models.
 * 
 * @see FlowModelRegistry#registerFlowModel(FlowModelHolder)
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface FlowModelHolder {

	/**
	 * Returns the <code>id</code> of the flow model held by this holder. This is a <i>lightweight</i> method callers
	 * may call to obtain the id of the flow without triggering full flow definition assembly (which may be an expensive
	 * operation).
	 */
	public String getFlowModelId();

	/**
	 * Returns the flow model held by this holder. Calling this method the first time may trigger flow assembly (which
	 * may be expensive).
	 * @throws FlowModelConstructionException if there is a problem constructing the target flow model
	 */
	public FlowModel getFlowModel() throws FlowModelConstructionException;

	/**
	 * Has the underlying flow model changed since it was last accessed via a call to {@link #getFlowModel()}.
	 * @return true if yes, false if not
	 */
	public boolean hasFlowModelChanged();

	/**
	 * Returns the underlying resource defining the flow model. Will return null if the flow model did not originate
	 * from a file-based resource.
	 * @return the flow model resource, or null
	 */
	public Resource getFlowModelResource();

	/**
	 * Refresh the flow model held by this holder. Calling this method typically triggers flow re-assembly, which may
	 * include a refresh from an externalized resource such as a file.
	 * @throws FlowModelConstructionException if there is a problem constructing the target flow model
	 */
	public void refresh() throws FlowModelConstructionException;

}