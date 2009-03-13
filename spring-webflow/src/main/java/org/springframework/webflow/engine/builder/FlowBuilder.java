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
package org.springframework.webflow.engine.builder;

import org.springframework.webflow.engine.Flow;

/**
 * Builder interface used to build a flow definition. The process of building a flow consists of the following steps:
 * <ol>
 * <li>Initialize this builder, creating the initial flow definition, by calling {@link #init(FlowBuilderContext)}.
 * <li>Call {@link #buildVariables()} to create any variables of the flow and add them to the flow definition.
 * <li>Call {@link #buildInputMapper()} to create and set the input mapper for the flow.
 * <li>Call {@link #buildStartActions()} to create and add any start actions to the flow.
 * <li>Call {@link #buildStates()} to create the states of the flow and add them to the flow definition.
 * <li>Call {@link #buildGlobalTransitions()} to create any transitions shared by all states of the flow and add them to
 * the flow definition.
 * <li>Call {@link #buildEndActions()} to create and add any end actions to the flow.
 * <li>Call {@link #buildOutputMapper()} to create and set the output mapper for the flow.
 * <li>Call {@link #buildExceptionHandlers()} to create the exception handlers of the flow and add them to the flow
 * definition.
 * <li>Call {@link #getFlow()} to return the fully-built {@link Flow} definition.
 * <li>Dispose this builder, releasing any resources allocated during the building process by calling {@link #dispose()}.
 * </ol>
 * <p>
 * Implementations should encapsulate flow construction logic, either for a specific kind of flow, for example, an
 * <code>OrderFlowBuilder</code> built in Java code, or a generic flow builder strategy, like the
 * <code>XmlFlowBuilder</code>, for building flows from an XML-definition.
 * <p>
 * Flow builders are used by the {@link FlowAssembler}, which acts as an assembler (director). Flow Builders may be
 * reused, however, exercise caution when doing this as these objects are not thread safe. Also, for each use be sure to
 * call init, followed by the build* methods, getFlow, and dispose completely in that order.
 * <p>
 * This is a good example of the classic GoF builder pattern.
 * 
 * @see Flow
 * @see FlowBuilderContext
 * @see FlowAssembler
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowBuilder {

	/**
	 * Initialize this builder. This could cause the builder to open a stream to an externalized resource representing
	 * the flow definition, for example.
	 * @param context the flow builder context
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void init(FlowBuilderContext context) throws FlowBuilderException;

	/**
	 * Builds any variables initialized by the flow when it starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildVariables() throws FlowBuilderException;

	/**
	 * Builds the input mapper responsible for mapping flow input on start.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildInputMapper() throws FlowBuilderException;

	/**
	 * Builds any start actions to execute when the flow starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStartActions() throws FlowBuilderException;

	/**
	 * Builds the states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStates() throws FlowBuilderException;

	/**
	 * Builds any transitions shared by all states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildGlobalTransitions() throws FlowBuilderException;

	/**
	 * Builds any end actions to execute when the flow ends.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildEndActions() throws FlowBuilderException;

	/**
	 * Builds the output mapper responsible for mapping flow output on end.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildOutputMapper() throws FlowBuilderException;

	/**
	 * Creates and adds all exception handlers to the flow built by this builder.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public void buildExceptionHandlers() throws FlowBuilderException;

	/**
	 * Get the fully constructed and configured Flow object. Called by the builder's assembler (director) after
	 * assembly. When this method is called by the assembler, it is expected flow construction has completed and the
	 * returned flow is fully configured and ready for use.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public Flow getFlow() throws FlowBuilderException;

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow construction process should start with another
	 * call to the {@link #init(FlowBuilderContext)} method.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public void dispose() throws FlowBuilderException;

	/**
	 * As the underlying flow managed by this builder changed since the last build occurred?
	 * @return true if changed, false if not
	 */
	public boolean hasFlowChanged();

	/**
	 * Returns a string describing the location of the flow resource; the logical location where the source code can be
	 * found. Used for informational purposes.
	 * @return the flow resource string
	 */
	public String getFlowResourceString();

}