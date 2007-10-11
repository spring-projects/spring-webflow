package org.springframework.webflow.config;

import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;

public class FooFlowBuilder extends AbstractFlowBuilder {

	public void buildStates() throws FlowBuilderException {
		new EndState(getFlow(), "finish");
	}

}
