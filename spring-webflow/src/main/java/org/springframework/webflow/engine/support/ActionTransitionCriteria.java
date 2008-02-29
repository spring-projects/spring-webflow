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

import org.springframework.util.Assert;
import org.springframework.webflow.engine.ActionExecutor;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * A transition criteria that will execute an action when tested and return <code>true</code> if the action's result
 * is equal to the 'trueEventId', <code>false</code> otherwise.
 * <p>
 * This effectively adapts an <code>Action</code> to a <code>TransitionCriteria</code>.
 * 
 * @see org.springframework.webflow.execution.Action
 * @see org.springframework.webflow.engine.TransitionCriteria
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ActionTransitionCriteria implements TransitionCriteria {

	/**
	 * The result event id that should map to a <code>true</code> return value.
	 */
	private String trueEventId = "success";

	/**
	 * The action to execute when the criteria is tested, annotated with usage attributes.
	 */
	private Action action;

	/**
	 * Create action transition criteria delegating to the specified action.
	 * @param action the action
	 */
	public ActionTransitionCriteria(Action action) {
		this.action = action;
	}

	/**
	 * Returns the action result <code>eventId</code> that should cause this criteria to return true (it will return
	 * false otherwise). Defaults to "success".
	 */
	public String getTrueEventId() {
		return trueEventId;
	}

	/**
	 * Sets the action result <code>eventId</code> that should cause this precondition to return true (it will return
	 * false otherwise).
	 * @param trueEventId the true result event ID
	 */
	public void setTrueEventId(String trueEventId) {
		Assert.notNull(trueEventId, "The trueEventId is required");
		this.trueEventId = trueEventId;
	}

	/**
	 * Returns the action wrapped by this object.
	 * @return the action
	 */
	protected Action getAction() {
		return action;
	}

	public boolean test(RequestContext context) {
		Event result = ActionExecutor.execute(getAction(), context);
		return result != null && getTrueEventId().equals(result.getId());
	}
}