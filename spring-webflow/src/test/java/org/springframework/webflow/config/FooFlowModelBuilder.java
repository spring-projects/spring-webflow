/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.config;

import java.util.LinkedList;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.engine.model.builder.ResourceBackedFlowModelBuilder;
import org.springframework.webflow.engine.model.registry.FlowModelLocator;

/**
 * Mock implementation of a {@link ResourceBackedFlowModelBuilder} that simply creates a new flow.
 * @author Paul Wilson
 */
public class FooFlowModelBuilder implements ResourceBackedFlowModelBuilder {

	private Resource resource;

	public void init() throws FlowModelBuilderException {
	}

	public void build() throws FlowModelBuilderException {
	}

	public FlowModel getFlowModel() throws FlowModelBuilderException {
		FlowModel model = new FlowModel();
		LinkedList<AbstractStateModel> states = new LinkedList<AbstractStateModel>();
		states.add(new EndStateModel("end"));
		model.setStates(states);
		return model;
	}

	public void dispose() throws FlowModelBuilderException {
	}

	public Resource getFlowModelResource() {
		return resource;
	}

	public boolean hasFlowModelResourceChanged() {
		return false;
	}

	public void setFlowModelLocator(FlowModelLocator modelLocator) {
	}

	public void setFlowResource(Resource resource) {
		this.resource = resource;
	}

}