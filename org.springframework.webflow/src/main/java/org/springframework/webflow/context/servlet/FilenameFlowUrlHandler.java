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
package org.springframework.webflow.context.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.mvc.servlet.FlowController;

/**
 * A file name based {@link FlowUrlHandler} implementation that is an alternative to the standard
 * {@link DefaultFlowUrlHandler}. Treats the filename of a request without the URL suffix and/or prefix as the flow id.
 * Used by the {@link FlowController} implementation as a default implementation to preserve compability with existing
 * Web Flow 2 applications.
 * 
 * <p>
 * This implementation extracts the filename and removes the file extension from the request URL. The results will be
 * used as the flow Id that must be unique throughout the application.
 * 
 * For example the URLs
 * 
 * <pre>
 * 	http://someHost/someApp/someServlet/foo
 * 	http://someHost/someApp/someServlet/nestedPath/foo
 * 	http://someHost/someApp/someServlet/nestedPath/foo.html
 * </pre>
 * 
 * will all treat the filename "foo" as the flow id.
 * </p>
 * 
 * <strong>Note:</strong> Because this class only treats a filename as a flow id, clashes can result. For example:
 * 
 * <pre>
 * http://localhost/springtravel/app/hotel/booking
 * http://localhost/springtravel/app/flight/booking
 * </pre>
 * 
 * would both map the same flow id "booking", instead of "hotel/booking" and "flight/booking". This is an limitation of
 * this implementation. Consider using the standard {@link DefaultFlowUrlHandler} that uses the request URL prefix as
 * well to avoid these clashes.
 * 
 * @author Agim Emruli
 */
public class FilenameFlowUrlHandler extends DefaultFlowUrlHandler {

	private UrlPathHelper urlPathHelper;

	public FilenameFlowUrlHandler() {
		urlPathHelper = new UrlPathHelper();
	}

	public String getFlowId(HttpServletRequest request) {
		return WebUtils.extractFilenameFromUrlPath(urlPathHelper.getLookupPathForRequest(request));
	}
}