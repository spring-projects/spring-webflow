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

import static org.springframework.faces.webflow.JsfRuntimeInformation.isAtLeastJsf20;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

import org.springframework.util.Assert;

/**
 * Custom {@link ApplicationFactory} that ensures the FlowApplication is the first {@link Application} in the chain,
 * which in turn guarantees the install order for other JSF components.
 * 
 * @see FlowApplication
 * 
 * @author Jeremy Grelle
 */
public class FlowApplicationFactory extends ApplicationFactory {

	private ApplicationFactory delegate;

	public FlowApplicationFactory(ApplicationFactory delegate) {
		Assert.notNull(delegate, "The delegate ApplicationFactory instance must not be null!");
		this.delegate = delegate;
	}

	public Application getApplication() {
		Application delegateApplication = delegate.getApplication();
		if (delegateApplication != null && (!(delegateApplication instanceof FlowApplication))) {
			Application flowApplication = (isAtLeastJsf20()) ? new Jsf2FlowApplication(delegateApplication)
					: new FlowApplication(delegateApplication);
			setApplication(flowApplication);
		}
		return delegate.getApplication();
	}

	public void setApplication(Application application) {
		delegate.setApplication(application);
	}

	public ApplicationFactory getWrapped() {
		return delegate;
	}

}
