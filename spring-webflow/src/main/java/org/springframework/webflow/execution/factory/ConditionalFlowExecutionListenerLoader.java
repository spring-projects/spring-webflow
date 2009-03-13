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
package org.springframework.webflow.execution.factory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListener;

/**
 * A flow execution listener loader that stores listeners in a list-backed data structure and allows for configuration
 * of which listeners should apply to which flow definitions. For trivial listener loading, see
 * {@link StaticFlowExecutionListenerLoader}.
 * 
 * @see FlowExecutionListenerCriteria
 * @see StaticFlowExecutionListenerLoader
 * 
 * @author Keith Donald
 */
public class ConditionalFlowExecutionListenerLoader implements FlowExecutionListenerLoader {

	private final Log logger = LogFactory.getLog(ConditionalFlowExecutionListenerLoader.class);

	/**
	 * The list of flow execution listeners containing {@link ConditionalFlowExecutionListenerHolder} objects. The list
	 * determines the conditions in which a single flow execution listener applies.
	 */
	private List listeners = new LinkedList();

	/**
	 * Add a listener that will listen to executions to flows matching the specified criteria.
	 * @param listener the listener
	 * @param criteria the listener criteria
	 */
	public void addListener(FlowExecutionListener listener, FlowExecutionListenerCriteria criteria) {
		if (listener == null) {
			throw new IllegalArgumentException("The flow execution listener cannot be null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding flow execution listener " + listener + " with criteria " + criteria);
		}
		ConditionalFlowExecutionListenerHolder conditional = getHolder(listener);
		if (conditional == null) {
			conditional = new ConditionalFlowExecutionListenerHolder(listener);
			listeners.add(conditional);
		}
		if (criteria == null) {
			criteria = new FlowExecutionListenerCriteriaFactory().allFlows();
		}
		conditional.add(criteria);
	}

	/**
	 * Returns the array of flow execution listeners for specified flow.
	 * @param flowDefinition the flow definition associated with the execution to be listened to
	 * @return the flow execution listeners that apply
	 */
	public FlowExecutionListener[] getListeners(FlowDefinition flowDefinition) {
		Assert.notNull(flowDefinition, "The Flow to load listeners for cannot be null");
		List listenersToAttach = new LinkedList();
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			ConditionalFlowExecutionListenerHolder listenerHolder = (ConditionalFlowExecutionListenerHolder) it.next();
			if (listenerHolder.listenerAppliesTo(flowDefinition)) {
				listenersToAttach.add(listenerHolder.getListener());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded [" + listenersToAttach.size() + "] of possible " + listeners.size()
					+ " listeners for this execution request for flow '" + flowDefinition.getId()
					+ "', the listeners to attach are " + StylerUtils.style(listenersToAttach));
		}
		return (FlowExecutionListener[]) listenersToAttach.toArray(new FlowExecutionListener[listenersToAttach.size()]);
	}

	// internal helpers

	/**
	 * Lookup the listener criteria holder for the listener provided.
	 * @param listener the listener
	 * @return the holder, or null if not found
	 */
	private ConditionalFlowExecutionListenerHolder getHolder(FlowExecutionListener listener) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			ConditionalFlowExecutionListenerHolder next = (ConditionalFlowExecutionListenerHolder) it.next();
			if (next.getListener().equals(listener)) {
				return next;
			}
		}
		return null;
	}
}