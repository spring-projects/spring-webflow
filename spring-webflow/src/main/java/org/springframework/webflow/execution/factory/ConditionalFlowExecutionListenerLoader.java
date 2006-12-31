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
package org.springframework.webflow.execution.factory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListener;

/**
 * A flow execution listener loader that stores listeners in a list-backed data
 * structure and allows for configuration of which listeners should apply to
 * which flow definitions. For trivial listener loading, see
 * {@link StaticFlowExecutionListenerLoader}.
 *
 * @see StaticFlowExecutionListenerLoader
 *
 * @author Keith Donald
 */
public class ConditionalFlowExecutionListenerLoader implements FlowExecutionListenerLoader {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * The list of flow execution listeners containing
	 * {@link ConditionalFlowExecutionListenerHolder} objects. The list
	 * determines the conditions in which a single flow execution listener
	 * applies.
	 */
	private List listeners = new LinkedList();

	/**
	 * Add a listener that will listen to executions for all flows.
	 * @param listener the listener to add
	 */
	public void addListener(FlowExecutionListener listener) {
		addListener(listener, null);
	}

	/**
	 * Adds a collection of listeners that share a matching criteria.
	 * @param listeners the listeners
	 * @param criteria the criteria where these listeners apply
	 */
	public void addListeners(FlowExecutionListener[] listeners, FlowExecutionListenerCriteria criteria) {
		for (int i = 0; i < listeners.length; i++) {
			addListener(listeners[i], criteria);
		}
	}

	/**
	 * Add a listener that will listen to executions to flows matching the
	 * specified criteria.
	 * @param listener the listener
	 * @param criteria the listener criteria
	 */
	public void addListener(FlowExecutionListener listener, FlowExecutionListenerCriteria criteria) {
		if (listener == null) {
			return;
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
	 * Set the list of flow execution listeners with corresponding criteria.
	 * Allows for bean style configuration. The given map should have
	 * {@link FlowExecutionListener} objects as keys and Strings ("*", "flowId",
	 * "flowId1,flowId2") or {@link FlowExecutionListenerCriteria}
	 * objects as values. This will clear any listeners registered with
	 * this object using the <tt>addListener</tt> methods.
	 * @param listenersWithCriteria the map of listeners and their corresponding criteria
	 */
	public void setListeners(Map listenersWithCriteria) {
		removeAllListeners();
		for (Iterator it = listenersWithCriteria.entrySet().iterator(); it.hasNext(); ) {
			Entry entry = (Entry)it.next();
			Assert.isInstanceOf(FlowExecutionListener.class, entry.getKey(),
					"The key in the listenersWithCriteria map needs to be a FlowExecutionListener object");
			FlowExecutionListener listener = (FlowExecutionListener)entry.getKey();
			FlowExecutionListenerCriteria criteria = null;
			if (entry.getValue() instanceof String) {
				criteria = getCriteria((String)entry.getValue());
			}
			else if (entry.getValue() instanceof FlowExecutionListenerCriteria) {
				criteria = (FlowExecutionListenerCriteria)entry.getValue();
			}
			else if (entry.getValue() != null) {
				throw new IllegalArgumentException(
						"The value in the listenersWithCriteria map needs to be a " +
						"String or a FlowExecutionListenerCriteria object");
			}
			addListener(listener, criteria);
		}
	}

	/**
	 * Is the given listener contained by this Flow execution manager?
	 * @param listener the listener
	 * @return true if yes, false otherwise
	 */
	public boolean containsListener(FlowExecutionListener listener) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			ConditionalFlowExecutionListenerHolder h = (ConditionalFlowExecutionListenerHolder)it.next();
			if (h.getListener().equals(listener)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove the flow execution listener from the listener list.
	 * @param listener the listener
	 */
	public void removeListener(FlowExecutionListener listener) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			ConditionalFlowExecutionListenerHolder h = (ConditionalFlowExecutionListenerHolder)it.next();
			if (h.getListener().equals(listener)) {
				it.remove();
			}
		}
	}

	/**
	 * Remove all listeners loadable by this loader.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Remove the criteria for the specified listener.
	 * @param listener the listener
	 * @param criteria the criteria
	 */
	public void removeListenerCriteria(FlowExecutionListener listener, FlowExecutionListenerCriteria criteria) {
		if (containsListener(listener)) {
			ConditionalFlowExecutionListenerHolder listenerHolder = getHolder(listener);
			listenerHolder.remove(criteria);
			if (listenerHolder.isCriteriaSetEmpty()) {
				removeListener(listener);
			}
		}
	}

	/**
	 * Returns the array of flow execution listeners for specified flow.
	 * @param flowDefinition the flow definition associated with the execution
	 * to be listened to
	 * @return the flow execution listeners that apply
	 */
	public FlowExecutionListener[] getListeners(FlowDefinition flowDefinition) {
		Assert.notNull(flowDefinition, "The Flow to load listeners for cannot be null");
		List listenersToAttach = new LinkedList();
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			ConditionalFlowExecutionListenerHolder listenerHolder = (ConditionalFlowExecutionListenerHolder)it.next();
			if (listenerHolder.listenerAppliesTo(flowDefinition)) {
				listenersToAttach.add(listenerHolder.getListener());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded [" + listenersToAttach.size() + "] of possible " + listeners.size()
					+ " listeners for this execution request for flow '" + flowDefinition.getId()
					+ "', the listeners to attach are " + StylerUtils.style(listenersToAttach));
		}
		return (FlowExecutionListener[])listenersToAttach.toArray(new FlowExecutionListener[listenersToAttach.size()]);
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
			ConditionalFlowExecutionListenerHolder next = (ConditionalFlowExecutionListenerHolder)it.next();
			if (next.getListener().equals(listener)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Decode given string value into one of the well known criteria types.
	 * @see FlowExecutionListenerCriteriaFactory
	 */
	protected FlowExecutionListenerCriteria getCriteria(String value) {
		if ("*".equals(value)) {
			return new FlowExecutionListenerCriteriaFactory().allFlows();
		}
		else {
			String[] flowIds = StringUtils.commaDelimitedListToStringArray(value);
			for (int i = 0; i < flowIds.length; i++) {
				flowIds[i] = flowIds[i].trim();
			}
			return new FlowExecutionListenerCriteriaFactory().flows(flowIds);
		}
	}

}