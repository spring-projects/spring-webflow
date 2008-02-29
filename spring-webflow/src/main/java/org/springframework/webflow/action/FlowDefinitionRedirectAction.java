package org.springframework.webflow.action;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class FlowDefinitionRedirectAction extends AbstractAction {
	private Expression expression;

	public FlowDefinitionRedirectAction(Expression expression) {
		Assert.notNull(expression, "The flow definition redirect expression is required");
		this.expression = expression;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String encodedRedirect = (String) expression.getValue(context);
		if (encodedRedirect == null) {
			throw new IllegalStateException(
					"Flow definition redirect expression evaluated to [null], the expression was " + expression);
		}
		// the encoded FlowDefinitionRedirect should look something like
		// "flowDefinitionId?param0=value0&param1=value1"
		// now parse that and build a corresponding view selection
		int index = encodedRedirect.indexOf('?');
		String flowDefinitionId;
		LocalAttributeMap executionInput = null;
		if (index != -1) {
			flowDefinitionId = encodedRedirect.substring(0, index);
			String[] parameters = StringUtils.delimitedListToStringArray(encodedRedirect.substring(index + 1), "&");
			executionInput = new LocalAttributeMap(parameters.length, 1);
			for (int i = 0; i < parameters.length; i++) {
				String nameAndValue = parameters[i];
				index = nameAndValue.indexOf('=');
				if (index != -1) {
					executionInput.put(nameAndValue.substring(0, index), nameAndValue.substring(index + 1));
				} else {
					executionInput.put(nameAndValue, "");
				}
			}
		} else {
			flowDefinitionId = encodedRedirect;
		}
		if (!StringUtils.hasText(flowDefinitionId)) {
			// equivalent to restart
			flowDefinitionId = context.getFlowExecutionContext().getDefinition().getId();
		}
		context.getExternalContext().requestFlowDefinitionRedirect(flowDefinitionId, executionInput);
		return success();
	}
}