package org.springframework.webflow.engine.impl;

/**
 * A enum used internally by {@link FlowExecutionImpl} to track its status. Flow Executions initially start out in
 * NOT_STARTED. After start is called, they are ACTIVE. After ending, their status is updated to ENDED.
 */
public enum FlowExecutionStatus {

	/**
	 * The flow execution has not yet started.
	 */
	NOT_STARTED,

	/**
	 * The flow execution has started and a session is active.
	 */
	ACTIVE,

	/**
	 * The flow execution has ended.
	 */
	ENDED

}
