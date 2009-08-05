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

import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.mvc.servlet.FlowController;

/**
 * A file name based {@link FlowUrlHandler} implementation that is an alternative to the standard
 * {@link DefaultFlowUrlHandler}. Treats the filename of a request without the URL suffix and/or prefix as the flow id.
 * Used by the {@link FlowController} implementation as a default implementation to preserve compatibility with existing
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
 * @author Jeremy Grelle
 * @author Nazaret Kazarian
 */
public class FilenameFlowUrlHandler extends DefaultFlowUrlHandler {

	private UrlPathHelper urlPathHelper;

	public FilenameFlowUrlHandler() {
		urlPathHelper = new UrlPathHelper();
	}

	public String getFlowId(HttpServletRequest request) {
		return WebUtils.extractFilenameFromUrlPath(urlPathHelper.getLookupPathForRequest(request));
	}

	/**
	 * The flow definition URL for the given flowId will be inferred from the URL of the current request, re-using the
	 * same path and file extension.
	 * 
	 * <p>
	 * Example - given a request originating at:
	 * 
	 * <pre>
	 * http://someHost/someApp/someServlet/nestedPath/foo.html
	 * </pre>
	 * 
	 * and a request for the flow id "bar", the new flow definition URL would be:
	 * 
	 * <pre>
	 * http://someHost/someApp/someServlet/nestedPath/bar.html
	 * </pre>
	 */
	public String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			url.append(request.getContextPath());
			url.append(request.getServletPath());
			// include the pathInfo part up until the filename
			url.append(pathInfo.substring(0, pathInfo.lastIndexOf("/") + 1));
			url.append(flowId);
			int dotIndex = pathInfo.lastIndexOf('.');
			if (dotIndex != -1) {
				url.append(pathInfo.substring(dotIndex));
			}
		} else {
			String servletPath = request.getServletPath();
			if (StringUtils.hasText(servletPath)) {
				url.append(request.getContextPath());
				// include the servletPath part up to the filename
				int slashIndex = servletPath.lastIndexOf("/");
				if (slashIndex != -1) {
					url.append(servletPath.substring(0, slashIndex));
				}
				url.append('/');
				url.append(flowId);
				int dotIndex = servletPath.lastIndexOf('.');
				if (dotIndex != -1) {
					url.append(servletPath.substring(dotIndex));
				}
			} else {
				// Leaving this for now, as DefaultFlowUrlHandler does the same thing,
				// but this should probably be an error case in the future.
				url.append('/');
				url.append(flowId);
			}
		}
		if (input != null && !input.isEmpty()) {
			url.append('?');
			appendQueryParameters(url, input.asMap(), getEncodingScheme(request));
		}
		return url.toString();
	}
}