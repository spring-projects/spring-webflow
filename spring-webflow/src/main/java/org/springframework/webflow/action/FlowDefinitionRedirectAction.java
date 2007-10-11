package org.springframework.webflow.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.webflow.context.FlowDefinitionRequestInfo;
import org.springframework.webflow.context.RequestPath;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class FlowDefinitionRedirectAction extends AbstractAction {
	private Expression flowId;
	private Expression[] requestElements;
	private Map requestParameters;

	public FlowDefinitionRedirectAction(Expression flowId, Expression[] requestElements, Map requestParameters) {
		Assert.notNull(flowId, "The flow id to redirect to is required");
		this.flowId = flowId;
		this.requestElements = requestElements;
		this.requestParameters = requestParameters;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String flowId = (String) this.flowId.getValue(context);
		RequestPath requestPath = evaluateRequestPath(context);
		ParameterMap requestParameters = evaluateRequestParameters(context);
		context.getExternalContext().sendFlowDefinitionRedirect(
				new FlowDefinitionRequestInfo(flowId, requestPath, requestParameters, null));
		return success();
	}

	private RequestPath evaluateRequestPath(RequestContext context) {
		if (this.requestElements == null || this.requestElements.length == 0) {
			return null;
		}
		String[] requestElements = new String[this.requestElements.length];
		for (int i = 0; i < this.requestElements.length; i++) {
			Expression element = this.requestElements[i];
			requestElements[i] = (String) element.getValue(context);
		}
		return RequestPath.valueOf(requestElements);
	}

	private ParameterMap evaluateRequestParameters(RequestContext context) {
		if (this.requestParameters == null) {
			return null;
		} else {
			Map requestParameters = new HashMap();
			for (Iterator it = this.requestParameters.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Expression name = (Expression) entry.getKey();
				Expression value = (Expression) entry.getValue();
				String paramName = (String) name.getValue(context);
				String paramValue = (String) value.getValue(context);
				requestParameters.put(paramName, paramValue);
			}
			return new LocalParameterMap(requestParameters);
		}
	}

	public static FlowDefinitionRedirectAction create(String encodedFlowRedirect) {
		// TODO
		return null;
	}
}
