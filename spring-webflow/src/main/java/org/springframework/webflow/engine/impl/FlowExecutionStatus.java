package org.springframework.webflow.engine.impl;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * A enum used internally by {@link FlowExecutionImpl} to track its status. Flow Executions initially start out in
 * NOT_STARTED. After start is called, they are ACTIVE. After ending, their status is updated to ENDED.
 */
public class FlowExecutionStatus extends StaticLabeledEnum {

	/**
	 * The flow execution has not yet started.
	 */
	public static final FlowExecutionStatus NOT_STARTED = new FlowExecutionStatus(0, "Not Started");

	/**
	 * The flow execution has started and a session is active.
	 */
	public static final FlowExecutionStatus ACTIVE = new FlowExecutionStatus(1, "Active");

	/**
	 * The flow execution has ended.
	 */
	public static final FlowExecutionStatus ENDED = new FlowExecutionStatus(2, "Ended");

	private FlowExecutionStatus(int code, String label) {
		super(code, label);
	}

}
