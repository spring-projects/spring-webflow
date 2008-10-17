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
package org.springframework.webflow.engine.model.registry;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;

/**
 * A holder holding a reference to a Flow model. Provides a layer of indirection, enabling things like "hot-reloadable"
 * flow models.
 * 
 * @see FlowModelRegistry#registerFlowModel(String, FlowModelHolder)
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface FlowModelHolder {

	/**
	 * Returns the flow model held by this holder. Calling this method the first time may trigger flow model assembly.
	 */
	public FlowModel getFlowModel();

	/**
	 * Has the underlying flow model changed since it was last accessed via a call to {@link #getFlowModel()}.
	 * @return true if yes, false if not
	 */
	public boolean hasFlowModelChanged();

	/**
	 * Returns the underlying resource defining the flow model.
	 * @return the flow model resource
	 */
	public Resource getFlowModelResource();

	/**
	 * Refresh the flow model held by this holder. Calling this method typically triggers flow re-assembly, which may
	 * include a refresh from an externalized resource such as a file.
	 */
	public void refresh();

}