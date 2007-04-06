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
 * Flow executor argument handler that wraps another argument handler
 * and applies a flow id mapping. This can be used to avoid literal flow ids in
 * URLs that launch flows.
 * <p>
 * For example, when used in combination with {@link RequestParameterFlowExecutorArgumentHandler}
 * the url <code>http://localhost/springair/reservation/booking.html</code> would
 * launch a new execution of the <code>booking-flow</code> flow, assuming a context path of
 * <code>/springair</code>, a servlet mapping of <code>/reservation/*</code> and a flow id
 * mapping of <code>booking-&gt;booking-flow</code>.
 * 
 * @see RequestParameterFlowExecutorArgumentHandler
 * @see RequestPathFlowExecutorArgumentHandler
 * 
 * @author Andrej Zachar
 * @author Erwin Vervaet
 */
public class FlowIdMappingArgumentHandlerWrapper extends FlowExecutorArgumentHandler {
	
	private FlowExecutorArgumentHandler argumentHandler;
	private Properties mappings = new Properties();
	private Properties reverseMappings = new Properties();
	private boolean fallback = true;
	
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
	 * Returns the flow id mappings in use.
	 */
	protected Properties getMappings() {
		return mappings;
	}

	/**
	 * Set the flow id mappings to use, overwriting any previous mappings.
	 */
	public void setMappings(Properties mappings) {
		for (Enumeration fromFlowIds = mappings.propertyNames(); fromFlowIds.hasMoreElements(); ) {
			String fromFlowId = (String)fromFlowIds.nextElement();
			String toFlowId = mappings.getProperty(fromFlowId);
			addMapping(fromFlowId, toFlowId);
		}
	}
	
	/**
	 * Add a flow id mapping, overwriting any previous mapping for the same
	 * flow ids.
	 */
	public void addMapping(String fromFlowId, String toFlowId) {
		mappings.setProperty(fromFlowId, toFlowId);
		reverseMappings.setProperty(toFlowId, fromFlowId);
	}
	
	/**
	 * Should we fall back to the flow id extracted by the wrapped argument
	 * handler if no mapping is defined for a flow id? Default is true.
	 */
	public boolean isFallback() {
		return fallback;
	}
	
	/**
	 * Set whether or not to fall back on the flow id extracted by the
	 * wrapped argument handler if no mapping is defined for a flow id.
	 * Default is true. When false an exception is thrown when there is
	 * a mapping failure.
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
		String originalFlowId = argumentHandler.extractFlowId(context);
		String flowId = mappings.getProperty(originalFlowId);
		if (!StringUtils.hasText(flowId)) {
			if (fallback) {
				flowId = originalFlowId;
			}
			else {
				throw new FlowExecutorArgumentExtractionException(
						"Unable to extract flow definition id: no mapping was defined for flow id '" +
						originalFlowId + "'");
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
		String flowId = reverseMappings.getProperty(flowDefinitionRedirect.getFlowDefinitionId());
		if (!StringUtils.hasText(flowId)) {
			if (fallback) {
				flowId = flowDefinitionRedirect.getFlowDefinitionId();
			}
			else {
				// this is a mapping problem
				throw new IllegalArgumentException(
						"Unable to create a flow definition URL for '" + flowDefinitionRedirect + "': " + 
						"no reverse mapping was defined for flow id '" + flowDefinitionRedirect.getFlowDefinitionId() + "'");
			}
		}
		flowDefinitionRedirect = new FlowDefinitionRedirect(flowId, flowDefinitionRedirect.getExecutionInput());
		return argumentHandler.createFlowDefinitionUrl(flowDefinitionRedirect, context);
	}

	public String createFlowExecutionUrl(String flowExecutionKey,
			FlowExecutionContext flowExecution, ExternalContext context) {
		return argumentHandler.createFlowExecutionUrl(flowExecutionKey, flowExecution, context);
	}

	public String createExternalUrl(ExternalRedirect redirect, String flowExecutionKey, ExternalContext context) {
		return argumentHandler.createExternalUrl(redirect, flowExecutionKey, context);
	}
}
