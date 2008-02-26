package org.springframework.webflow.action;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class ExternalRedirectAction extends AbstractAction {

	private Expression resourceUri;

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
