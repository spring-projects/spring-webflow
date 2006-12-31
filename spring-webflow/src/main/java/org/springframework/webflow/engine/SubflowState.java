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
package org.springframework.webflow.engine;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * A transitionable state that spawns a subflow when executed. When the subflow
 * this state spawns ends, the ending result is used as grounds for a state
 * transition out of this state.
 * <p>
 * A subflow state may be configured to map input data from its flow -- acting
 * as the parent flow -- down to the subflow when the subflow is spawned. In
 * addition, output data produced by the subflow may be mapped up to the parent
 * flow when the subflow ends and the parent flow resumes. See the
 * {@link FlowAttributeMapper} interface definition for more information on how
 * to do this. The logic for ending a subflow is located in the {@link EndState}
 * implementation.
 * 
 * @see org.springframework.webflow.engine.FlowAttributeMapper
 * @see org.springframework.webflow.engine.EndState
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SubflowState extends TransitionableState {

	/**
	 * The subflow that should be spawned when this subflow state is entered.
	 */
	private Flow subflow;

	/**
	 * The attribute mapper that should map attributes from the parent flow down
	 * to the spawned subflow and visa versa.
	 */
	private FlowAttributeMapper attributeMapper;

	/**
	 * Create a new subflow state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @param subflow the subflow to spawn
	 * @throws IllegalArgumentException when this state cannot be added to given
	 * flow, e.g. because the id is not unique
	 * @see #setAttributeMapper(FlowAttributeMapper)
	 */
	public SubflowState(Flow flow, String id, Flow subflow) throws IllegalArgumentException {
		super(flow, id);
		setSubflow(subflow);
	}

	/**
	 * Returns the subflow spawned by this state.
	 */
	public Flow getSubflow() {
		return subflow;
	}

	/**
	 * Set the subflow that will be spawned by this state.
	 * @param subflow the subflow to spawn
	 */
	private void setSubflow(Flow subflow) {
		Assert.notNull(subflow, "A subflow state must have a subflow; the subflow is required");
		this.subflow = subflow;
	}

	/**
	 * Returns the attribute mapper used to map data between the parent and child
	 * flow, or null if no mapping is needed.
	 */
	public FlowAttributeMapper getAttributeMapper() {
		return attributeMapper;
	}

	/**
	 * Set the attribute mapper used to map model data between the parent and
	 * child flow. Can be null if no mapping is needed.
	 */
	public void setAttributeMapper(FlowAttributeMapper attributeMapper) {
		this.attributeMapper = attributeMapper;
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that
	 * executes behaviour specific to this state type in polymorphic fashion.
	 * <p>
	 * Entering this state, creates the subflow input map and spawns the subflow
	 * in the current flow execution.
	 * @param context the control context for the currently executing flow, used
	 * by this state to manipulate the flow execution
	 * @return a view selection containing model and view information needed to
	 * render the results of the state execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected ViewSelection doEnter(RequestControlContext context) throws FlowExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Spawning subflow '" + getSubflow().getId() + "' within flow '" + getFlow().getId() + "'");
		}
		return context.start(getSubflow(), createSubflowInput(context));
	}

	/**
	 * Create the input data map for the spawned subflow session. The returned
	 * map will be passed to {@link Flow#start(RequestControlContext, MutableAttributeMap)}.
	 */
	protected MutableAttributeMap createSubflowInput(RequestContext context) {
		if (getAttributeMapper() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Messaging the configured attribute mapper to map attributes "
						+ "down to the spawned subflow for access within the subflow");
			}
			return getAttributeMapper().createFlowInput(context);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("No attribute mapper configured for this subflow state '" + getId()
						+ "' -- As a result, no attributes will be passed to the spawned subflow '"
						+ subflow.getId() + "'");
			}
			return null;
		}
	}

	/**
	 * Called on completion of the subflow to handle the subflow result event as
	 * determined by the end state reached by the subflow.
	 */
	public ViewSelection onEvent(RequestControlContext context) {
		mapSubflowOutput(context.getLastEvent().getAttributes(), context);
		return super.onEvent(context);
	}

	/**
	 * Map the output data produced by the subflow back into the request context
	 * (typically flow scope).
	 */
	private void mapSubflowOutput(AttributeMap subflowOutput, RequestContext context) {
		if (getAttributeMapper() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Messaging the configured attribute mapper to map subflow result attributes to the "
						+ "resuming parent flow -- It will have access to attributes passed up by the completed subflow");
			}
			attributeMapper.mapFlowOutput(subflowOutput, context);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("No attribute mapper is configured for the resuming subflow state '" + getId()
						+ "' -- As a result, no attributes of the ending flow will be passed to the resuming parent flow");
			}
		}
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("subflow", subflow.getId()).append("attributeMapper", attributeMapper);
		super.appendToString(creator);
	}
}