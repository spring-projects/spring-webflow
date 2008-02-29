package org.springframework.webflow.engine;

import org.springframework.webflow.execution.RequestContext;

public interface VariableValueFactory {
	public Object createVariableValue(RequestContext context);

	public Object restoreReferences(Object value, RequestContext context);
}
