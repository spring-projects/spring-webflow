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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.context.MessageSource;

/**
 * {@link FacesContextFactory} implementation that installs the {@link FlowFacesContext}, allowing for integration with
 * a Spring {@link MessageSource}.
 * 
 * @author Jeremy Grelle
 */
public class FlowFacesContextFactory extends FacesContextFactory {

	FacesContextFactory delegate;

	public FlowFacesContextFactory(FacesContextFactory delegate) {
		this.delegate = delegate;
	}

	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {
		return new FlowFacesContext(delegate.getFacesContext(context, request, response, lifecycle));
	}

}
