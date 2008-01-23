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
package org.springframework.webflow.context;

import java.io.PrintWriter;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * A facade that provides normalized access to an external system that has called into the Spring Web Flow system.
 * <p>
 * This context object provides a normalized interface for internal web flow artifacts to use to reason on and
 * manipulate the state of an external actor calling into SWF to execute flows. It represents the context about a
 * single, <i>external</i> client request to manipulate a flow execution.
 * <p>
 * The design of this interface was inspired by JSF's own ExternalContext abstraction and shares the same name for
 * consistency. If a particular external client type does not support all methods defined by this interface, they can
 * just be implemented as returning an empty map or <code>null</code>.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public interface ExternalContext {

	/**
	 * Returns the unique id of the flow definition invoked by this caller. For a "launch" request, this identifier is
	 * used directly to create a new flow execution. For a "resume" request, this identifier provides additional flow
	 * execution context.
	 * @return the flow definition identifier, never null
	 * @see #getFlowExecutionKey()
	 */
	public String getFlowId();

	/**
	 * Returns the unique key identifying the flow execution this client wishes to resume.
	 * @return the flow execution key, may be null if this is not a resume request
	 */
	public String getFlowExecutionKey();

	/**
	 * Returns the type of this client request. For example, this request may be a "GET" request or a "POST" request.
	 * @return the request method
	 */
	public String getRequestMethod();

	/**
	 * Returns the path of this request as a ordered list of fields.
	 * @return the elements of the request path
	 */
	public RequestPath getRequestPath();

	/**
	 * Provides access to the parameters associated with the user request that led to SWF being called. This map is
	 * expected to be immutable and cannot be changed.
	 * @return the immutable request parameter map
	 */
	public ParameterMap getRequestParameterMap();

	/**
	 * Provides access to the external request attribute map, providing a storage for data local to the current user
	 * request and accessible to both internal and external SWF artifacts.
	 * @return the mutable request attribute map
	 */
	public MutableAttributeMap getRequestMap();

	/**
	 * Provides access to the external session map, providing a storage for data local to the current user session and
	 * accessible to both internal and external SWF artifacts.
	 * @return the mutable session attribute map
	 */
	public SharedAttributeMap getSessionMap();

	/**
	 * Provides access to the <i>global</i> external session map, providing a storage for data globally accross the
	 * user session and accessible to both internal and external SWF artifacts.
	 * <p>
	 * Note: most external context implementations do not distinguish between the concept of a "local" user session
	 * scope and a "global" session scope. The Portlet world does, but not the Servlet for example. In those cases
	 * calling this method returns the same map as calling {@link #getSessionMap()}.
	 * @return the mutable global session attribute map
	 */
	public SharedAttributeMap getGlobalSessionMap();

	/**
	 * Provides access to the external application map, providing a storage for data local to the current user
	 * application and accessible to both internal and external SWF artifacts.
	 * @return the mutable application attribute map
	 */
	public SharedAttributeMap getApplicationMap();

	/**
	 * Provides access to the context object for the current environment.
	 * @return the environment specific context object
	 */
	public Object getContext();

	/**
	 * Provides access to the request object for the current environment.
	 * @return the environment specific request object.
	 */
	public Object getRequest();

	/**
	 * Provides access to the response object for the current environment.
	 * @return the environment specific response object.
	 */
	public Object getResponse();

	/**
	 * Get a writer for writing out a response.
	 * @return the writer
	 */
	public PrintWriter getResponseWriter();

	/**
	 * Builds a context-relative flow definition URL, suitable for rendering links that a launch new execution of a flow
	 * definition when accessed.
	 * @param requestInfo data needed to build the flow definition path
	 * @return the generated flow definition URL
	 */
	public String buildFlowDefinitionUrl(FlowDefinitionRequestInfo requestInfo);

	/**
	 * Builds a flow execution URL, suitable for rendering links that resume a paused flow execution when accessed.
	 * @param requestInfo data needed to build the flow execution URL
	 * @param contextRelative whether the URL returned should be relative to this external context or absolute.
	 * @return the generated flow execution URL
	 */
	public String buildFlowExecutionUrl(FlowExecutionRequestInfo requestInfo, boolean contextRelative);

	/**
	 * Encode the provided string using the encoding scheme of this external context.
	 * @param string the string
	 * @return the encoded string
	 */
	public String encode(String string);

	/**
	 * Request that a flow execution redirect be sent as the response. A flow execution redirect tells the caller to
	 * resume a flow execution in a new request. Sets response committed to true.
	 * @param requestInfo data needed to issue the flow execution redirect
	 * @see #isResponseCommitted()
	 */
	public void sendFlowExecutionRedirect(FlowExecutionRequestInfo requestInfo);

	/**
	 * Request that a flow definition redirect be sent as the response. A flow definition redirect tells the caller to
	 * start a new execution of the flow definition with the input provided.
	 * @param requestInfo data needed to issue the flow definition redirect
	 */
	public void sendFlowDefinitionRedirect(FlowDefinitionRequestInfo requestInfo);

	/**
	 * Request that a external redirect be sent as the response. An external redirect tells the caller to access the
	 * resource at the given resource URL. Sets response committed to true. Note: no special encoding is performed on
	 * the string argument. Callers must perform their own encoding when necessary.
	 * @param resourceUrl the resource URL string
	 * @see #isResponseCommitted()
	 */
	public void sendExternalRedirect(String resourceUrl);

	/**
	 * Report that flow execution request processing ended with a "paused" result, indicating the flow execution paused
	 * and will be waiting to resume on a subsequent request.
	 * @param flowExecutionKey the flow execution key
	 */
	public void setPausedResult(String flowExecutionKey);

	/**
	 * Report that flow execution request processing ended with a "ended" result, indicating the flow execution
	 * terminated.
	 * @param flowExecutionKey the flow execution key, now invalid or null if never assigned
	 */
	public void setEndedResult(String flowExecutionKey);

	/**
	 * Report that flow execution request processing ended with a flow exception.
	 * @param e the flow exception
	 */
	public void setExceptionResult(FlowException e);

	/**
	 * Returns true if the current request has already provisioned the response that will be sent back to the calling
	 * system.
	 * @return true if the response has been committed, false otherwise
	 * @see #sendFlowExecutionRedirect(FlowExecutionRequestInfo)
	 * @see #sendFlowDefinitionRedirect(FlowDefinitionRequestInfo)
	 * @see #sendExternalRedirect(String)
	 */
	public boolean isResponseCommitted();

	/**
	 * Returns true if the current request is an Ajax request, determined by the value of the http accept header.
	 * @return true if the current request is an Ajax request
	 */
	public boolean isAjaxRequest();

	public void setResponseHeader(String name, String value);

}