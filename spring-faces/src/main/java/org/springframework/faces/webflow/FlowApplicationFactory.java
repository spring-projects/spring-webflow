package org.springframework.faces.webflow;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class FlowApplicationFactory extends ApplicationFactory {

	private ApplicationFactory delegate;

	private boolean stateManagerConfigured = false;

	public FlowApplicationFactory(ApplicationFactory delegate) {
		this.delegate = delegate;
	}

	public Application getApplication() {
		Application app = delegate.getApplication();
		// Ensure that FlowViewStateManager is first in the chain
		if (!stateManagerConfigured && app.getStateManager() != null) {
			FlowViewStateManager sm = new FlowViewStateManager(app.getStateManager());
			app.setStateManager(sm);
			stateManagerConfigured = true;
		}
		return app;
	}

	public void setApplication(Application application) {
		delegate.setApplication(application);
	}

}
