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
package org.springframework.webflow.action;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action that sends a flow definition redirect when executed.
 * 
 * @author Keith Donald
 */
public class FlowDefinitionRedirectAction extends AbstractAction {

	private Expression expression;

	/**
	 * Creates a new flow definition redirect action.
	 * @param expression a encoded flow redirect expression
	 */
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