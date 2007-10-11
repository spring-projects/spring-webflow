package org.springframework.faces.ui.resource;

import org.easymock.IArgumentMatcher;
import org.springframework.webflow.context.FlowDefinitionRequestInfo;

class RequestInfoMatcher implements IArgumentMatcher {

	private FlowDefinitionRequestInfo expected;

	public RequestInfoMatcher(FlowDefinitionRequestInfo expected) {
		this.expected = expected;
	}

	public void appendTo(StringBuffer buffer) {
		buffer.append("/" + expected.getFlowDefinitionId() + expected.getRequestPath());
	}

	public boolean matches(Object actual) {
		FlowDefinitionRequestInfo actualRequest = (FlowDefinitionRequestInfo) actual;
		return expected.getFlowDefinitionId().equals(actualRequest.getFlowDefinitionId())
				&& expected.getRequestPath().equals(actualRequest.getRequestPath());
	}
}
