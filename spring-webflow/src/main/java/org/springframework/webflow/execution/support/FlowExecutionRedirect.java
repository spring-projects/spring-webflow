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
package org.springframework.webflow.execution.support;

import java.io.ObjectStreamException;

import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.ViewSelection;

/**
 * Concrete response type that refreshes an application view by redirecting
 * to an <i>existing</i>, active Spring Web Flow execution at a unique
 * SWF-specific <i>flow execution URL</i>. This enables the triggering of
 * post-redirect-get semantics from within an <i>active</i> flow execution.
 * <p>
 * Once the redirect response is issued a new request is initiated by the
 * browser targeted at the flow execution URL. The URL is stabally refreshable
 * (and bookmarkable) while the conversation remains active, safely triggering a
 * {@link ViewState#refresh(org.springframework.webflow.execution.RequestContext)}
 * on each access.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public final class FlowExecutionRedirect extends ViewSelection {

	/**
	 * The single instance of this class.
	 */
	public static final FlowExecutionRedirect INSTANCE = new FlowExecutionRedirect();

	/**
	 * Avoid instantiation.
	 */
	private FlowExecutionRedirect() {
	}

	// resolve the singleton instance
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}

	public String toString() {
		return "redirect:";
	}
}