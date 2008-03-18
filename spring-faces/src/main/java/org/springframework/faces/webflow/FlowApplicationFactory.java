package org.springframework.faces.webflow;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

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
