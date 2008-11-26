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
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * An action that sets a special attribute that views use to render partial views called "fragments", instead of the
 * entire view.
 * 
 * @author Keith Donald
 */
public class RenderAction extends AbstractAction {

	/**
	 * The expression for setting the scoped attribute value.
	 */
	private Expression[] fragmentExpressions;

	/**
	 * Creates a new render action.
	 * @param fragmentExpressions the set of expressions to resolve the view fragments to render
	 */
	public RenderAction(Expression[] fragmentExpressions) {
		if (fragmentExpressions == null || fragmentExpressions.length == 0) {
			throw new IllegalArgumentException(
					"You must provide at least one fragment expression to this render action");
		}
		this.fragmentExpressions = fragmentExpressions;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String[] fragments = new String[fragmentExpressions.length];
		for (int i = 0; i < fragmentExpressions.length; i++) {
			Expression exp = fragmentExpressions[i];
			fragments[i] = (String) exp.getValue(context);
		}
		context.getFlashScope().put(View.RENDER_FRAGMENTS_ATTRIBUTE, fragments);
		return success();
	}

	public String toString() {
		return new ToStringCreator(this).append("fragments", fragmentExpressions).toString();
	}
}