/*
 * Copyright 2002-2007 the original author or authors.
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

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.Flow;

/**
 * Builder interface used to build a flow definition. The process of building a
 * flow consists of the following steps:
 * <ol>
 * <li> Initialize this builder, creating the initial flow definition, by
 * calling {@link #init(String, AttributeMap)}.
 * <li> Call {@link #buildVariables()} to create any variables of the flow and
 * add them to the flow definition.
 * <li> Call {@link #buildInputMapper()} to create and set the input mapper for
 * the flow.
 * <li> Call {@link #buildStartActions()} to create and add any start actions to
 * the flow.
 * <li> Call {@link #buildInlineFlows()} to create any inline flows
 * encapsulated by the flow and add them to the flow definition.
 * <li> Call {@link #buildStates()} to create the states of the flow and add
 * them to the flow definition.
 * <li> Call {@link #buildGlobalTransitions()} to create the any transitions
 * shared by all states of the flow and add them to the flow definition.
 * <li> Call {@link #buildEndActions()} to create and add any end actions to
 * the flow.
 * <li> Call {@link #buildOutputMapper()} to create and set the output mapper
 * for the flow.
 * <li> Call {@link #buildExceptionHandlers()} to create the exception
 * handlers of the flow and add them to the flow definition.
 * <li> Call {@link #getFlow()} to return the fully-built {@link Flow}
 * definition.
 * <li> Dispose this builder, releasing any resources allocated during the
 * building process by calling {@link #dispose()}.
 * </ol>
 * <p>
 * Implementations should encapsulate flow construction logic, either for a
 * specific kind of flow, for example, an <code>OrderFlowBuilder</code> built
 * in Java code, or a generic flow builder strategy, like the
 * <code>XmlFlowBuilder</code>, for building flows from an XML-definition.
 * <p>
 * Flow builders are used by the
 * {@link org.springframework.webflow.engine.builder.FlowAssembler}, which acts as an
 * assembler (director). Flow Builders may be reused, however, exercise caution
 * when doing this as these objects are not thread safe. Also, for each use be
 * sure to call init, followed by the build* methods, getFlow, and dispose
 * completely in that order.
 * <p>
 * This is an example of the classic GoF builder pattern.
 * 
 * @see Flow
 * @see org.springframework.webflow.engine.builder.FlowAssembler
 * @see org.springframework.webflow.engine.builder.AbstractFlowBuilder
 * @see org.springframework.webflow.engine.builder.xml.XmlFlowBuilder
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowBuilder {

	/**
	 * Initialize this builder. This could cause the builder to open a stream to
	 * an externalized resource representing the flow definition, for example.
	 * @param flowId the identifier to assign to the flow
	 * @param attributes custom attributes to assign to the flow
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void init(String flowId, AttributeMap attributes) throws FlowBuilderException;

	/**
	 * Builds any variables initialized by the flow when it starts.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildVariables() throws FlowBuilderException;

	/**
	 * Builds the input mapper responsible for mapping flow input on start.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildInputMapper() throws FlowBuilderException;

	/**
	 * Builds any start actions to execute when the flow starts.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildStartActions() throws FlowBuilderException;

	/**
	 * Builds any "in-line" flows encapsulated by the flow.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildInlineFlows() throws FlowBuilderException;

	/**
	 * Builds the states of the flow.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildStates() throws FlowBuilderException;

	/**
	 * Builds any transitions shared by all states of the flow.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildGlobalTransitions() throws FlowBuilderException;

	/**
	 * Builds any end actions to execute when the flow ends.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildEndActions() throws FlowBuilderException;

	/**
	 * Builds the output mapper responsible for mapping flow output on end.
	 * @throws FlowBuilderException an exception occured building the flow
	 */
	public void buildOutputMapper() throws FlowBuilderException;

	/**
	 * Creates and adds all exception handlers to the flow built by this
	 * builder.
	 * @throws FlowBuilderException an exception occured building this flow
	 */
	public void buildExceptionHandlers() throws FlowBuilderException;

	/**
	 * Get the fully constructed and configured Flow object - called by the
	 * builder's assembler (director) after assembly. When this method is called
	 * by the assembler, it is expected flow construction has completed
	 * and the returned flow is ready for use.
	 */
	public Flow getFlow();

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow
	 * construction process should start with another call to the
	 * {@link #init(String, AttributeMap)} method.
	 */
	public void dispose();
}