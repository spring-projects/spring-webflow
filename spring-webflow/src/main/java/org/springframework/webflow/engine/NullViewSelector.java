/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.engine;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * Makes a null view selection, indicating no response should be issued.
 * 
 * @see org.springframework.webflow.execution.ViewSelection#NULL_VIEW
 * 
 * @author Keith Donald
 */
public final class NullViewSelector implements ViewSelector, Serializable {

	/*
	 * Implementation note: not located in webflow.execution.support package to
	 * avoid a cyclic dependency between webflow.execution and webflow.execution.support. 
	 */

	/**
	 * The shared singleton {@link NullViewSelector} instance. 
	 */
	public static final ViewSelector INSTANCE = new NullViewSelector();

	/**
	 * Private constructor since this is a singleton.
	 */
	private NullViewSelector() {
	}

	public boolean isEntrySelectionRenderable(RequestContext context) {
		return true;
	}

	public ViewSelection makeEntrySelection(RequestContext context) {
		return ViewSelection.NULL_VIEW;
	}

	public ViewSelection makeRefreshSelection(RequestContext context) {
		return makeEntrySelection(context);
	}

	// resolve the singleton instance
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}
	
}