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

import java.io.IOException;
import java.io.Writer;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialResponseWriter;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.servlet.AbstractAjaxHandler;

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
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		if (facesContext != null) {
			return facesContext.getPartialViewContext().isAjaxRequest();
		} else {
			String header = request.getHeader("Faces-Request");
			String param = request.getParameter("jakarta.faces.partial.ajax");
			return "partial/ajax".equals(header) || "true".equals(param);
		}
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
			PartialResponseWriter writer = createPartialResponseWriter(facesContext);
			writer.startDocument();
			writer.redirect(response.encodeRedirectURL(targetUrl));
			writer.endDocument();
		} finally {
			helper.releaseIfNecessary();
		}
	}

	private PartialResponseWriter createPartialResponseWriter(FacesContext context) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
		String encoding = externalContext.getRequestCharacterEncoding();
		externalContext.setResponseContentType("application/xml");
		externalContext.setResponseCharacterEncoding(encoding);
		ResponseWriter responseWriter = null;
		Writer out = externalContext.getResponseOutputWriter();
		if (out != null) {
			RenderKitFactory factory = JsfUtils.findFactory(RenderKitFactory.class);
			RenderKit renderKit = factory.getRenderKit(context, RenderKitFactory.HTML_BASIC_RENDER_KIT);
			responseWriter = renderKit.createResponseWriter(out, "text/xml", encoding);
		}
		if (responseWriter instanceof PartialResponseWriter) {
			return (PartialResponseWriter) responseWriter;
		} else {
			return new PartialResponseWriter(responseWriter);
		}
	}

}
