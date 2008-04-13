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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.core.collection.AttributeMap;

/**
 * The default flow url handler for Spring Web Flow.
 * <p>
 * Expects URLs to launch flow to be of this pattern:
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/[namespace]/&lt;flow id&gt;
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * http://localhost/springtravel/app/booking
 * </pre>
 * 
 * Expects URLs to resume flows to be of this pattern:
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/[namespace]/&lt;flow id&gt;?execution=&lt;flow execution key&gt;
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * http://localhost/springtravel/app/booking?execution=c1v1
 * </pre>
 * 
 * @author Keith Donald
 * 
 */
public class DefaultFlowUrlHandler implements FlowUrlHandler {

	private static final String DEFAULT_URL_ENCODING_SCHEME = "UTF-8";

	private String urlEncodingScheme = DEFAULT_URL_ENCODING_SCHEME;

	private UrlPathHelper urlPathHelper;

	public DefaultFlowUrlHandler() {
		urlPathHelper = new UrlPathHelper();
	}

	public String getFlowExecutionKey(HttpServletRequest request) {
		return request.getParameter("execution");
	}

	public String getFlowId(HttpServletRequest request) {
		return WebUtils.extractFilenameFromUrlPath(urlPathHelper.getLookupPathForRequest(request));
	}

	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(request.getRequestURI());
		url.append('?');
		appendQueryParameter(url, "execution", flowExecutionKey);
		return url.toString();
	}

	public String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(getFlowHandlerUri(request));
		url.append('/');
		url.append(encode(flowId));
		if (input != null && !input.isEmpty()) {
			url.append('?');
			appendQueryParameters(url, input.asMap());
		}
		return url.toString();
	}

	private void appendQueryParameters(StringBuffer url, Map parameters) {
		Iterator entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			appendQueryParameter(url, entry.getKey(), entry.getValue());
			if (entries.hasNext()) {
				url.append('&');
			}
		}
	}

	private void appendQueryParameter(StringBuffer url, Object key, Object value) {
		String encodedKey = encode(key);
		String encodedValue = encode(value);
		url.append(encodedKey).append('=').append(encodedValue);
	}

	private String encode(Object value) {
		return value != null ? urlEncode(String.valueOf(value)) : "";
	}

	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(String.valueOf(value), urlEncodingScheme);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Cannot url encode " + value);
		}
	}

	private String getFlowHandlerUri(HttpServletRequest request) {
		String flowRequestUri = request.getRequestURI();
		int lastSlash = flowRequestUri.lastIndexOf('/');
		return flowRequestUri.substring(0, lastSlash);
	}

}
