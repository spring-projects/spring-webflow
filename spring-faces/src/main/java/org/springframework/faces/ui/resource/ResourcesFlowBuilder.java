package org.springframework.faces.ui.resource;

import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;

/**
 * Builder for generating the "resources" flow which is responsible for serving static resources from the classpath.
 * @author Jeremy Grelle
 */
public class ResourcesFlowBuilder extends AbstractFlowBuilder {
	public void buildStates() throws FlowBuilderException {
		EndState renderResource = new EndState(getFlow(), "renderResource");
		renderResource.setFinalResponseAction(new RenderResourceAction());
	}
}
