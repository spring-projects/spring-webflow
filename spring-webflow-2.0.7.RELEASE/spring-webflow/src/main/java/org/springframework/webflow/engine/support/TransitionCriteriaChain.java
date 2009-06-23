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
package org.springframework.webflow.engine.support;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContext;

/**
 * An ordered chain of <code>TransitionCriteria</code>. Iterates over each element in the chain, continues until one
 * returns false or the list is exhausted. So in effect it will do a logical AND between the contained criteria.
 * 
 * @author Keith Donald
 */
public class TransitionCriteriaChain implements TransitionCriteria {

	/**
	 * The ordered chain of TransitionCriteria objects.
	 */
	private List criteriaChain = new LinkedList();

	/**
	 * Creates an initially empty transition criteria chain.
	 * @see #add(TransitionCriteria)
	 */
	public TransitionCriteriaChain() {
	}

	/**
	 * Creates a transition criteria chain with the specified criteria.
	 * @param criteria the criteria
	 */
	public TransitionCriteriaChain(TransitionCriteria[] criteria) {
		criteriaChain.addAll(Arrays.asList(criteria));
	}

	/**
	 * Add given criteria object to the end of the chain.
	 * @param criteria the criteria
	 * @return this object, so multiple criteria can be added in a single statement
	 */
	public TransitionCriteriaChain add(TransitionCriteria criteria) {
		this.criteriaChain.add(criteria);
		return this;
	}

	public boolean test(RequestContext context) {
		Iterator it = criteriaChain.iterator();
		while (it.hasNext()) {
			TransitionCriteria criteria = (TransitionCriteria) it.next();
			if (!criteria.test(context)) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return new ToStringCreator(this).append("criteriaChain", criteriaChain).toString();
	}

	// static helpers

	/**
	 * Create a transition criteria chain chaining given list of actions.
	 * @param actions the actions (and their execution properties) to chain together
	 */
	public static TransitionCriteria criteriaChainFor(Action[] actions) {
		if (actions == null || actions.length == 0) {
			return WildcardTransitionCriteria.INSTANCE;
		}
		TransitionCriteriaChain chain = new TransitionCriteriaChain();
		for (int i = 0; i < actions.length; i++) {
			chain.add(new ActionTransitionCriteria(actions[i]));
		}
		return chain;
	}
}