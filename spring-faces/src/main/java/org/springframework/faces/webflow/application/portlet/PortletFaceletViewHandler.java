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
package org.springframework.faces.webflow.application.portlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.portlet.RenderResponse;

import org.springframework.faces.webflow.JsfRuntimeInformation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sun.facelets.FaceletViewHandler;

/**
 * <p>
 * This {@link ViewHandler} implementation is needed because portions of the native Facelets ViewHandler depend on the
 * Servlet API and cannot be used directly in a Portlet environment.
 * </p>
 * 
 * <p>
 * Note: the basis for this code was a Facelets sample provided with Apache MyFaces Portlet Bridge for JSF version
 * 2.0.0.alpha-2.
 * </p>
 * 
 * @since 2.2.0
 */
public class PortletFaceletViewHandler extends FaceletViewHandler {

	private static final String FACELETS_CONTENT_TYPE_KEY = "facelets.ContentType";
	private static final String FACELETS_ENCODING_KEY = "facelets.Encoding";

	public PortletFaceletViewHandler(ViewHandler parent) {
		super(parent);
	}

	protected ResponseWriter createResponseWriter(FacesContext context) throws IOException, FacesException {
		if (!JsfRuntimeInformation.isPortletRequest(context)) {
			return super.createResponseWriter(context);
		}
		// Create a temporary ResponseWriter to see what content type the ReponseWriter is going to ask for.
		ResponseWriter writer = createNoopResponseWriter(context);
		RenderResponse response = (RenderResponse) context.getExternalContext().getResponse();
		String contentType = getResponseContentType(context, writer.getContentType());
		String encoding = getResponseEncoding(context, writer.getCharacterEncoding());

		// Set the content type and the encoding and clone writer with the real ResponseWriter
		response.setContentType(contentType + "; charset=" + encoding);
		return writer.cloneWithWriter(response.getWriter());
	}

	private ResponseWriter createNoopResponseWriter(FacesContext context) {
		RenderKit renderKit = context.getRenderKit();
		Assert.notNull(renderKit, context.getViewRoot().getRenderKitId());

		// Append */* to the contentType so createResponseWriter will succeed no matter the requested contentType.
		String contentType = (String) context.getExternalContext().getRequestMap().get(FACELETS_CONTENT_TYPE_KEY);
		if (StringUtils.hasText(contentType) && (!contentType.equals("*/*"))) {
			contentType += ",*/*";
		}

		ResponseWriter writer;
		String encoding = (String) context.getExternalContext().getRequestMap().get(FACELETS_ENCODING_KEY);
		try {
			writer = renderKit.createResponseWriter(NoopWriter.INSTANCE, contentType, encoding);
		} catch (IllegalArgumentException e) {
			// See RI bug prior to 1.2_05-b3. Might as well leave it:
			// https://javaserverfaces.dev.java.net/issues/show_bug.cgi?id=613
			log.fine("The impl didn't correctly handle '*/*' in the content type list.. try '*/*' directly.");
			writer = renderKit.createResponseWriter(NoopWriter.INSTANCE, "*/*", encoding);
		}
		return writer;
	}

	@SuppressWarnings("unchecked")
	protected String getResponseEncoding(FacesContext context, String originalEncoding) {
		String encoding = originalEncoding;

		Map requestMap = context.getExternalContext().getRequestMap();
		Map sessionMap = context.getExternalContext().getSessionMap();

		// 1. check the request attribute
		if (requestMap.containsKey(FACELETS_ENCODING_KEY)) {
			encoding = (String) requestMap.get(FACELETS_ENCODING_KEY);
			sessionMap.put(CHARACTER_ENCODING_KEY, encoding);
		}

		// 2. get it from request
		if (encoding == null) {
			encoding = context.getExternalContext().getResponseCharacterEncoding();
		}

		// 3. get it from the session
		if (encoding == null) {
			encoding = (String) sessionMap.get(CHARACTER_ENCODING_KEY);
		}

		// 4. default it
		if (encoding == null) {
			encoding = "UTF-8";
		}

		return encoding;
	}

	protected static class NoopWriter extends Writer {

		static final NoopWriter INSTANCE = new NoopWriter();

		public void write(char[] buffer) {
		}

		public void write(char[] buffer, int off, int len) {
		}

		public void write(String str) {
		}

		public void write(int c) {
		}

		public void write(String str, int off, int len) {
		}

		public void close() {
		}

		public void flush() {
		}
	}

}
