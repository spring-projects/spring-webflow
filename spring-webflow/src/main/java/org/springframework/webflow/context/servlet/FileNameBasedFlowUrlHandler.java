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
import org.springframework.webflow.mvc.servlet.FlowHandler;

/**
 * 
 * A Web Flow < 2.0.4 compliant {@link FlowUrlHandler} implementation. Will be used by the {@link FlowController}
 * implementation by default. Other custom {@link FlowHandler} implementations should use this implementation to keep
 * the old URL schemes with Web Flow 2.0.4, whereas the last part of the URL is the flow id instead of the whole path.
 * 
 * <p>
 * Expects URLs to launch flow to be of this pattern:
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/[somePath]/&lt;flow id&gt;
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * http://localhost/springtravel/app/booking/booking
 * </pre>
 * 
 * Expects URLs to resume flows to be of this pattern:
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/[somePath]/&lt;flow id&gt;?execution=&lt;flow execution key&gt;
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * http://localhost/springtravel/app/booking/booking?execution=c1v1
 * </pre>
 * 
 * 
 * <strong>Note:</strong> This class is available only for backwards compability of Web Flow 2.0.4 with older Web Flow
 * 2.0 applications. Consider using the new Web Flow 2.0.4 URL ({@link DefaultFlowUrlHandler}) scheme to avoid flow id
 * clashes inside the application.
 * 
 * @author Agim Emruli
 * 
 */
public class FileNameBasedFlowUrlHandler extends DefaultFlowUrlHandler {

	private UrlPathHelper urlPathHelper;

	public FileNameBasedFlowUrlHandler() {
		urlPathHelper = new UrlPathHelper();
	}

	public String getFlowId(HttpServletRequest request) {
		return WebUtils.extractFilenameFromUrlPath(urlPathHelper.getLookupPathForRequest(request));
	}
}