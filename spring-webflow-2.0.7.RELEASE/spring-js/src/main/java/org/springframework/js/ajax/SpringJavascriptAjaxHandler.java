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
package org.springframework.js.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

/**
 * Ajax handler for Spring Javascript (Spring.js).
 * 
 * @author Jeremy Grelle
 * @author Keith Donald
 */
public class SpringJavascriptAjaxHandler implements AjaxHandler {

	/**
	 * The response header to be set on an Ajax redirect
	 */
	public static final String REDIRECT_URL_HEADER = "Spring-Redirect-URL";

	/**
	 * The response header to be set on an redirect that should be issued from a popup window.
	 */
	public static final String POPUP_VIEW_HEADER = "Spring-Modal-View";

	/**
	 * The accept header value that signifies an Ajax request.
	 */
	public static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";

	/**
	 * Alternate request parameter to indicate an Ajax request for cases when control of the header is not available.
	 */
	public static final String AJAX_SOURCE_PARAM = "ajaxSource";

	public boolean isAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
		String acceptHeader = request.getHeader("Accept");
		String ajaxParam = request.getParameter(AJAX_SOURCE_PARAM);
		if (AJAX_ACCEPT_CONTENT_TYPE.equals(acceptHeader) || StringUtils.hasText(ajaxParam)) {
			return true;
		} else {
			return false;
		}
	}

	public void sendAjaxRedirect(String targetUrl, HttpServletRequest request, HttpServletResponse response,
			boolean popup) throws IOException {
		if (popup) {
			response.setHeader(POPUP_VIEW_HEADER, "true");
		}
		response.setHeader(REDIRECT_URL_HEADER, response.encodeRedirectURL(targetUrl));
	}

}