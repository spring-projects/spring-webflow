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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.webflow.context.servlet.DefaultFlowUrlHandler;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;

/**
 * Implementation of {@link org.springframework.web.servlet.HandlerMapping} that follows a simple convention for
 * creating URL path mappings from the <i>ids</i> of registered {@link FlowDefinition flow definitions}.
 * 
 * This implementation returns a FlowHandler that invokes a flow if the current request path matches the id of a flow in
 * the configured {@link FlowDefinitionRegistry}. Alternatively, a custom {@link FlowHandler} may also be registered
 * with in containing ApplicationContext with that id and it will be returned. This allows for more control over the
 * invocation of a flow from Spring MVC environment.
 * 
 * Null is returned in the case of no flow id match, allowing the next handler mapping in the chain to execute.
 * 
 * @author Keith Donald
 */
public class FlowHandlerMapping extends AbstractHandlerMapping {

	private static final Log logger = LogFactory.getLog(FlowHandlerMapping.class);

	private FlowDefinitionRegistry flowRegistry;

	private FlowUrlHandler flowUrlHandler;

	/**
	 * Returns the registry of flows to query when this mapping is tested.
	 * @return the flow definition registry
	 */
	public FlowDefinitionRegistry getFlowRegistry() {
		return flowRegistry;
	}

	/**
	 * Sets the registry of flows to query when this mapping is tested. Optional. If not set, this handler mapping will
	 * look in the containing application context for a bean with id <code>flowRegistry</code>.
	 * @param flowRegistry the flow definition registry
	 */
	public void setFlowRegistry(FlowDefinitionRegistry flowRegistry) {
		this.flowRegistry = flowRegistry;
	}

	/**
	 * Returns the configured flow url handler.
	 */
	public FlowUrlHandler getFlowUrlHandler() {
		return flowUrlHandler;
	}

	/**
	 * Sets the flow URL handler, which allows customization for how the flow id is determined for each request tested
	 * by this mapping. Defaults to a {@link DefaultFlowUrlHandler}.
	 * @param flowUrlHandler the flow URL handler
	 */
	public void setFlowUrlHandler(FlowUrlHandler flowUrlHandler) {
		this.flowUrlHandler = flowUrlHandler;
	}

	protected void initServletContext(ServletContext servletContext) {
		Assert.notNull(flowRegistry, "The FlowRegistry to query when mapping requests is required");
		if (flowUrlHandler == null) {
			flowUrlHandler = new DefaultFlowUrlHandler();
		}
	}

	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		String flowId = flowUrlHandler.getFlowId(request);
		if (getApplicationContext().containsBean(flowId)) {
			Object handler = getApplicationContext().getBean(flowId);
			if (handler instanceof FlowHandler) {
				if (logger.isDebugEnabled()) {
					logger.debug("Mapping request with URI '" + request.getRequestURI() + "' to flow with id '"
							+ flowId + "'; custom FlowHandler " + handler + " will manage flow execution");
				}
				return handler;
			}
		}
		if (flowRegistry.containsFlowDefinition(flowId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Mapping request with URI '" + request.getRequestURI() + "' to flow with id '" + flowId
						+ "'");
			}
			return new DefaultFlowHandler(flowId);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("No flow mapping found for request with URI '" + request.getRequestURI() + "'");
		}
		return null;
	}

	private static class DefaultFlowHandler extends AbstractFlowHandler {

		private String flowId;

		public DefaultFlowHandler(String flowId) {
			this.flowId = flowId;
		}

		public String getFlowId() {
			return flowId;
		}
	}

}