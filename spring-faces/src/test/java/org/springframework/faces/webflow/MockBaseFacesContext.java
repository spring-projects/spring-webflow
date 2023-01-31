package org.springframework.faces.webflow;

import java.util.Map;

import org.apache.myfaces.test.mock.MockFacesContext20;

import jakarta.faces.FactoryFinder;
import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.lifecycle.Lifecycle;

public class MockBaseFacesContext extends MockFacesContext20 {

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

	public Map<Object, Object> getAttributes() {
		return super.getAttributes();
	}

	@Override
	public Lifecycle getLifecycle() {
		return null;
	}

	public Application getApplication() {
		if (this.application == null) {
			ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
					.getFactory(FactoryFinder.APPLICATION_FACTORY);
			this.application = applicationFactory.getApplication();
		}
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
