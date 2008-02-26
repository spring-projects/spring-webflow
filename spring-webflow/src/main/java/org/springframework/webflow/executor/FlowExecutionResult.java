package org.springframework.webflow.executor;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;

public class FlowExecutionResult {
	private String flowId;
	private String flowExecutionKey;
	private String outcome;
	private AttributeMap output;

	private FlowExecutionResult(String flowId, String flowExecutionKey, String outcome, AttributeMap output) {
		this.flowId = flowId;
		this.flowExecutionKey = flowExecutionKey;
		this.outcome = outcome;
		this.output = output;
	}

	public static FlowExecutionResult createPausedResult(String flowId, String flowExecutionKey) {
		return new FlowExecutionResult(flowId, flowExecutionKey, null, null);
	}

	public static FlowExecutionResult createEndedResult(String flowId, Event outcome) {
		return new FlowExecutionResult(flowId, null, outcome.getId(), outcome.getAttributes());
	}

	public String getFlowId() {
		return flowId;
	}

	public boolean paused() {
		return flowExecutionKey != null;
	}

	public String getPausedKey() {
		return flowExecutionKey;
	}

	public boolean ended() {
		return flowExecutionKey == null;
	}

	public String getEndedOutcome() {
		return outcome;
	}

	public AttributeMap getEndedOutput() {
		return output;
	}

}
