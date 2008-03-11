package org.springframework.webflow.engine;

import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.View;

/**
 * Thrown if a IO exception was thrown during view rendering.
 * 
 * @author Keith Donald
 */
public class ViewRenderingException extends FlowExecutionException {

	/**
	 * Create a new action execution exception.
	 * @param flowId the current flow
	 * @param stateId the current state (may be null)
	 * @param view the view that generated an unrecoverable exception
	 * @param cause the underlying cause
	 */
	public ViewRenderingException(String flowId, String stateId, View view, Throwable cause) {
		super(flowId, stateId, "Exception thrown rendering " + view + " in state '" + stateId + "' of flow '" + flowId
				+ "'", cause);
	}
}