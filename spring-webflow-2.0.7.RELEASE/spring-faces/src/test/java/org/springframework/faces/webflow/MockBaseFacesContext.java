package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.Lifecycle;

import org.apache.shale.test.mock.MockFacesContext12;

public class MockBaseFacesContext extends MockFacesContext12 {

	private Application application;

	public MockBaseFacesContext() {
		super();
	}

	public MockBaseFacesContext(ExternalContext externalContext) {
		super(externalContext);
	}

	public MockBaseFacesContext(ExternalContext externalContext, Lifecycle lifecycle) {
		super(externalContext, lifecycle);
	}

	public Application getApplication() {
		if (application == null) {
			ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
					.getFactory(FactoryFinder.APPLICATION_FACTORY);
			application = applicationFactory.getApplication();
		}
		return application;
	}
}
