package org.springframework.webflow.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class FlowDefinitionRedirectAction extends AbstractAction {
	private Expression flowId;
	private Map input;

	public FlowDefinitionRedirectAction(Expression flowId, Map input) {
		Assert.notNull(flowId, "The flow id to redirect to is required");
		this.flowId = flowId;
		this.input = input;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String flowId = (String) this.flowId.getValue(context);
		AttributeMap input = evaluateInput(context);
		context.getExternalContext().requestFlowDefinitionRedirect(flowId, input);
		return success();
	}

	private AttributeMap evaluateInput(RequestContext context) {
		if (this.input == null) {
			return null;
		} else {
			Map input = new HashMap();
			for (Iterator it = this.input.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Expression name = (Expression) entry.getKey();
				Expression value = (Expression) entry.getValue();
				String paramName = (String) name.getValue(context);
				Object paramValue = value.getValue(context);
				input.put(paramName, paramValue);
			}
			return new LocalAttributeMap(input);
		}
	}

	public static FlowDefinitionRedirectAction create(String encodedFlowRedirect) {
		// TODO
		return null;
	}
}
