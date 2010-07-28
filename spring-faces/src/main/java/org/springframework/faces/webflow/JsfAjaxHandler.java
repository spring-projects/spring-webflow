/*
 * Copyright 2004-2010 the original author or authors.
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

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.PartialResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.js.ajax.AbstractAjaxHandler;

import com.sun.faces.context.PartialViewContextImpl;

/**
 * Ajax handler for JSF 2 requests that can identify JSF 2 Ajax requests and send redirect instructions back to the
 * client by including a redirect instruction in the content of the response.
 * 
 * @see AbstractAjaxHandler
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class JsfAjaxHandler extends AbstractAjaxHandler {

	public JsfAjaxHandler() {
		this(null);
	}

	public JsfAjaxHandler(AbstractAjaxHandler delegate) {
		super(delegate);
	}

	protected boolean isAjaxRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		String facesRequestHeader = request.getHeader("Faces-Request");
		return ("partial/ajax".equals(facesRequestHeader)) ? true : false;
	}

	protected void sendAjaxRedirectInternal(final String targetUrl, final HttpServletRequest request,
			final HttpServletResponse response, boolean popup) throws IOException {

		// Ideally facesContext.getExternalContext().redirect() should be used instead of the code in this method.
		// However PartialViewContextImpl.createPartialResponseWriter() calls cxt.getRenderKit(), which in turn
		// tries to get the UIViewRoot's renderKitId. That results in NPE when JsfAjaxHandler is called
		// outside of flow execution. The code below wraps the FacesContext to override getRenderKit() and provide a
		// default render kit id.

		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getServletContext(), request, response);
			facesContext = new FixedRenderKitFacesContext(facesContext, determineRenderKitId(request, response));
			PartialViewContextImpl partialViewContext = new PartialViewContextImpl(facesContext);
			PartialResponseWriter writer = partialViewContext.getPartialResponseWriter();
			writer.startDocument();
			writer.redirect(targetUrl);
			writer.endDocument();
		} finally {
			helper.releaseIfNecessary();
		}
	}

	protected String determineRenderKitId(HttpServletRequest request, HttpServletResponse response) {
		return RenderKitFactory.HTML_BASIC_RENDER_KIT;
	}

	private class FixedRenderKitFacesContext extends FacesContextWrapper {

		private FacesContext delegate;
		private String renderKitId;

		public FixedRenderKitFacesContext(FacesContext delegate, String renderKitId) {
			this.delegate = delegate;
			this.renderKitId = renderKitId;
		}

		@Override
		public FacesContext getWrapped() {
			return this.delegate;
		}

		@Override
		public RenderKit getRenderKit() {
			RenderKitFactory factory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
			return factory.getRenderKit(this, renderKitId);
		}
	}

}
