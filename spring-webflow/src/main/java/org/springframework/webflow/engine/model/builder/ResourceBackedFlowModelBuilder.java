/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.webflow.engine.model.builder;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.registry.FlowModelLocator;

/**
 * A {@link FlowModelBuilder} interface for components that require a backing {@link Resource} and a
 * {@link FlowModelLocator} for their operation.
 * 
 * <p>
 * Clients should invoke {@link #init()} from {@link FlowModelBuilder} <em>after</em> calling
 * {@link #setFlowModelLocator(FlowModelLocator)} and {@link #setFlowResource(Resource)}, otherwise an
 * {@link IllegalStateException} will be thrown.
 * 
 * @author Paul Wilson
 */
public interface ResourceBackedFlowModelBuilder extends FlowModelBuilder {

	/**
	 * Set the {@link FlowModelLocator flow model locator} instance for this builder
	 * @param modelLocator the flow model locator
	 */
	public void setFlowModelLocator(FlowModelLocator modelLocator);

	/**
	 * Set the flow {@link Resource} to use during construction
	 * @param resource the flow resource
	 */
	public void setFlowResource(Resource resource);
}