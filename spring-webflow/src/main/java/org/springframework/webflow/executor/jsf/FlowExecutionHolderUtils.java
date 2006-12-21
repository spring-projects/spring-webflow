/*
 * Copyright 2002-2006 the original author or authors.
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

/**
 * A static utility class for accessing the current flow execution holder.
 * <p>
 * By default, the current flow execution holder is stored associated with the
 * current thread in the {@link FacesContext}'s
 * {@link ExternalContext#getRequestMap()}.
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
		return (FlowExecutionHolder)context.getExternalContext().getRequestMap().get(getFlowExecutionHolderKey());
	}

	/**
	 * Sets the current flow execution holder for the given faces context.
	 * @param holder the flow execution holder
	 * @param context faces context
	 */
	public static void setFlowExecutionHolder(FlowExecutionHolder holder, FacesContext context) {
		context.getExternalContext().getRequestMap().put(getFlowExecutionHolderKey(), holder);
	}

	private static String getFlowExecutionHolderKey() {
		return FlowExecutionHolder.class.getName();
	}

	public static boolean isFlowExecutionRestored(FacesContext context) {
		return getFlowExecutionHolder(context) != null;
	}
	
	public static boolean isFlowExecutionChanged(FacesContext context) {
		return isFlowExecutionRestored(context) && getFlowExecutionHolder(context).needsSave();
	}
}