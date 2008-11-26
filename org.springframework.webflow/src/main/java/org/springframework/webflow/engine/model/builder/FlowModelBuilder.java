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
package org.springframework.webflow.engine.model.builder;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;

/**
 * Builder interface used to build a flow model. The process of building a flow model consists of the following steps:
 * <ol>
 * <li> Initialize this builder by calling {@link #init()}.
 * <li> Call {@link #build()} to create the flow model.
 * <li> Call {@link #getFlowModel()} to return the fully-built {@link FlowModel} model.
 * <li> Dispose this builder, releasing any resources allocated during the building process by calling
 * {@link #dispose()}.
 * </ol>
 * <p>
 * Implementations should encapsulate flow construction logic, either for a specific kind of flow, for example, an
 * <code>XmlFlowModelBuilder</code>, for building flows from an XML-definition.
 * <p>
 * This is a good example of the classic GoF builder pattern.
 * 
 * @see FlowModel
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public interface FlowModelBuilder {

	/**
	 * Initialize this builder. This could cause the builder to open a stream to an externalized resource representing
	 * the flow definition, for example.
	 * @throws FlowModelBuilderException an exception occurred building the flow
	 */
	public void init() throws FlowModelBuilderException;

	/**
	 * Builds any variables initialized by the flow when it starts.
	 * @throws FlowModelBuilderException an exception occurred building the flow
	 */
	public void build() throws FlowModelBuilderException;

	/**
	 * Get the fully constructed flow model. Called by the builder's assembler (director) after assembly. When this
	 * method is called by the assembler, it is expected flow construction has completed and the returned flow model is
	 * ready for use.
	 * @throws FlowModelBuilderException an exception occurred building this flow
	 */
	public FlowModel getFlowModel() throws FlowModelBuilderException;

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow construction process should start with another
	 * call to the {@link #init()} method.
	 * @throws FlowModelBuilderException an exception occurred disposing this flow
	 */
	public void dispose() throws FlowModelBuilderException;

	/**
	 * Get the underlying flow model resource accessed to build this flow model. Returns null if this builder does not
	 * construct the flow model from a resource.
	 * @return the flow model resource
	 */
	public Resource getFlowModelResource();

	/**
	 * Returns true if the underlying flow model resource has changed since the last call to {@link #init()}.
	 * Always returns false if the flow model is not build from a resource.
	 * @return true if the resource backing the flow model has changed
	 */
	public boolean hasFlowModelResourceChanged();

}