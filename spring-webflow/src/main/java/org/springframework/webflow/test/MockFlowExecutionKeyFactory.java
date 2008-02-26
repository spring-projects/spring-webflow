package org.springframework.webflow.test;

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;

/**
 * Trivial flow execution key factory implementation that returns a mock flow execution key each time. The mock key
 * returned is unique: its value is the result of incrementing a sequence managed in static memory.
 * 
 * @author Keith Donald
 */
public class MockFlowExecutionKeyFactory implements FlowExecutionKeyFactory {
	public FlowExecutionKey getKey(FlowExecution execution) {
		return new GeneratedFlowExecutionKey();
	}
}
