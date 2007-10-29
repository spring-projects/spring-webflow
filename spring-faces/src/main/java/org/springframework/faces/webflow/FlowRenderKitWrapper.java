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

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

/**
 * Delegating {@link RenderKit} implementation that provides a custom {@link ResponseStateManager} for storing JSF view
 * state as part of the current FlowExecution.
 * 
 * @author Jeremy Grelle
 */
public class FlowRenderKitWrapper extends RenderKit {

	RenderKit delegate;

	ResponseStateManager manager = new FlowResponseStateManager();

	public FlowRenderKitWrapper(RenderKit renderKit) {
		this.delegate = renderKit;
	}

	public void addRenderer(String family, String rendererType, Renderer renderer) {
		delegate.addRenderer(family, rendererType, renderer);
	}

	public ResponseStream createResponseStream(OutputStream out) {
		return delegate.createResponseStream(out);
	}

	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
		return delegate.createResponseWriter(writer, contentTypeList, characterEncoding);
	}

	public Renderer getRenderer(String family, String rendererType) {
		return delegate.getRenderer(family, rendererType);
	}

	public ResponseStateManager getResponseStateManager() {
		return manager;
	}

}
