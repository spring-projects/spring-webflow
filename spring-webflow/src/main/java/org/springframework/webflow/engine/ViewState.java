/*
 * Copyright 2002-2006 the original author or authors.
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
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * A view state is a state that issues a response to the user, for
 * example, for soliciting form input.
 * <p>
 * To accomplish this, a <code>ViewState</code> makes a {@link ViewSelection},
 * which contains the necessary information to issue a suitable response.
 * 
 * @see org.springframework.webflow.engine.ViewSelector
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ViewState extends TransitionableState {

	/**
	 * The list of actions to be executed when this state is entered.
	 */
	private ActionList renderActionList = new ActionList();

	/**
	 * The factory for the view selection to return when this state is entered.
	 */
	private ViewSelector viewSelector = NullViewSelector.INSTANCE;

	/**
	 * Create a new view state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException when this state cannot be added to given
	 * flow, e.g. because the id is not unique
	 */
	public ViewState(Flow flow, String id) throws IllegalArgumentException {
		super(flow, id);
	}

	/**
	 * Returns the strategy used to select the view to render in this view
	 * state.
	 */
	public ViewSelector getViewSelector() {
		return viewSelector;
	}

	/**
	 * Sets the strategy used to select the view to render in this view state.
	 */
	public void setViewSelector(ViewSelector viewSelector) {
		Assert.notNull(viewSelector, "The view selector to make view selections is required");
		this.viewSelector = viewSelector;
	}

	/**
	 * Returns the list of actions executable by this view state on entry and on
	 * refresh. The returned list is mutable.
	 * @return the state action list
	 */
	public ActionList getRenderActionList() {
		return renderActionList;
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that
	 * executes behavior specific to this state type in polymorphic fashion.
	 * <p>
	 * Returns a view selection indicating a response to issue. The view
	 * selection typically contains all the data necessary to issue the
	 * response.
	 * @param context the control context for the currently executing flow, used
	 * by this state to manipulate the flow execution
	 * @return a view selection serving as a response instruction
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected ViewSelection doEnter(RequestControlContext context) throws FlowExecutionException {
		if (viewSelector.isEntrySelectionRenderable(context)) {
			// the entry selection will be rendered!
			renderActionList.execute(context);
		}
		return viewSelector.makeEntrySelection(context);
	}

	/**
	 * Request that the current view selection be reconstituted to support
	 * reissuing the response. This is an idempotent operation that may be
	 * safely called any number of times on a paused execution, used primarily
	 * to support a flow execution redirect.
	 * @param context the request context
	 * @return the view selection
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	public ViewSelection refresh(RequestContext context) throws FlowExecutionException {
		renderActionList.execute(context);
		return viewSelector.makeRefreshSelection(context);
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("viewSelector", viewSelector);
		super.appendToString(creator);
	}
}