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
package org.springframework.webflow.engine;

import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A transitionable state that spawns a subflow when executed. When the subflow this state spawns ends, the ending
 * result is used as grounds for a state transition out of this state.
 * <p>
 * A subflow state may be configured to map input data from its flow -- acting as the parent flow -- down to the subflow
 * when the subflow is spawned. In addition, output data produced by the subflow may be mapped up to the parent flow
 * when the subflow ends and the parent flow resumes. See the {@link SubflowAttributeMapper} interface definition for
 * more information on how to do this. The logic for ending a subflow is located in the {@link EndState} implementation.
 * 
 * @see org.springframework.webflow.engine.SubflowAttributeMapper
 * @see org.springframework.webflow.engine.EndState
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SubflowState extends TransitionableState {

	/**
	 * The subflow that should be spawned when this subflow state is entered.
	 */
	private Expression subflow;

	/**
	 * The attribute mapper that should map attributes from the parent flow down to the spawned subflow and visa versa.
	 */
	private SubflowAttributeMapper subflowAttributeMapper;

	/**
	 * Create a new subflow state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @param subflow the subflow to spawn
	 * @throws IllegalArgumentException when this state cannot be added to given flow, e.g. because the id is not unique
	 * @see #setAttributeMapper(SubflowAttributeMapper)
	 */
	public SubflowState(Flow flow, String id, Expression subflow) throws IllegalArgumentException {
		super(flow, id);
		setSubflow(subflow);
	}

	/**
	 * Set the subflow this state will call.
	 */
	private void setSubflow(Expression subflow) {
		Assert.notNull(subflow, "A subflow state must have a subflow; the subflow is required");
		this.subflow = subflow;
	}

	/**
	 * Set the attribute mapper used to map model data between the parent and child flow.
	 */
	public void setAttributeMapper(SubflowAttributeMapper attributeMapper) {
		this.subflowAttributeMapper = attributeMapper;
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
		MutableAttributeMap flowInput;
		if (subflowAttributeMapper != null) {
			flowInput = subflowAttributeMapper.createSubflowInput(context);
		} else {
			flowInput = new LocalAttributeMap();
		}
		Flow subflow = (Flow) this.subflow.getValue(context);
		if (logger.isDebugEnabled()) {
			logger.debug("Calling subflow '" + subflow.getId() + "' with input " + flowInput);
		}
		context.start(subflow, flowInput);
	}

	/**
	 * Called on completion of the subflow to handle the subflow result event as determined by the end state reached by
	 * the subflow.
	 */
	public boolean handleEvent(RequestControlContext context) {
		if (subflowAttributeMapper != null) {
			AttributeMap subflowOutput = context.getCurrentEvent().getAttributes();
			if (logger.isDebugEnabled()) {
				logger.debug("Mapping subflow output " + subflowOutput);
			}
			subflowAttributeMapper.mapSubflowOutput(subflowOutput, context);
		}
		return super.handleEvent(context);
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("subflow", subflow).append("subflowAttributeMapper", subflowAttributeMapper);
		super.appendToString(creator);
	}

}