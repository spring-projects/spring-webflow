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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * Default {@link AjaxHandler} implementation.
 *
 * <p>Detects Ajax requests through an "Accept" header with the value
 * {@link #AJAX_ACCEPT_CONTENT_TYPE} or a request parameter with the name
 * {@link #AJAX_SOURCE_PARAM}.
 *
 * <p>Also for a redirect during an Ajax request it sets the response headers
 * {@link #REDIRECT_URL_HEADER} and {@link #POPUP_VIEW_HEADER}. The latter is
 * set if the redirect occurs on a view state with popup="true".
 *
 * @author Rossen Stoyanchev
 * @since 2.5
 */
public class DefaultAjaxHandler extends AbstractAjaxHandler {

	/** "Accept" header value that indicates an Ajax request. */
	public static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";

	/** Request parameter alternative that indicate an Ajax request. */
	public static final String AJAX_SOURCE_PARAM = "ajaxSource";

	/** Response header to be set on an Ajax redirect with the redirect location */
	public static final String REDIRECT_URL_HEADER = "Spring-Redirect-URL";

	/** Response header to be set on a redirect that should be issued from a popup window. */
	public static final String POPUP_VIEW_HEADER = "Spring-Modal-View";


	/**
	 * Create a DefaultAjaxHandler that is not part of a chain of AjaxHandler's.
	 */
	public DefaultAjaxHandler() {
		this(null);
	}

	/**
	 * Create a DefaultAjaxHandler as part of a chain of AjaxHandler's.
	 */
	public DefaultAjaxHandler(AbstractAjaxHandler delegate) {
		super(delegate);
	}

	protected boolean isAjaxRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		String header = request.getHeader(HttpHeaders.ACCEPT);
		String param = request.getParameter(AJAX_SOURCE_PARAM);
		return AJAX_ACCEPT_CONTENT_TYPE.equals(header) || StringUtils.hasText(param);
	}

	protected void sendAjaxRedirectInternal(String targetUrl, HttpServletRequest request,
			HttpServletResponse response, boolean popup) {

		if (popup) {
			response.setHeader(POPUP_VIEW_HEADER, "true");
		}
		response.setHeader(REDIRECT_URL_HEADER, response.encodeRedirectURL(targetUrl));
	}

}