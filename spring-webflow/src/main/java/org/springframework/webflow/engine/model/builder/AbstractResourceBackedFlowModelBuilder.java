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
package org.springframework.webflow.engine.model.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.registry.FlowModelLocator;
import org.springframework.webflow.engine.model.registry.NoSuchFlowModelException;

/**
 * Abstract {@link FlowModelBuilder flow model builder} for the construction of flow models that are based upon the
 * {@link Resource spring resource abstraction}.
 * 
 * <p>
 * This template abstracts the responsibility of managing the {@link Resource resource} modification timestamps, as well
 * as the merging of {@link FlowModel models}, allowing the implementation to focus upon the transformation of the
 * {@link Resource resource} into the base {@link FlowModel model}.
 * 
 * @author Paul Wilson
 * @see Resource
 * @see FlowModelLocator
 * @see FlowModel
 * @since 2.4.0
 */
public abstract class AbstractResourceBackedFlowModelBuilder implements ResourceBackedFlowModelBuilder {

	private long lastModifiedTimestamp;
	private Resource resource;
	private FlowModelLocator modelLocator;
	private FlowModel flowModel;

	/**
	 * Initialize this builder from the provided resource.
	 * @throws FlowModelBuilderException an exception occurred building the flow
	 */
	protected abstract void doInit(Resource resource) throws FlowModelBuilderException;

	/**
	 * Create a {@link FlowModel} instance from the resource.
	 * <p>
	 * Implementations are <em>not</em> required to merge flows from parent definitions
	 * @return the constructed flow model
	 * @throws FlowModelBuilderException if the resource cannot be constructed (e.g. malformed flow syntax)
	 */
	protected abstract FlowModel parseFlow() throws FlowModelBuilderException;

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow construction process should start with another
	 * call to the {@link #init()} method.
	 * @throws FlowModelBuilderException an exception occurred disposing this flow
	 */
	protected abstract void doDispose() throws FlowModelBuilderException;

	/**
	 * Configure the {@link FlowModelLocator flow model locator}
	 */
	public void setFlowModelLocator(FlowModelLocator modelLocator) {
		this.modelLocator = modelLocator;
	}

	/**
	 * Sets the {@link Resource resource} from which the model should be constructed
	 */
	public void setFlowResource(Resource resource) {
		Assert.notNull(resource, "A resource cannot be null. To clear the resource use #dispose.");
		this.resource = resource;
	}

	public void init() throws FlowModelBuilderException {
		doInit(resource);
		initLastModifiedTimestamp();
	}

	public void build() throws FlowModelBuilderException {
		flowModel = parseFlow();
		mergeFlows(flowModel);
		mergeStates(flowModel);
	}

	public FlowModel getFlowModel() throws FlowModelBuilderException {
		if (flowModel == null) {
			throw new FlowModelBuilderException(
					"The FlowModel must be built first -- called init() and build() before calling getFlowModel()");
		}
		return flowModel;
	}

	public void dispose() throws FlowModelBuilderException {
		flowModel = null;
		doDispose();
	}

	public Resource getFlowModelResource() {
		return resource;
	}

	public boolean hasFlowModelResourceChanged() {
		if (lastModifiedTimestamp == -1) {
			return false;
		}
		try {
			long lastModified = resource.lastModified();
			if (lastModified > lastModifiedTimestamp) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	private void initLastModifiedTimestamp() {
		try {
			lastModifiedTimestamp = resource.lastModified();
		} catch (IOException e) {
			lastModifiedTimestamp = -1;
		}
	}

	private void mergeFlows(FlowModel flowModel) {
		if (flowModel.getParent() != null) {
			List<String> parents = Arrays.asList(StringUtils.trimArrayElements(flowModel.getParent().split(",")));
			for (String parentFlowId : parents) {
				if (StringUtils.hasText(parentFlowId)) {
					try {
						flowModel.merge(modelLocator.getFlowModel(parentFlowId));
					} catch (NoSuchFlowModelException e) {
						throw new FlowModelBuilderException("Unable to find flow '" + parentFlowId
								+ "' to inherit from", e);
					}
				}
			}
		}
	}

	private void mergeStates(FlowModel flowModel) {
		if (flowModel.getStates() == null) {
			return;
		}
		for (AbstractStateModel childState : flowModel.getStates()) {
			String parent = childState.getParent();
			if (childState.getParent() != null) {
				String flowId;
				String stateId;
				AbstractStateModel parentState = null;
				int hashIndex = parent.indexOf("#");
				if (hashIndex == -1) {
					throw new FlowModelBuilderException("Invalid parent syntax '" + parent
							+ "', should take form 'flowId#stateId'");
				}
				flowId = parent.substring(0, hashIndex).trim();
				stateId = parent.substring(hashIndex + 1).trim();
				try {
					if (StringUtils.hasText(flowId)) {
						parentState = modelLocator.getFlowModel(flowId).getStateById(stateId);
					} else {
						parentState = flowModel.getStateById(stateId);
					}
					if (parentState == null) {
						throw new FlowModelBuilderException("Unable to find state '" + stateId + "' in flow '" + flowId
								+ "'");
					}
					childState.merge(parentState);
				} catch (NoSuchFlowModelException e) {
					throw new FlowModelBuilderException("Unable to find flow '" + flowId + "' to inherit from", e);
				} catch (ClassCastException e) {
					throw new FlowModelBuilderException("Parent state type '" + parentState.getClass().getName()
							+ "' cannot be merged with state type '" + childState.getClass().getName() + "'", e);
				}
			}
		}
	}
}