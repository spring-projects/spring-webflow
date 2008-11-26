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
package org.springframework.webflow.action;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewFactory;

/**
 * Simple adapter that adapts a view factory render cycle to the action interface.
 * @author Keith Donald
 */
public class ViewFactoryActionAdapter extends AbstractAction {

	private ViewFactory viewFactory;

	/**
	 * Creates a new view factory action adapter
	 * @param viewFactory the view factory to adapt to the Action interface.
	 */
	public ViewFactoryActionAdapter(ViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		if (viewFactory != null) {
			viewFactory.getView(context).render();
		}
		return new Event(this, "success");
	}
}