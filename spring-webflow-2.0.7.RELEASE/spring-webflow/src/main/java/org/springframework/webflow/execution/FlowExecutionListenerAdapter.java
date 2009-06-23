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
package org.springframework.webflow.execution;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;

/**
 * An abstract adapter class for listeners (observers) of flow execution lifecycle events. The methods in this class are
 * empty. This class exists as convenience for creating listener objects; subclass it and override what you need.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 * @author Scott Andrews
 */
public abstract class FlowExecutionListenerAdapter implements FlowExecutionListener {

	public void requestSubmitted(RequestContext context) {
	}

	public void requestProcessed(RequestContext context) {
	}

	public void sessionCreating(RequestContext context, FlowDefinition definition) {
	}

	public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input) {
	}

	public void sessionStarted(RequestContext context, FlowSession session) {
	}

	public void eventSignaled(RequestContext context, Event event) {
	}

	public void transitionExecuting(RequestContext context, TransitionDefinition transition) {
	}

	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
	}

	public void viewRendered(RequestContext context, View view, StateDefinition viewState) {
	}

	public void viewRendering(RequestContext context, View view, StateDefinition viewState) {
	}

	public void stateEntered(RequestContext context, StateDefinition previousState, StateDefinition newState) {
	}

	public void paused(RequestContext context) {
	}

	public void resuming(RequestContext context) {
	}

	public void sessionEnding(RequestContext context, FlowSession session, String outcome, MutableAttributeMap output) {
	}

	public void sessionEnded(RequestContext context, FlowSession session, String outcome, AttributeMap output) {
	}

	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
	}

}