package org.springframework.webflow.mvc.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;

public class FlowHandlerMapping extends AbstractHandlerMapping {

	private FlowDefinitionRegistry flowRegistry;

	protected void initServletContext(ServletContext servletContext) {
		if (flowRegistry == null) {
			flowRegistry = (FlowDefinitionRegistry) getApplicationContext().getBean("flowRegistry",
					FlowDefinitionRegistry.class);
		}
	}

	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		String flowId = request.getPathInfo().substring(1);
		if (getApplicationContext().containsBean(flowId)) {
			Object handler = getApplicationContext().getBean(flowId);
			if (handler instanceof FlowHandler) {
				return handler;
			}
		}
		if (flowRegistry.containsFlowDefinition(flowId)) {
			return new DefaultFlowHandler(flowId);
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
