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
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListener;

/**
 * A holder that holds a listener plus a set of criteria defining the flows in which that listener applies.
 * <p>
 * This is an internal helper class used by the {@link ConditionalFlowExecutionListenerLoader}.
 * 
 * @see ConditionalFlowExecutionListenerLoader
 * 
 * @author Keith Donald
 */
class ConditionalFlowExecutionListenerHolder {

	/**
	 * The held listener.
	 */
	private FlowExecutionListener listener;

	/**
	 * The listener criteria set.
	 */
	private Set criteriaSet = new LinkedHashSet(3);

	/**
	 * Create a new conditional flow execution listener holder.
	 * @param listener the listener to hold
	 */
	public ConditionalFlowExecutionListenerHolder(FlowExecutionListener listener) {
		Assert.notNull(listener, "The listener is required");
		this.listener = listener;
	}

	/**
	 * Returns the held listener.
	 */
	public FlowExecutionListener getListener() {
		return listener;
	}

	/**
	 * Add given criteria.
	 */
	public void add(FlowExecutionListenerCriteria criteria) {
		criteriaSet.add(criteria);
	}

	/**
	 * Remove given criteria. If not present, does nothing.
	 */
	public void remove(FlowExecutionListenerCriteria criteria) {
		criteriaSet.remove(criteria);
	}

	/**
	 * Are there any criteria registered?
	 */
	public boolean isCriteriaSetEmpty() {
		return criteriaSet.isEmpty();
	}

	/**
	 * Determines if the listener held by this holder applies to the specified flow definition. Will do a logical OR
	 * between the registered criteria.
	 * @param flowDefinition the flow
	 * @return true if yes, false otherwise
	 */
	public boolean listenerAppliesTo(FlowDefinition flowDefinition) {
		Iterator it = criteriaSet.iterator();
		while (it.hasNext()) {
			FlowExecutionListenerCriteria criteria = (FlowExecutionListenerCriteria) it.next();
			if (criteria.appliesTo(flowDefinition)) {
				return true;
			}
		}
		return false;
	}
}