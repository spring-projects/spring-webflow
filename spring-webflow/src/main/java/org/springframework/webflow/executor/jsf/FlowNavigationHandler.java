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
package org.springframework.webflow.executor.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.springframework.webflow.execution.ViewSelection;

/**
 * An implementation of a JSF <code>NavigationHandler</code> that provides integration with Spring Web Flow.
 * Responsible for delegating to Spring Web Flow to resume flow executions by treating JSF action outcomes
 * (like a command button click) as web flow events.
 * <p>
 * This class delegates to the standard NavigationHandler implementation when a navigation request does not pertain to a
 * flow execution.
 * <p>
 * The following navigation handler algorithm is implemented by default:
 * </p>
 * <p>
 * If a flow execution has been restored in the current request:
 * <ul>
 * <li>Continue the current flow execution by signaling the JSF action outcome as an event against the current state.
 * <li>Once event processing completes expose the selected view as the "current" {@link ViewSelection}.
 * </ul>
 * <p>
 * If a flow execution has not been restored in the current request simply delegate to the next NavigationHandler in 
 * the chain and return.
 * 
 * @author Craig McClanahan
 * @author Colin Sampaleanu
 * @author Keith Donald
 */
public class FlowNavigationHandler extends NavigationHandler {

	/**
	 * The standard navigation handler to delegate to when this one does not apply. 
	 */
	private NavigationHandler handlerDelegate;
	
	/**
	 * Create a new {@link FlowNavigationHandler} wrapping the specified standard navigation handler implementation.
	 * @param handlerDelegate the standard <code>NavigationHandler</code> this class decorates
	 */
	public FlowNavigationHandler(NavigationHandler handlerDelegate) {
		this.handlerDelegate = handlerDelegate;
	}

	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
		if (FlowExecutionHolderUtils.isFlowExecutionRestored(facesContext)) {
			// a flow execution has been restored - signal an event against it
			FlowExecutionHolder holder = FlowExecutionHolderUtils.getFlowExecutionHolder(facesContext);
			JsfExternalContext context = new JsfExternalContext(facesContext, fromAction, outcome);
			ViewSelection selectedView = holder.getFlowExecution().signalEvent(outcome, context);
			holder.setViewSelection(selectedView);
		}
		else {
			// no flow execution is restored, proceed with standard navigation
			handlerDelegate.handleNavigation(facesContext, fromAction, outcome);
		}
	}
}