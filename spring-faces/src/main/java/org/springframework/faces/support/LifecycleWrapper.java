/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.support;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

/**
 * Provides a simple implementation of {@link Lifecycle} that can be subclassed by developers wishing to provide
 * specialized behavior to an existing {@link Lifecycle instance} . The default implementation of all methods is to call
 * through to the wrapped {@link Lifecycle}.
 * 
 * @author Phillip Webb
 * 
 * @since 2.4
 */
public abstract class LifecycleWrapper extends Lifecycle implements FacesWrapper<Lifecycle> {

	public abstract Lifecycle getWrapped();

	public void addPhaseListener(PhaseListener listener) {
		getWrapped().addPhaseListener(listener);
	}

	public void execute(FacesContext context) throws FacesException {
		getWrapped().execute(context);
	}

	public PhaseListener[] getPhaseListeners() {
		return getWrapped().getPhaseListeners();
	}

	public void removePhaseListener(PhaseListener listener) {
		getWrapped().removePhaseListener(listener);
	}

	public void render(FacesContext context) throws FacesException {
		getWrapped().render(context);
	}
}
