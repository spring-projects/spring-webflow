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
package org.springframework.webflow.engine;

import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * A state that ends a flow when entered. More specifically, this state ends the
 * active flow session of the active flow execution associated with the current
 * request context.
 * <p>
 * If the ended session is the "root flow session" the entire flow execution
 * ends, signaling the end of a logical conversation.
 * <p>
 * If the terminated session was acting as a subflow the flow execution
 * continues and control is returned to the parent flow session. In that case,
 * this state returns an ending result event the resuming parent flow is
 * expected to respond to.
 * <p>
 * An end state may optionally be configured with the name of a view to render
 * when entered. This view will be rendered if the end state terminates the
 * entire flow execution as a kind of flow ending "confirmation page".
 * <p>
 * Note: if no <code>viewName</code> property is specified <b>and</b> this
 * end state terminates the entire flow execution it is expected that some
 * action has already written the response (or else a blank response will
 * result). On the other hand, if no <code>viewName</code> is specified <b>and</b>
 * this end state relinquishes control back to a parent flow, view selection
 * responsibility falls on the parent flow.
 * 
 * @see org.springframework.webflow.engine.ViewSelector
 * @see org.springframework.webflow.engine.SubflowState
 * 
 * @author Keith Donald
 * @author Colin Sampaleanu
 * @author Erwin Vervaet
 */
public class EndState extends State {

	/**
	 * The optional view selector that will select a view to render if this end
	 * state terminates a root flow session.
	 */
	private ViewSelector viewSelector = NullViewSelector.INSTANCE;

	/**
	 * Attribute mapper for mapping output attributes exposed by this end state
	 * when it is entered.
	 */
	private AttributeMapper outputMapper;

	/**
	 * Create a new end state with no associated view.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException when this state cannot be added to given
	 * flow, e.g. because the id is not unique
	 * @see State#State(Flow, String)
	 * @see #setViewSelector(ViewSelector)
	 * @see #setOutputMapper(AttributeMapper)
	 */
	public EndState(Flow flow, String id) throws IllegalArgumentException {
		super(flow, id);
	}

	/**
	 * Returns the strategy used to select the view to render in this end state
	 * if it terminates a root flow.
	 */
	public ViewSelector getViewSelector() {
		return viewSelector;
	}

	/**
	 * Sets the strategy used to select the view to render when this end state
	 * is entered and terminates a root flow.
	 */
	public void setViewSelector(ViewSelector viewSelector) {
		Assert.notNull(viewSelector, "The view selector is required");
		this.viewSelector = viewSelector;
	}

	/**
	 * Returns the configured attribute mapper for mapping output attributes
	 * exposed by this end state when it is entered.
	 */
	public AttributeMapper getOutputMapper() {
		return outputMapper;
	}

	/**
	 * Sets the attribute mapper to use for mapping output attributes exposed by
	 * this end state when it is entered.
	 */
	public void setOutputMapper(AttributeMapper outputMapper) {
		this.outputMapper = outputMapper;
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that
	 * executes behaviour specific to this state type in polymorphic fashion.
	 * <p>
	 * This implementation pops the top (active) flow session off the execution
	 * stack, ending it, and resumes control in the parent flow (if neccessary).
	 * If the ended session is the root flow, a {@link ViewSelection} is
	 * returned.
	 * @param context the control context for the currently executing flow, used
	 * by this state to manipulate the flow execution
	 * @return a view selection signaling that control should be returned to the
	 * client and a view rendered
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected ViewSelection doEnter(RequestControlContext context) throws FlowExecutionException {
		FlowSession activeSession = context.getFlowExecutionContext().getActiveSession();
		if (activeSession.isRoot()) {
			// entire flow execution is ending, return ending view if applicable
			ViewSelection selectedView = viewSelector.makeEntrySelection(context);
			context.endActiveFlowSession(createSessionOutput(context));
			return selectedView;
		}
		else {
			// there is a parent flow that will resume (this flow is a subflow)
			LocalAttributeMap sessionOutput = createSessionOutput(context);
			context.endActiveFlowSession(sessionOutput);
			return context.signalEvent(new Event(this, getId(), sessionOutput));
		}
	}

	/**
	 * Returns the subflow output map. This will invoke the output mapper (if any)
	 * to map data available in the flow execution request context into a newly
	 * creaed empty map.
	 */
	protected LocalAttributeMap createSessionOutput(RequestContext context) {
		LocalAttributeMap outputMap = new LocalAttributeMap();
		if (outputMapper != null) {
			outputMapper.map(context, outputMap, null);
		}
		return outputMap;
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("viewSelector", viewSelector).append("outputMapper", outputMapper);
	}
}