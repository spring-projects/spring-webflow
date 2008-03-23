/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.engine.model.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;

/**
 * A flow model holder that can detect changes on an underlying flow model resource and refresh that resource
 * automatically.
 * <p>
 * This class is thread-safe.
 * <p>
 * Note that this {@link FlowModel} holder uses a {@link FlowModelBuilder}.
 * 
 * @see FlowModel
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class DefaultFlowModelHolder implements FlowModelHolder {

	private static final Log logger = LogFactory.getLog(DefaultFlowModelHolder.class);

	/**
	 * The flow model assembled by this assembler.
	 */
	private FlowModel flowModel;

	/**
	 * The flow mode identifier
	 */
	private String flowModelId;

	/**
	 * The flow model builder.
	 */
	private FlowModelBuilder flowModelBuilder;

	/**
	 * A last modified date for the backing flow definition resource, used to support automatic reassembly on resource
	 * change.
	 */
	private long lastModified;

	/**
	 * Creates a new refreshable flow model holder that uses the configured assembler (GOF director) to drive flow
	 * assembly, on initial use and on any resource change or refresh.
	 * @param flowModelBuilder the flow model builder to use
	 * @param flowModelId the identifier of the flow model
	 */
	public DefaultFlowModelHolder(FlowModelBuilder flowModelBuilder, String flowModelId) {
		this.flowModelBuilder = flowModelBuilder;
		this.flowModelId = flowModelId;
	}

	public String getFlowModelId() {
		return flowModelId;
	}

	public synchronized FlowModel getFlowModel() throws FlowModelConstructionException {
		if (flowModel == null) {
			assembleFlowModel();
		} else {
			if (flowModelBuilder.hasFlowModelChanged()) {
				assembleFlowModel();
			}
		}
		return flowModel;
	}

	public Resource getFlowModelResource() {
		return flowModelBuilder.getFlowModelResource();
	}

	public boolean hasFlowModelChanged() {
		return flowModelBuilder.hasFlowModelChanged();
	}

	public synchronized void refresh() throws FlowModelConstructionException {
		assembleFlowModel();
	}

	// internal helpers

	private void assembleFlowModel() throws FlowModelConstructionException {
		try {
			flowModelBuilder.init();
			flowModelBuilder.build();
			flowModel = flowModelBuilder.getFlowModel();
		} catch (FlowModelBuilderException e) {
			throw new FlowModelConstructionException(flowModelId, e);
		} finally {
			flowModelBuilder.dispose();
		}
	}

	public String toString() {
		return "'" + getFlowModelId() + "'";
	}

}