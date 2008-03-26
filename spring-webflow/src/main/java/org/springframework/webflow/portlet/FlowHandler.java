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
package org.springframework.webflow.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;

/**
 * A controller helper used for customizing access to a <i>single</i> flow definition. This helper is used to:
 * <ol>
 * <li>Launch executions of that flow with data in the execution input map
 * <li>Handle outcomes reached by that flow in a custom manner.
 * <li>Handle un-handled exceptions dealing with that flow in a custom manner.
 * </ol>
 * Such a handler can be visually thought of as a "flow reference" on a Garrett IA diagram. It holds a reference to the
 * flow id to launch, how to provision its input, how to process its outcomes, and how to handle uncaught exceptions.
 * 
 * @author Keith Donald
 */
public interface FlowHandler {

	/**
	 * Returns the id of the flow handled by this handler. Used by a Controller to load the flow definition.
	 * @return the flow id
	 */
	public String getFlowId();

	/**
	 * Creates the flow execution input map to pass to a new instance of the flow being started. Used by a Controller to
	 * launch the flow execution with the correct input.
	 * @param request the current request
	 * @return the input map
	 */
	public MutableAttributeMap createExecutionInputMap(PortletRequest request);

	/**
	 * Handles a specific flow execution outcome. Used to select a new view to render after the flow ends.
	 * @param outcome the outcome that was reached
	 * @param output the output returned by the flow execution
	 * @param request the current render request
	 * @param response the current render response
	 * @return the model and view to render on the occurrence of this outcome, or null if the outcome was not handled
	 */
	public ModelAndView handleFlowOutcome(String outcome, AttributeMap output, RenderRequest request,
			RenderResponse response);

	/**
	 * Handles a flow exception that was not handled by the Web Flow system. Used by a Controller to handle a specific
	 * type of exception dealing with this flow in a custom manner.
	 * @param e the unhandled exception originating from Spring Web Flow. May be thrown by the flow execution itself or
	 * the flow executor system if no execution could be restored.
	 * @param request the current request
	 * @param response the current response
	 * @return the model and view to render on the occurrence of this exception, or null if the exception is not handled
	 */
	public ModelAndView handleException(FlowException e, RenderRequest request, RenderResponse response);

}
