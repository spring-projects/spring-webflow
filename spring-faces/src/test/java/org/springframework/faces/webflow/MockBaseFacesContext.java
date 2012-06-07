package org.springframework.faces.webflow;

import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.Lifecycle;

import org.apache.myfaces.test.mock.MockFacesContext20;

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
