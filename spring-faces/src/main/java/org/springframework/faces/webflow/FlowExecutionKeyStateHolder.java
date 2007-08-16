/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.faces.webflow;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionAccessException;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;

/**
 * This {@link UIComponent} instance can be added to the {@link UIViewRoot} before rendering so that the
 * {@link FlowExecution} can be properly saved and then restored during the next request's {@link PhaseId#RESTORE_VIEW}
 * phase.
 * 
 * @author Jeremy Grelle
 * @author Keith Donald
 */
public class FlowExecutionKeyStateHolder extends UIComponentBase {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	private static final String COMPONENT_FAMILY = "javax.faces.Parameter";

	/**
	 * Immutable id of the flow execution key component for easier lookup later.
	 */
	public static final String COMPONENT_ID = "FlowExecutionKeyStateHolder";

	/**
	 * The key value
	 */
	private String flowExecutionKey;

	private boolean transientValue;

	public String getId() {
		return COMPONENT_ID;
	}

	public void setId(String id) {
		// Do nothing so as to ensure the id never gets overwritten.
		return;
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public Renderer getRenderer() {
		// this component is not rendered
		return null;
	}

	/**
	 * Returns the flow execution key.
	 */
	public String getFlowExecutionKey() {
		return flowExecutionKey;
	}

	/**
	 * Sets the tracked flow execution key used to restore the current flow execution during
	 * {@link #restoreState(FacesContext, Object)}.
	 * @param flowExecutionKey the flow execution key
	 */
	public void setFlowExecutionKey(String flowExecutionKey) {
		this.flowExecutionKey = flowExecutionKey;
	}

	public boolean isTransient() {
		return transientValue;
	}

	public void setTransient(boolean transientValue) {
		this.transientValue = transientValue;
	}

	/**
	 * Restore the FlowExecution from the stored FlowExecutionKey
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		flowExecutionKey = (String) values[0];
		restoreFlowExecution(context);
	}

	private void restoreFlowExecution(FacesContext facesContext) {
		JsfExternalContext context = new JsfExternalContext(facesContext);
		// restore only if the key is present and the current flow execution has not already been restored
		if (StringUtils.hasText(flowExecutionKey) && !FlowExecutionHolderUtils.isFlowExecutionRestored(facesContext)) {
			// restore the "current" flow execution from repository so it will be available to variable/property
			// resolvers
			// and the flow navigation handler (this could happen as part of a view action like a form submission)
			FlowExecutionRepository repository = getRepository(context);
			// restore the key from the stored encoded key string
			FlowExecutionKey key = repository.parseFlowExecutionKey(flowExecutionKey);
			try {
				FlowExecutionLock lock = repository.getLock(key);
				lock.lock();
				try {
					FlowExecution flowExecution = repository.getFlowExecution(key);
					if (logger.isDebugEnabled()) {
						logger
								.debug("Loaded existing flow execution with key '"
										+ flowExecutionKey
										+ "' as part of component restoration [triggered via an action event like a button click]");
					}
					FlowExecutionHolderUtils.setFlowExecutionHolder(new FlowExecutionHolder(key, flowExecution, lock),
							facesContext);
				} catch (RuntimeException e) {
					lock.unlock();
					throw e;
				} catch (Error e) {
					lock.unlock();
					throw e;
				}
			} catch (FlowExecutionAccessException e) {
				handleFlowExecutionAccessException(e, facesContext);
			}
		}
	}

	/**
	 * Hook method to handle a thrown flow execution access exception. By default this implementation simply rethrows
	 * the exception. Subclasses may override this method to redirect to an error page or take some other action.
	 * @param e the flow execution access exception
	 * @param context the current faces context
	 */
	protected void handleFlowExecutionAccessException(FlowExecutionAccessException e, FacesContext context) {
		throw e;
	}

	/**
	 * Save the just the current FlowExecutionKey value.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[1];
		values[0] = flowExecutionKey;
		return values;
	}

	public String getClientId(FacesContext context) {
		return COMPONENT_ID;
	}

	private FlowExecutionRepository getRepository(JsfExternalContext context) {
		return FlowFacesUtils.getExecutionRepository(context.getFacesContext());
	}
}