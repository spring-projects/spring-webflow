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
package org.springframework.webflow.engine.builder.support;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.FlowBuilderException;

/**
 * Abstract base implementation of a flow builder defining common functionality needed by most concrete flow builder
 * implementations. This class implements all optional parts of the FlowBuilder process as no-op methods. Subclasses are
 * only required to implement {@link #buildStates()}.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowBuilder implements FlowBuilder {

	/**
	 * The <code>Flow</code> built by this builder.
	 */
	private Flow flow;

	/**
	 * The flow builder context providing access to services needed to build the flow.
	 */
	private FlowBuilderContext context;

	public void init(FlowBuilderContext context) throws FlowBuilderException {
		this.context = context;
		doInit();
		flow = createFlow();
	}

	/**
	 * Flow builder initialization hook. Does nothing by default. May be overridden by subclasses.
	 */
	protected void doInit() {

	}

	/**
	 * Factory method that initially creates the flow implementation during flow builder initialization. Simply
	 * delegates to the configured flow artifact factory by default.
	 * @return the flow instance, initially created but not yet built
	 */
	protected Flow createFlow() {
		String id = getContext().getFlowId();
		AttributeMap attributes = getContext().getFlowAttributes();
		return getContext().getFlowArtifactFactory().createFlow(id, attributes);
	}

	/**
	 * Returns this flow builder's context.
	 * @return the flow builder context
	 */
	protected FlowBuilderContext getContext() {
		return context;
	}

	public void buildVariables() throws FlowBuilderException {
	}

	public void buildInputMapper() throws FlowBuilderException {
	}

	public void buildStartActions() throws FlowBuilderException {
	}

	public abstract void buildStates() throws FlowBuilderException;

	public void buildGlobalTransitions() throws FlowBuilderException {
	}

	public void buildEndActions() throws FlowBuilderException {
	}

	public void buildOutputMapper() throws FlowBuilderException {
	}

	public void buildExceptionHandlers() throws FlowBuilderException {
	}

	public Flow getFlow() throws FlowBuilderException {
		return flow;
	}

	public void dispose() throws FlowBuilderException {
		flow = null;
		doDispose();
	}

	public boolean hasFlowChanged() {
		return false;
	}

	public String getFlowResourceString() {
		return getClass().getName();
	}

	/**
	 * Flow builder destruction hook. Does nothing by default. May be overridden by subclasses.
	 */
	protected void doDispose() {

	}

}