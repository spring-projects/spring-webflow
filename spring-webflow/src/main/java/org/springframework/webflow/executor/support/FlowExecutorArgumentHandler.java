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
package org.springframework.webflow.executor.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.core.JdkVersion;
import org.springframework.util.StringUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;

/**
 * Abstract base class for objects handling
 * {@link org.springframework.webflow.executor.FlowExecutor} arguments. This
 * class combines the two argument handling responsabilities of ({@link FlowExecutorArgumentExtractor extraction}
 * and {@link FlowExecutorArgumentExposer exposing}) and makes sure they are
 * consistent, i.e. that exposed arguments can later be extracted again.
 * <p>
 * All argument names are configurable. Common convenience functionality is also
 * provided, e.g. a {@link #applyDefaultFlowId(String) default flow id},
 * {@link #encodeValue(Object) URL encoding} and dealing with
 * {@link #makeRedirectUrlContextRelativeIfNecessary(String, ExternalContext) relative URLs}.
 * Subclasses are responsible for taking these settings into account when
 * implementing actual argument extraction and exposing behavior.
 * 
 * @see FlowExecutorArgumentExtractor
 * @see FlowExecutorArgumentExposer
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class FlowExecutorArgumentHandler implements FlowExecutorArgumentExtractor, FlowExecutorArgumentExposer {

	// data and behavior related to argument extraction

	/**
	 * By default clients can send the id of the flow definition to be launched
	 * using an argument with this name ("_flowId").
	 */
	private static final String FLOW_ID_ARGUMENT_NAME = "_flowId";

	/**
	 * By default clients can send the key of a flow execution to be resumed
	 * using an argument with this name ("_flowExecutionKey").
	 */
	private static final String FLOW_EXECUTION_KEY_ARGUMENT_NAME = "_flowExecutionKey";

	/**
	 * By default clients can send the event to be signaled in an argument with
	 * this name ("_eventId").
	 */
	private static final String EVENT_ID_ARGUMENT_NAME = "_eventId";

	/**
	 * Identifies a flow definition to launch a new execution for, defaults to
	 * {@link #FLOW_ID_ARGUMENT_NAME}.
	 */
	private String flowIdArgumentName = FLOW_ID_ARGUMENT_NAME;

	/**
	 * Input argument that identifies an existing flow execution to participate
	 * in, defaults to {@link #FLOW_EXECUTION_KEY_ARGUMENT_NAME}.
	 */
	private String flowExecutionKeyArgumentName = FLOW_EXECUTION_KEY_ARGUMENT_NAME;

	/**
	 * Identifies an event that occured in an existing flow execution, defaults
	 * to {@link #EVENT_ID_ARGUMENT_NAME}.
	 */
	private String eventIdArgumentName = EVENT_ID_ARGUMENT_NAME;

	/**
	 * The flow definition id to use if no flowId argument value can be
	 * extracted during the {@link #extractFlowId(ExternalContext)} operation.
	 * Default value is <code>null</code>.
	 */
	private String defaultFlowId;

	/**
	 * Returns the flow id argument name, used to request a flow to launch.
	 */
	public String getFlowIdArgumentName() {
		return flowIdArgumentName;
	}

	/**
	 * Sets the flow id argument name, used to request a flow to launch.
	 */
	public void setFlowIdArgumentName(String flowIdArgumentName) {
		this.flowIdArgumentName = flowIdArgumentName;
	}

	/**
	 * Returns the flow execution key argument name, used to request that an
	 * executing conversation resumes.
	 */
	public String getFlowExecutionKeyArgumentName() {
		return flowExecutionKeyArgumentName;
	}

	/**
	 * Sets the flow execution key argument name, used to request that an
	 * executing conversation resumes.
	 */
	public void setFlowExecutionKeyArgumentName(String flowExecutionKeyArgumentName) {
		this.flowExecutionKeyArgumentName = flowExecutionKeyArgumentName;
	}

	/**
	 * Returns the event id argument name, used to signal what user action
	 * happened within a paused flow execution.
	 */
	public String getEventIdArgumentName() {
		return eventIdArgumentName;
	}

	/**
	 * Sets the event id argument name, used to signal what user action happened
	 * within a paused flow execution.
	 */
	public void setEventIdArgumentName(String eventIdArgumentName) {
		this.eventIdArgumentName = eventIdArgumentName;
	}

	/**
	 * Returns the <i>default</i> flowId argument value. If no flow id argument
	 * is provided, the default acts as a fallback. Defaults to
	 * <code>null</code>.
	 */
	public String getDefaultFlowId() {
		return defaultFlowId;
	}

	/**
	 * Sets the default flowId argument value.
	 * <p>
	 * This value will be used if no flowId argument value can be extracted from
	 * the request by the {@link #extractFlowId(ExternalContext)} operation.
	 */
	public void setDefaultFlowId(String defaultFlowId) {
		this.defaultFlowId = defaultFlowId;
	}

	// data and behavior for response issuance

	/**
	 * The string-encoded id of the flow execution will be exposed to the view
	 * in a model attribute with this name ("flowExecutionKey").
	 */
	private static final String FLOW_EXECUTION_KEY_ATTRIBUTE = "flowExecutionKey";

	/**
	 * The flow execution context itself will be exposed to the view in a model
	 * attribute with this name ("flowExecutionContext").
	 */
	private static final String FLOW_EXECUTION_CONTEXT_ATTRIBUTE = "flowExecutionContext";

	/**
	 * The default URL encoding scheme: UTF-8.
	 */
	private static final String DEFAULT_URL_ENCODING_SCHEME = "UTF-8";

	/**
	 * Model attribute that identifies the flow execution participated in,
	 * defaults to {@link #FLOW_EXECUTION_KEY_ATTRIBUTE}.
	 */
	private String flowExecutionKeyAttributeName = FLOW_EXECUTION_KEY_ATTRIBUTE;

	/**
	 * Model attribute that provides state about the flow execution participated
	 * in, defaults to {@link #FLOW_EXECUTION_CONTEXT_ATTRIBUTE}.
	 */
	private String flowExecutionContextAttributeName = FLOW_EXECUTION_CONTEXT_ATTRIBUTE;

	/**
	 * The url encoding scheme to be used to encode URLs built by this argument
	 * handler. Defaults to {@link #DEFAULT_URL_ENCODING_SCHEME}.
	 */
	private String urlEncodingScheme = DEFAULT_URL_ENCODING_SCHEME;

	/**
	 * A flag indicating whether to interpret a redirect URL that starts with a
	 * slash ("/") as relative to the current ServletContext, i.e. as relative
	 * to the web application root, as opposed to absolute. Default is true.
	 */
	private boolean redirectContextRelative = true;

	/**
	 * Returns the flow execution key attribute name, used as a model attribute
	 * for identifying the executing flow being participated in.
	 */
	public String getFlowExecutionKeyAttributeName() {
		return flowExecutionKeyAttributeName;
	}

	/**
	 * Sets the flow execution key attribute name, used as a model attribute for
	 * identifying the current state of the executing flow being participated in
	 * (typically used by view templates during rendering).
	 */
	public void setFlowExecutionKeyAttributeName(String flowExecutionKeyAttributeName) {
		this.flowExecutionKeyAttributeName = flowExecutionKeyAttributeName;
	}

	/**
	 * Returns the flow execution context attribute name.
	 */
	public String getFlowExecutionContextAttributeName() {
		return flowExecutionContextAttributeName;
	}

	/**
	 * Sets the flow execution context attribute name.
	 */
	public void setFlowExecutionContextAttributeName(String flowExecutionContextAttributeName) {
		this.flowExecutionContextAttributeName = flowExecutionContextAttributeName;
	}

	/**
	 * Returns the url encoding scheme to be used to encode URLs built by this
	 * argument handler. Defaults to "UTF-8".
	 */
	public String getUrlEncodingScheme() {
		return urlEncodingScheme;
	}

	/**
	 * Set the url encoding scheme to be used to encode URLs built by this
	 * argument handler. Defaults to "UTF-8".
	 */
	public void setUrlEncodingScheme(String urlEncodingScheme) {
		this.urlEncodingScheme = urlEncodingScheme;
	}

	/**
	 * Set whether to interpret a given redirect URL that starts with a slash
	 * ("/") as relative to the current ServletContext, i.e. as relative to the
	 * web application root.
	 * <p>
	 * Default is "true": A redirect URL that starts with a slash will be
	 * interpreted as relative to the web application root, i.e. the context
	 * path will be prepended to the URL.
	 */
	public void setRedirectContextRelative(boolean redirectContextRelative) {
		this.redirectContextRelative = redirectContextRelative;
	}

	/**
	 * Return whether to interpret a given redirect URL that starts with a slash
	 * ("/") as relative to the current ServletContext, i.e. as relative to the
	 * web application root.
	 */
	public boolean isRedirectContextRelative() {
		return redirectContextRelative;
	}

	public abstract boolean isFlowIdPresent(ExternalContext context);

	public abstract String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException;

	public abstract boolean isFlowExecutionKeyPresent(ExternalContext context);

	public abstract String extractFlowExecutionKey(ExternalContext context)
			throws FlowExecutorArgumentExtractionException;

	public abstract boolean isEventIdPresent(ExternalContext context);

	public abstract String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException;

	public void exposeFlowExecutionContext(String flowExecutionKey, FlowExecutionContext context, Map model) {
		if (flowExecutionKey != null) {
			model.put(getFlowExecutionKeyAttributeName(), flowExecutionKey);
		}
		model.put(getFlowExecutionContextAttributeName(), context);
	}

	public abstract String createFlowDefinitionUrl(FlowDefinitionRedirect flowDefinitionRedirect,
			ExternalContext context);

	public abstract String createFlowExecutionUrl(String flowExecutionKey, FlowExecutionContext flowExecution,
			ExternalContext context);

	public abstract String createExternalUrl(ExternalRedirect redirect, String flowExecutionKey, ExternalContext context);

	// helpers for use in subclasses

	/**
	 * Apply the configured default flow id to given extracted flow id.
	 * @param extractedFlowId the extracted flow id, could be null if non was
	 * available in the external context
	 * @return the extracted flow id if not empty, the default flow id otherwise
	 * (which could still be null if not set)
	 * @see #getDefaultFlowId()
	 */
	protected String applyDefaultFlowId(String extractedFlowId) {
		return StringUtils.hasText(extractedFlowId) ? extractedFlowId : getDefaultFlowId();
	}

	/**
	 * URL-encode the given input object with the configured encoding scheme.
	 * @param value the unencoded value
	 * @return the encoded output String
	 * @see #getUrlEncodingScheme()
	 */
	protected String encodeValue(Object value) {
		return value != null ? urlEncode(value.toString()) : "";
	}

	/**
	 * Make given redirect URL context relative if necessary. If the URL starts
	 * with a slash ("/") it will be made relative to the current
	 * ServletContext, i.e. relative to the web application root.
	 * @param url the original URL
	 * @param context the external context
	 * @return the processed URL
	 * @see #isRedirectContextRelative()
	 */
	protected String makeRedirectUrlContextRelativeIfNecessary(String url, ExternalContext context) {
		StringBuffer res = new StringBuffer();
		if (url.startsWith("/") && isRedirectContextRelative()) {
			res.append(context.getContextPath());
		}
		res.append(url);
		return res.toString();
	}

	// internal helpers

	/**
	 * URL-encode the given input String with the configured encoding scheme.
	 * <p>
	 * Default implementation uses <code>URLEncoder.encode(input, enc)</code>
	 * on JDK 1.4+, falling back to <code>URLEncoder.encode(input)</code>
	 * (which uses the platform default encoding) on JDK 1.3.
	 * @param input the unencoded input String
	 * @return the encoded output String
	 */
	private String urlEncode(String input) {
		if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
			return URLEncoder.encode(input);
		}
		try {
			return URLEncoder.encode(input, getUrlEncodingScheme());
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Cannot encode URL " + input);
		}
	}
}