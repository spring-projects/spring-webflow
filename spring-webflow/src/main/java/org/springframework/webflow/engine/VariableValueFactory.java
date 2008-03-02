package org.springframework.webflow.engine;

import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy that encapsulates the source of a flow variable value.
 * @author Keith Donald
 */
public interface VariableValueFactory {

	/**
	 * Creates the variable's initial value.
	 * @param context the currently executing flow request
	 * @return the value
	 */
	public Object createInitialValue(RequestContext context);

	/**
	 * Restore any references the variable's value needs to other objects. Such references may have been lost during
	 * deserialization, for example, and need to be restored.
	 * @param value the current variable value
	 * @param context the currently executing flow request
	 */
	public void restoreReferences(Object value, RequestContext context);
}
