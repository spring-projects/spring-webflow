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
package org.springframework.faces.webflow;

import javax.faces.component.UIViewRoot;

import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * Specialized {@link Action} implementation for rendering the JSF view in an {@link EndState}.
 * <p>
 * Before the final view is rendered, the {@link UIViewRoot} will be marked transient in order to bypass the JSF state
 * saving process.
 * 
 * @author Jeremy Grelle
 */
public class JsfFinalResponseAction implements Action {

	private JsfViewFactory viewFactory;

	public JsfFinalResponseAction(JsfViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public Event execute(RequestContext context) throws Exception {
		View view = viewFactory.getView(context);
		((JsfView) view).getViewRoot().setTransient(true);
		view.render();
		return new Event(this, "success");
	}

}
