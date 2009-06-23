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
package org.springframework.faces.webflow;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.StateManager;

/**
 * Custom {@link ApplicationFactory} that ensures the FlowViewStateManager is the first {@link StateManager} in the
 * chain so that Web Flow may manage JSF component state when a flow is active.
 * 
 * @author Jeremy Grelle
 * 
 */
public class FlowApplicationFactory extends ApplicationFactory {

	private ApplicationFactory delegate;

	public FlowApplicationFactory(ApplicationFactory delegate) {
		this.delegate = delegate;
	}

	public Application getApplication() {
		Application app = delegate.getApplication();
		// Ensure that FlowViewStateManager is first in the chain
		if (app.getStateManager() != null && !(app.getStateManager() instanceof FlowViewStateManager)) {
			FlowViewStateManager sm = new FlowViewStateManager(app.getStateManager());
			app.setStateManager(sm);
		}
		return app;
	}

	public void setApplication(Application application) {
		delegate.setApplication(application);
	}

}
