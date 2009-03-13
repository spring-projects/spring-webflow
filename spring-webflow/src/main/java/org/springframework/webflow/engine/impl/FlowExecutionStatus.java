package org.springframework.webflow.engine.impl;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * A enum used internally by {@link FlowExecutionImpl} to track its status. Flow Executions initially start out in
 * NOT_STARTED. After start is called, they are STARTING. Once the root session is activated, they have STARTED. When
 * the execution begins to end, their status is ENDING. After ending, their status is updated to ENDED.
 */
public class FlowExecutionStatus extends StaticLabeledEnum {

	/**
	 * The flow execution has not yet started.
	 */
	public static final FlowExecutionStatus NOT_STARTED = new FlowExecutionStatus(0, "Not Started");

	/**
	 * The flow execution is in the process of starting; the root session has not yet been activated.
	 */
	public static final FlowExecutionStatus STARTING = new FlowExecutionStatus(1, "Starting");

	/**
	 * The flow execution has started and a session is active.
	 */
	public static final FlowExecutionStatus STARTED = new FlowExecutionStatus(2, "Started");

	/**
	 * The flow execution is ending.
	 */
	public static final FlowExecutionStatus ENDING = new FlowExecutionStatus(3, "Ending");

	/**
	 * The flow execution has ended.
	 */
	public static final FlowExecutionStatus ENDED = new FlowExecutionStatus(4, "Ended");

	private FlowExecutionStatus(int code, String label) {
		super(code, label);
	}

}
