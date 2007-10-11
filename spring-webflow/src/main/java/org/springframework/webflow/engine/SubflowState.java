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
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A transitionable state that spawns a subflow when executed. When the subflow this state spawns ends, the ending
 * result is used as grounds for a state transition out of this state.
 * <p>
 * A subflow state may be configured to map input data from its flow -- acting as the parent flow -- down to the subflow
 * when the subflow is spawned. In addition, output data produced by the subflow may be mapped up to the parent flow
 * when the subflow ends and the parent flow resumes. See the {@link FlowAttributeMapper} interface definition for more
 * information on how to do this. The logic for ending a subflow is located in the {@link EndState} implementation.
 * 
 * @see org.springframework.webflow.engine.FlowAttributeMapper
 * @see org.springframework.webflow.engine.EndState
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SubflowState extends TransitionableState {

	/**
	 * The subflow that should be spawned when this subflow state is entered. TODO - late binding
	 */
	private Flow subflow;

	/**
	 * The attribute mapper that should map attributes from the parent flow down to the spawned subflow and visa versa.
	 */
	private FlowAttributeMapper attributeMapper = new NoAttributeMapper();

	/**
	 * Create a new subflow state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @param subflow the subflow to spawn
	 * @throws IllegalArgumentException when this state cannot be added to given flow, e.g. because the id is not unique
	 * @see #setAttributeMapper(FlowAttributeMapper)
	 */
	public SubflowState(Flow flow, String id, Flow subflow) throws IllegalArgumentException {
		super(flow, id);
		setSubflow(subflow);
	}

	/**
	 * Set the subflow this state will call.
	 */
	private void setSubflow(Flow subflow) {
		Assert.notNull(subflow, "A subflow state must have a subflow; the subflow is required");
		this.subflow = subflow;
	}

	/**
	 * Set the attribute mapper used to map model data between the parent and child flow.
	 */
	public void setAttributeMapper(FlowAttributeMapper attributeMapper) {
		Assert.notNull(attributeMapper, "The attribute mapper is required");
		this.attributeMapper = attributeMapper;
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that executes behaviour specific to this state
	 * type in polymorphic fashion.
	 * <p>
	 * Entering this state, creates the subflow input map and spawns the subflow in the current flow execution.
	 * @param context the control context for the currently executing flow, used by this state to manipulate the flow
	 * execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected void doEnter(RequestControlContext context) throws FlowExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Calling subflow '" + subflow.getId() + "'");
		}
		context.start(subflow, attributeMapper.createFlowInput(context));
	}

	/**
	 * Called on completion of the subflow to handle the subflow result event as determined by the end state reached by
	 * the subflow.
	 */
	public void handleEvent(RequestControlContext context) {
		attributeMapper.mapFlowOutput(context.getLastEvent().getAttributes(), context);
		super.handleEvent(context);
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("subflow", subflow.getId()).append("attributeMapper", attributeMapper);
		super.appendToString(creator);
	}

	/**
	 * Maps no output attributes. The default implementation.
	 */
	private class NoAttributeMapper implements FlowAttributeMapper {
		public MutableAttributeMap createFlowInput(RequestContext context) {
			logger.debug("No input will be passed to subflow");
			return new LocalAttributeMap();
		}

		public void mapFlowOutput(AttributeMap flowOutput, RequestContext context) {
			logger.debug("No subflow output will be mapped");
		}
	}

}