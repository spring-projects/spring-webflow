/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.context.servlet;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Strategy interface that encapsulates knowledge about a client-side ajax system and how to communicate with that
 * system.
 * 
 * @author Keith Donald
 */
public interface AjaxHandler {

	/**
	 * Is the current request an Ajax request?
	 * @param request the current request
	 * @param response the current response
	 */
	boolean isAjaxRequest(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Send a redirect request to the Ajax client. This should cause the client-side agent to send a new request to the
	 * specified target url.
	 * @param request the current request
	 * @param response the current response
	 * @param targetUrl the target url to redirect to
	 * @param popup whether the redirect should be sent from a new popup dialog window
	 */
	void sendAjaxRedirect(String targetUrl, HttpServletRequest request, HttpServletResponse response,
			boolean popup) throws IOException;
}
