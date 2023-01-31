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
package org.springframework.faces.webflow;

import java.util.Iterator;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.render.RenderKit;

/**
 * Mock implementation of the <code>FacesContext</code> class to facilitate standalone Action unit tests.
 * <p>
 * NOT intended to be used for anything but standalone unit tests. This is a simple state holder, a <i>stub</i>
 * implementation, at least if you follow <a href="https://www.martinfowler.com/articles/mocksArentStubs.html">Martin
 * Fowler's</a> reasoning. This class is called <i>Mock</i>FacesContext to be consistent with the naming convention in
 * the rest of the Spring framework (e.g. MockHttpServletRequest, ...).
 * 
 * @see jakarta.faces.context.FacesContext
 * 
 * @author Ulrik Sandberg
 */
public class MockFacesContext extends FacesContext {
	private ExternalContext externalContext;

	private Application application;

	private UIViewRoot viewRoot;

	public Application getApplication() {
		return this.application;
	}

	/**
	 * Set the application to be used by this faces context.
	 * @param application the applicaiton to set.
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	public Iterator<String> getClientIdsWithMessages() {
		return null;
	}

	@Override
	public Lifecycle getLifecycle() {
		return null;
	}

	public ExternalContext getExternalContext() {
		return this.externalContext;
	}

	/**
	 * Set the external context of this faces context.
	 * @param externalContext the external context to set.
	 */
	public void setExternalContext(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public Severity getMaximumSeverity() {
		return null;
	}

	public Iterator<FacesMessage> getMessages() {
		return null;
	}

	public Iterator<FacesMessage> getMessages(String arg0) {
		return null;
	}

	public RenderKit getRenderKit() {
		return null;
	}

	public boolean getRenderResponse() {
		return false;
	}

	public boolean getResponseComplete() {
		return false;
	}

	public ResponseStream getResponseStream() {
		return null;
	}

	public void setResponseStream(ResponseStream arg0) {

	}

	public ResponseWriter getResponseWriter() {
		return null;
	}

	public void setResponseWriter(ResponseWriter arg0) {
	}

	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	public void setViewRoot(UIViewRoot viewRoot) {
		this.viewRoot = viewRoot;
	}

	public void addMessage(String arg0, FacesMessage arg1) {
	}

	public void release() {
	}

	public void renderResponse() {
	}

	public void responseComplete() {
	}
}
