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
package org.springframework.webflow.execution;

import java.io.IOException;

/**
 * Allows the client to participate in flow execution. Encapsulates behavior to send the client an appropriate response
 * and handle the resulting event once the client responds.
 * 
 * @author Keith Donald
 * @see ViewFactory
 */
public interface View {

	/**
	 * Well-known attribute name for storing a render fragments value.
	 */
	public static final String RENDER_FRAGMENTS_ATTRIBUTE = "flowRenderFragments";

	/**
	 * Render this view's content.
	 * @throws IOException if an IO Exception occured rendering the view
	 */
	public void render() throws IOException;

	/**
	 * Execute the view resume lifecycle. This typically results in a view model binding and validation.
	 */
	public void processUserEvent();

	/**
	 * Returns true if an event occurred the flow system should handle.
	 * @return true if yes, false otherwise
	 */
	public boolean hasFlowEvent();

	/**
	 * Get the user event the flow should handle. Returns an event object when {@link #hasFlowEvent()} returns
	 * true. Returns null otherwise
	 * @return the event, or null if there is no event for the flow system to handle
	 */
	public Event getFlowEvent();

}
