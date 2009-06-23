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
package org.springframework.webflow.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;

/**
 * A controller helper used for customizing access to a <i>single</i> flow definition in a Servlet enviroment. This
 * helper is used to:
 * <ol>
 * <li>Launch executions of that flow with data in the execution input map
 * <li>Handle outcomes reached by that flow in a custom manner
 * <li>Handle unhandled exceptions dealing with that flow in a custom manner.
 * </ol>
 * Such a handler can be visually thought of as a "flow reference" on a Garrett IA diagram. It holds a reference to the
 * flow id to launch, how to provision its input, how to process its outcomes, and how to handle uncaught exceptions.
 * 
 * @author Keith Donald
 */
public interface FlowHandler {

	/**
	 * Returns the id of the flow handled by this handler. Used by a Controller to load the flow definition. Optional.
	 * @return the flow id, or null if the flow id should be determined by the caller
	 */
	public String getFlowId();

	/**
	 * Creates the flow execution input map to pass to a new instance of the flow being started. Used by a Controller to
	 * launch the flow execution with the correct input.
	 * @param request the current request
	 * @return the input map, or null if the contents of the input map should be determined by the caller
	 */
	public MutableAttributeMap createExecutionInputMap(HttpServletRequest request);

	/**
	 * Handles a specific flow execution outcome. Used by a Controller to get the location of the resource to redirect
	 * to after the outcome is handled.
	 * <p>
	 * The following prefixes are supported for qualifying the returned location string:
	 * <ul>
	 * <li>servletRelative: - the location is relative to the current servlet</li>
	 * <li>contextRelative: - the location is relative to the current web application</li>
	 * <li>serverRelative: - the location is relative to the server root</li>
	 * </ul>
	 * If the returned location is a path with no prefix, for example "/hotels/index", it is treated as relative to the
	 * current servlet by default. Fully qualified URLs beginning with http:// or https:// may also be returned.
	 * <p>
	 * For servlet-relative, context-relative, and server-relative URLs, a leading slash is optional.
	 * 
	 * @param outcome the outcome that was reached
	 * @param request the current request
	 * @param response the current response
	 * @return the location of the new resource to redirect to, or null if the execution outcome was not handled and
	 * should be handled by the caller
	 */
	public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Handles a flow exception that was not handled by the Web Flow system. Used by a Controller to handle a specific
	 * type of exception dealing with this flow in a custom manner.
	 * @param e the unhandled exception originating from Spring Web Flow. May be thrown by the flow execution itself or
	 * the flow executor system if no execution could be restored.
	 * @param request the current request
	 * @param response the current response
	 * @return the location of the error resource to redirect to, or null if the execution outcome was not handled and
	 * should be handled by the caller
	 */
	public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response);
}
