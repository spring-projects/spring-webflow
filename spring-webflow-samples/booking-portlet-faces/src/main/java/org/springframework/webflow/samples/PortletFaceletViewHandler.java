/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.samples;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.portlet.MimeResponse;
import javax.portlet.faces.Bridge;

import com.sun.facelets.FaceletViewHandler;

/**
 * See: http://www.mail-archive.com/users@myfaces.apache.org/msg55213.html
 */
public class PortletFaceletViewHandler extends FaceletViewHandler {

    public PortletFaceletViewHandler(ViewHandler parent) {
	super(new FaceletViewHandler(parent));
    }

    protected ResponseWriter createResponseWriter(FacesContext context) throws IOException, FacesException {
	// Only override if in a portlet request
	if (context.getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
	    return super.createResponseWriter(context);
	}

	ExternalContext extContext = context.getExternalContext();
	RenderKit renderKit = context.getRenderKit();
	// Avoid a cryptic NullPointerException when the renderkit ID
	// is incorrectly set
	if (renderKit == null) {
	    String id = context.getViewRoot().getRenderKitId();
	    throw new IllegalStateException("No render kit was available for id \"" + id + "\"");
	}

	MimeResponse response = (MimeResponse) extContext.getResponse();

	// get our content type
	String contentType = (String) extContext.getRequestMap().get("facelets.ContentType");

	// get the encoding
	String encoding = (String) extContext.getRequestMap().get("facelets.Encoding");

	ResponseWriter writer;
	// append */* to the contentType so createResponseWriter will succeed no matter
	// the requested contentType.
	if (contentType != null && !contentType.equals("*/*")) {
	    contentType += ",*/*";
	}
	// Create a dummy ResponseWriter with a bogus writer,
	// so we can figure out what content type the ReponseWriter
	// is really going to ask for
	try {
	    writer = renderKit.createResponseWriter(NullWriter.Instance, contentType, encoding);
	} catch (IllegalArgumentException e) {
	    // Added because of an RI bug prior to 1.2_05-b3. Might as well leave it in case other impls have the same
	    // problem.
	    // https://javaserverfaces.dev.java.net/issues/show_bug.cgi?id=613
	    log.fine("The impl didn't correctly handled '*/*' in the content type list.  Trying '*/*' directly.");
	    writer = renderKit.createResponseWriter(NullWriter.Instance, "*/*", encoding);
	}

	// Override the JSF provided content type if necessary
	contentType = getResponseContentType(context, writer.getContentType());
	encoding = getResponseEncoding(context, writer.getCharacterEncoding());

	// apply them to the response
	response.setContentType(contentType + "; charset=" + encoding);

	// removed 2005.8.23 to comply with J2EE 1.3
	// response.setCharacterEncoding(encoding);

	// Now, clone with the real writer
	writer = writer.cloneWithWriter(response.getWriter());

	return writer;
    }

    /**
     * Generate the encoding
     * 
     * @param context
     * @param orig
     * @return
     */
    protected String getResponseEncoding(FacesContext context, String orig) {
	String encoding = orig;

	// see if we need to override the encoding
	Map m = context.getExternalContext().getRequestMap();
	Map sm = context.getExternalContext().getSessionMap();

	// 1. check the request attribute
	if (m.containsKey("facelets.Encoding")) {
	    encoding = (String) m.get("facelets.Encoding");
	    if (log.isLoggable(Level.FINEST)) {
		log.finest("Facelet specified alternate encoding '" + encoding + "'");
	    }
	    sm.put(CHARACTER_ENCODING_KEY, encoding);
	}

	// 2. get it from request
	if (encoding == null) {
	    encoding = context.getExternalContext().getResponseCharacterEncoding();
	}

	// 3. get it from the session
	if (encoding == null) {
	    encoding = (String) sm.get(CHARACTER_ENCODING_KEY);
	    if (log.isLoggable(Level.FINEST)) {
		log.finest("Session specified alternate encoding '" + encoding + "'");
	    }
	}

	// 4. default it
	if (encoding == null) {
	    encoding = "UTF-8";
	    if (log.isLoggable(Level.FINEST)) {
		log.finest("ResponseWriter created had a null CharacterEncoding, defaulting to UTF-8");
	    }
	}

	return encoding;
    }

    protected static class NullWriter extends Writer {

	static final NullWriter Instance = new NullWriter();

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