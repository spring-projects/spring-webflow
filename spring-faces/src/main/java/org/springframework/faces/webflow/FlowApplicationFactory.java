/*
 * Copyright 2004-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.webflow;

import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;

import org.springframework.util.Assert;

/**
 * Custom {@link ApplicationFactory} that ensures the FlowApplication is the first {@link Application} in the chain,
 * which in turn guarantees the install order for other JSF components.
 * 
 * @see FlowApplication
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class FlowApplicationFactory extends ApplicationFactory {

	private final ApplicationFactory wrapped;

	public FlowApplicationFactory(ApplicationFactory wrapped) {
		Assert.notNull(wrapped, "The wrapped ApplicationFactory instance must not be null!");
		this.wrapped = wrapped;
	}

	public Application getApplication() {
		Application application = this.wrapped.getApplication();
		if (application != null && (!(application instanceof FlowApplication))) {
			setApplication(new FlowApplication(application));
		}
		return this.wrapped.getApplication();
	}

	public void setApplication(Application application) {
		this.wrapped.setApplication(application);
	}

	public ApplicationFactory getWrapped() {
		return this.wrapped;
	}
}
