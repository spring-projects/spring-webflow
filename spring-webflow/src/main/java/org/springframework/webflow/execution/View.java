/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.execution;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.webflow.engine.ViewState;

/**
 * Allows a client to participate in flow execution. Encapsulates behavior to send the client an appropriate response
 * and handle the resulting event once the client responds.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 * @see ViewFactory
 */
public interface View {

	/**
	 * Well-known attribute name for storing a render fragments value.
	 */
	String RENDER_FRAGMENTS_ATTRIBUTE = "flowRenderFragments";

	/**
	 * Well-known attribute name for storing the results of processing a user event
	 */
	String USER_EVENT_STATE_ATTRIBUTE = "viewUserEventState";

	/**
	 * Render this view's content.
	 * @throws IOException if an IO Exception occured rendering the view
	 */
	void render() throws IOException;

	/**
	 * True if there is a user event queued this view should process.
	 * @return true if a user event is queued, false if not
	 */
	boolean userEventQueued();

	/**
	 * Process the queued user event. Should only be called when {@link #userEventQueued()} returns true. After calling
	 * this method, a flow event may be raised that should be handled in the Web Flow system.
	 * @see #hasFlowEvent()
	 */
	void processUserEvent();

	/**
	 * True if a call to {@link #processUserEvent()} raised a flow event the current state should handle. Call
	 * {@link #getFlowEvent()} to access the Event.
	 * @return true if yes, false otherwise
	 */
	boolean hasFlowEvent();

	/**
	 * Get the flow event the current state should handle. Returns an Event object when {@link #hasFlowEvent()} returns
	 * true. Returns <code>null</code> otherwise.
	 * @return the event, or <code>null</code> if there is no event for the flow system to handle
	 */
	Event getFlowEvent();

	/**
	 * A memento holding the results of processing a user event. Used to allow transient view state such as binding and
	 * validation errors to survive a flow execution redirect.
	 * @return the serializable user event state object, or null if no event state needs managing
	 * @see #processUserEvent()
	 */
	Serializable getUserEventState();

	/**
	 * Saves any state associated with this view out to view scope. Called when exiting a {@link ViewState} to allow for
	 * any changes applied after postback processing to be captured and reflected when going back. Can be a no-op for
	 * views that store no view state.
	 */
	void saveState();

}
