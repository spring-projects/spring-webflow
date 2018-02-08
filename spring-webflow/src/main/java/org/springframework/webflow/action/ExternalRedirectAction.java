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
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action that sends an external redirect when executed.
 * 
 * @author Keith Donald
 */
public class ExternalRedirectAction extends AbstractAction {

	private Expression resourceUri;

	/**
	 * Creates a new external redirect action
	 * @param resourceUri an expression for the resource Uri to redirect to
	 */
	public ExternalRedirectAction(Expression resourceUri) {
		Assert.notNull(resourceUri, "The URI of the resource to redirect to is required");
		this.resourceUri = resourceUri;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String resourceUri = (String) this.resourceUri.getValue(context);
		context.getExternalContext().requestExternalRedirect(resourceUri);
		return success();
	}

}
