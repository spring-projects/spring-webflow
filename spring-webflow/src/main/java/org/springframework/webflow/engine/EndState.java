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

import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ActionExecutor;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * A state that ends a flow when entered. This state ends the active flow session of an ongoing flow execution.
 * <p>
 * If the ended session is the "root flow session" the entire flow execution ends, signaling the end of a logical
 * conversation.
 * <p>
 * If the terminated session was acting as a subflow, the flow execution continues and control is returned to the parent
 * flow session. In that case, this state returns an ending result event the resuming parent flow responds to.
 * <p>
 * An end state may be configured with a renderer to render a final response. This renderer will be invoked if the end
 * state terminates the entire flow execution.
 * 
 * @see org.springframework.webflow.engine.SubflowState
 * 
 * @author Keith Donald
 * @author Colin Sampaleanu
 * @author Erwin Vervaet
 */
public class EndState extends State {

	/**
	 * The renderer that will render the final response when a flow execution terminates.
	 */
	private Action finalResponseAction;

	/**
	 * The attribute mapper for mapping output attributes exposed by this end state when it is entered.
	 */
	private Mapper outputMapper;

	/**
	 * Create a new end state with no associated view.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException when this state cannot be added to given flow, e.g. because the id is not unique
	 * @see State#State(Flow, String)
	 * @see #setFinalResponseAction(Action)
	 * @see #setOutputMapper(Mapper)
	 */
	public EndState(Flow flow, String id) throws IllegalArgumentException {
		super(flow, id);
	}

	/**
	 * Sets the renderer that will render the final flow execution response.
	 */
	public void setFinalResponseAction(Action finalResponseAction) {
		this.finalResponseAction = finalResponseAction;
	}

	/**
	 * Sets the attribute mapper to use for mapping output attributes exposed by this end state when it is entered.
	 */
	public void setOutputMapper(Mapper outputMapper) {
		this.outputMapper = outputMapper;
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that executes behavior specific to this state
	 * type in polymorphic fashion.
	 * <p>
	 * This implementation pops the top (active) flow session off the execution stack, ending it, and resumes control in
	 * the parent flow (if necessary). If the ended session is the root flow, a final response is rendered.
	 * @param context the control context for the currently executing flow, used by this state to manipulate the flow
	 * execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected void doEnter(final RequestControlContext context) throws FlowExecutionException {
		FlowSession activeSession = context.getFlowExecutionContext().getActiveSession();
		if (activeSession.isRoot()) {
			// entire flow execution is ending; issue the final response
			if (finalResponseAction != null && context.getExternalContext().isResponseAllowed()) {
				ActionExecutor.execute(finalResponseAction, context);
				context.getExternalContext().recordResponseComplete();
			}
			context.endActiveFlowSession(getId(), createSessionOutput(context));
		} else {
			// there is a parent flow that will resume (this flow is a subflow)
			LocalAttributeMap sessionOutput = createSessionOutput(context);
			context.endActiveFlowSession(getId(), sessionOutput);
		}
	}

	/**
	 * Returns the subflow output map. This will invoke the output mapper (if any) to map data available in the flow
	 * execution request context into a newly created empty map.
	 */
	protected LocalAttributeMap createSessionOutput(RequestContext context) {
		LocalAttributeMap output = new LocalAttributeMap();
		if (outputMapper != null) {
			MappingResults results = outputMapper.map(context, output);
			if (results != null && results.hasErrorResults()) {
				throw new FlowOutputMappingException(getOwner().getId(), getId(), results);
			}
		}
		return output;
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("finalResponseAction", finalResponseAction).append("outputMapper", outputMapper);
	}

}