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
package org.springframework.webflow.executor.support;

import java.util.Enumeration;
import java.util.Properties;

import org.springframework.util.StringUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;

/**
 * Flow executor argument handler that wraps another argument handler and applies a public to private flow id mapping.
 * This can be used to avoid literal flow ids in URLs that launch flows.
 * <p>
 * For example, when used in combination with {@link RequestPathFlowExecutorArgumentHandler} the url
 * <code>http://localhost/springair/reservation/booking.html</code> would launch a new execution of the
 * <code>booking-flow</code> flow, assuming a context path of <code>/springair</code>, a servlet mapping of
 * <code>/reservation/*</code> and a flow id mapping of <code>booking-&gt;booking-flow</code>
 * (the .html suffix would be removed by {@link RequestPathFlowExecutorArgumentHandler#extractFlowId(ExternalContext)}.
 * 
 * @see RequestParameterFlowExecutorArgumentHandler
 * @see RequestPathFlowExecutorArgumentHandler
 * 
 * @since 1.0.2
 * 
 * @author Andrej Zachar
 * @author Erwin Vervaet
 */
public class FlowIdMappingArgumentHandlerWrapper extends FlowExecutorArgumentHandler {

	/**
	 * The mappings between client-submitted flow identifiers and internal flow identifiers.
	 */
	private Properties mappings = new Properties();

	/**
	 * The reverse: mappings between internal flow identifiers and client-submitted flow identifiers.
	 */
	private Properties reverseMappings = new Properties();

	/**
	 * Whether or not to fallback to the argument handler delegate if no mapping is found.
	 */
	private boolean fallback = true;

	/**
	 * The argument handler delegate this handler wraps.
	 */
	private FlowExecutorArgumentHandler argumentHandler;

	/**
	 * Default constructor for bean style usage.
	 * @see #setArgumentHandler(FlowExecutorArgumentHandler)
	 * @see #setMappings(Properties)
	 * @see #setFallback(boolean)
	 */
	public FlowIdMappingArgumentHandlerWrapper() {
	}

	/**
	 * Returns the wrapped argument handler.
	 */
	public FlowExecutorArgumentHandler getArgumentHandler() {
		return argumentHandler;
	}

	/**
	 * Set the wrapped argument handler.
	 */
	public void setArgumentHandler(FlowExecutorArgumentHandler argumentHandler) {
		this.argumentHandler = argumentHandler;
	}

	/**
	 * Returns the public-to-private flow id mappings in use.
	 */
	protected Properties getMappings() {
		return mappings;
	}

	/**
	 * Set the mappings between client-submitted flow identifiers and internal flow identifiers. Overwrites any previous
	 * mappings.
	 * @param mappings the public to private flow id mappings
	 */
	public void setMappings(Properties mappings) {
		for (Enumeration publicFlowIds = mappings.propertyNames(); publicFlowIds.hasMoreElements();) {
			String publicId = (String) publicFlowIds.nextElement();
			String privateId = mappings.getProperty(publicId);
			addMapping(publicId, privateId);
		}
	}

	/**
	 * Add a flow id mapping, overwriting any previous mapping for the same flow ids.
	 * @param publicFlowId how the flow will be identified publically (to web clients)
	 * @param privateFlowId how the flow is identified internally (in the flow definition registry)
	 */
	public void addMapping(String publicFlowId, String privateFlowId) {
		mappings.setProperty(publicFlowId, privateFlowId);
		reverseMappings.setProperty(privateFlowId, publicFlowId);
	}

	/**
	 * Should we fall back to the flow id extracted by the wrapped argument handler if no mapping is defined for a flow
	 * id? Default is true.
	 */
	public boolean isFallback() {
		return fallback;
	}

	/**
	 * Set whether or not to fall back on the flow id extracted by the wrapped argument handler if no mapping is defined
	 * for a flow id. Default is true. When false an exception is thrown when there is a mapping failure.
	 */
	public void setFallback(boolean fallback) {
		this.fallback = fallback;
	}

	public boolean isFlowIdPresent(ExternalContext context) {
		if (argumentHandler.isFlowIdPresent(context)) {
			return fallback || mappings.containsKey(argumentHandler.extractFlowId(context));
		}
		else {
			return false;
		}
	}

	public String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String publicFlowId = argumentHandler.extractFlowId(context);
		String flowId = mappings.getProperty(publicFlowId);
		if (!StringUtils.hasText(flowId)) {
			if (fallback) {
				flowId = publicFlowId;
			}
			else {
				throw new FlowExecutorArgumentExtractionException("Unable to extract flow definition id: "
						+ "no mapping was defined for '" + publicFlowId + "'");
			}
		}
		return flowId;
	}

	public boolean isFlowExecutionKeyPresent(ExternalContext context) {
		return argumentHandler.isFlowExecutionKeyPresent(context);
	}

	public String extractFlowExecutionKey(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		return argumentHandler.extractFlowExecutionKey(context);
	}

	public boolean isEventIdPresent(ExternalContext context) {
		return argumentHandler.isEventIdPresent(context);
	}

	public String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		return argumentHandler.extractEventId(context);
	}

	public String createFlowDefinitionUrl(FlowDefinitionRedirect flowDefinitionRedirect, ExternalContext context) {
		// do reverse mapping
		String publicFlowId = reverseMappings.getProperty(flowDefinitionRedirect.getFlowDefinitionId());
		if (!StringUtils.hasText(publicFlowId)) {
			if (fallback) {
				publicFlowId = flowDefinitionRedirect.getFlowDefinitionId();
			}
			else {
				// this is a mapping problem
				throw new IllegalArgumentException("Unable to create a flow definition URL for '"
						+ flowDefinitionRedirect + "': no reverse mapping was defined for flow id '"
						+ flowDefinitionRedirect.getFlowDefinitionId() + "'");
			}
		}
		flowDefinitionRedirect = new FlowDefinitionRedirect(publicFlowId, flowDefinitionRedirect.getExecutionInput());
		return argumentHandler.createFlowDefinitionUrl(flowDefinitionRedirect, context);
	}

	public String createFlowExecutionUrl(String flowExecutionKey, FlowExecutionContext flowExecution,
			ExternalContext context) {
		return argumentHandler.createFlowExecutionUrl(flowExecutionKey, flowExecution, context);
	}

	public String createExternalUrl(ExternalRedirect redirect, String flowExecutionKey, ExternalContext context) {
		return argumentHandler.createExternalUrl(redirect, flowExecutionKey, context);
	}
}