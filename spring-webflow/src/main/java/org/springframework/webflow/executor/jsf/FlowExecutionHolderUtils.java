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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionContextHolder;

/**
 * A static utility class for accessing the current flow execution holder.
 * <p>
 * By default, the current flow execution holder is stored associated with the current thread in the
 * {@link FacesContext}'s {@link ExternalContext#getRequestMap()}.
 * 
 * @author Keith Donald
 * @author Craig McClanahan
 */
public class FlowExecutionHolderUtils {

	/**
	 * Returns the current flow execution holder for the given faces context.
	 * @param context faces context
	 * @return the flow execution holder, or <code>null</code> if none set.
	 */
	public static FlowExecutionHolder getFlowExecutionHolder(FacesContext context) {
		return (FlowExecutionHolder) context.getExternalContext().getRequestMap().get(getFlowExecutionHolderKey());
	}

	/**
	 * Sets the current flow execution holder for the given faces context.
	 * @param holder the flow execution holder
	 * @param context faces context
	 */
	public static void setFlowExecutionHolder(FlowExecutionHolder holder, FacesContext context) {
		context.getExternalContext().getRequestMap().put(getFlowExecutionHolderKey(), holder);
		FlowExecutionContextHolder.setFlowExecutionContext(holder.getFlowExecution());
	}

	/**
	 * Returns true if the flow execution has been restored in the current thread.
	 * @param context the faces context
	 * @return true if restored, false otherwise
	 */
	public static boolean isFlowExecutionRestored(FacesContext context) {
		return getFlowExecutionHolder(context) != null;
	}

	/**
	 * Returns the current flow execution in the given faces context.
	 * @param context faces context
	 * @return the flow execution or <code>null</code> if no execution is bound
	 */
	public static FlowExecution getCurrentFlowExecution(FacesContext context) {
		FlowExecutionHolder holder = getFlowExecutionHolder(context);
		if (holder != null) {
			return holder.getFlowExecution();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the current required flow execution in the given faces context.
	 * @param context faces context
	 * @return the flow execution
	 * @throws EvaluationException if no flow execution was bound
	 */
	public static FlowExecution getRequiredCurrentFlowExecution(FacesContext context) throws EvaluationException {
		FlowExecution execution = getCurrentFlowExecution(context);
		if (execution != null) {
			return execution;
		}
		else {
			throw new EvaluationException("No current FlowExecution bound to the Faces Context "
					+ "- was the current flow execution not restored before a view referenced it? "
					+ "Has the flow execution ended or expired?");
		}
	}

	/**
	 * Unlocks the current flow execution in the faces context if necessary.
	 * Can be safely called even if no execution is bound or one is bound but not locked.
	 * @param context the faces context
	 */
	public static void unlockCurrentFlowExecutionIfNecessary(FacesContext context) {
		if (isFlowExecutionRestored(context)) {
			getFlowExecutionHolder(context).unlockFlowExecutionIfNecessary();
		}
	}
	
	private static String getFlowExecutionHolderKey() {
		return FlowExecutionHolder.class.getName();
	}
}