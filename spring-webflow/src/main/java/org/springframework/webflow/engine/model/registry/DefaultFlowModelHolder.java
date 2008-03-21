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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.util.ResourceHolder;

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
	private FlowModelBuilder builder;

	/**
	 * A last modified date for the backing flow definition resource, used to support automatic reassembly on resource
	 * change.
	 */
	private long lastModified;

	/**
	 * Creates a new refreshable flow model holder that uses the configured assembler (GOF director) to drive flow
	 * assembly, on initial use and on any resource change or refresh.
	 * @param builder the flow model builder to use
	 * @param flowModelId the identifier of the flow model
	 */
	public DefaultFlowModelHolder(FlowModelBuilder builder, String flowModelId) {
		this.builder = builder;
		this.flowModelId = flowModelId;
	}

	/**
	 * Creates a new static flow model holder
	 * @param flowModel the flow model to hold
	 * @param flowModelId the identifier of the flow model
	 */
	public DefaultFlowModelHolder(FlowModel flowModel, String flowModelId) {
		this.flowModel = flowModel;
		this.flowModelId = flowModelId;
	}

	public String getFlowModelId() {
		return flowModelId;
	}

	public synchronized FlowModel getFlowModel() throws FlowModelConstructionException {
		if (flowModel == null) {
			lastModified = calculateLastModified();
			logger.debug("Assembling the flow model for the first time");
			assembleFlow();
		} else {
			refreshIfChanged();
		}
		return flowModel;
	}

	public synchronized void refresh() throws FlowModelConstructionException {
		assembleFlow();
	}

	// internal helpers

	/**
	 * Helper that retrieves the last modified date by querying the backing flow resource.
	 * @return the last modified date, or 0L if it could not be retrieved
	 */
	private long calculateLastModified() {
		if (getFlowModelBuilder() instanceof ResourceHolder) {
			Resource resource = ((ResourceHolder) getFlowModelBuilder()).getResource();
			try {
				long lastModified = resource.getFile().lastModified();
				if (logger.isDebugEnabled()) {
					logger.debug("Flow definition [" + resource + "] was last modified on " + lastModified);
				}
				return lastModified;
			} catch (IOException e) {
				// ignore, last modified checks not supported
			}
		}
		return 0L;
	}

	/**
	 * Assemble the held flow definition, delegating to the configured FlowAssembler (director).
	 */
	private void assembleFlow() throws FlowModelConstructionException {
		try {
			builder.init();
			builder.build();
			flowModel = builder.getFlowModel();
		} catch (FlowModelBuilderException e) {
			throw new FlowModelConstructionException(flowModelId, e);
		} finally {
			builder.dispose();
		}
	}

	/**
	 * Reassemble the flow if its underlying resource has changed.
	 */
	private void refreshIfChanged() {
		long calculatedLastModified = calculateLastModified();
		if (calculatedLastModified > lastModified) {
			if (logger.isDebugEnabled()) {
				logger.debug("Refreshing flow definition [" + flowModelId + "]");
			}
			assembleFlow();
			lastModified = calculatedLastModified;
		}
	}

	/**
	 * Returns the flow builder that actually builds the Flow definition.
	 */
	private FlowModelBuilder getFlowModelBuilder() {
		return builder;
	}

	public String toString() {
		return "'" + getFlowModelId() + "'";
	}

}