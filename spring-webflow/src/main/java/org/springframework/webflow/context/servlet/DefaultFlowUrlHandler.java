/*
 * Copyright 2004-2012 the original author or authors.
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

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.core.collection.AttributeMap;

/**
 * The default FlowUrlHandler implementation for Spring Web Flow.
 * <p>
 * Expects URLs to launch flow to be of this pattern:
 * </p>
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/&lt;flow path&gt;
 * </pre>
 * 
 * As an example, the URL <code>http://localhost/springtravel/app/booking</code> would map to flow "booking", while the
 * URL <code>http://localhost/springtravel/app/hotels/booking</code> would map to flow "hotels/booking". In both these
 * examples, /springtravel is the context path and /app is the servlet path. The flow id is treated as the path info
 * component of the request URL string.
 * 
 * If the path info is null, the servletPath will be used as the flow id. Also, if the servlet path ends in an extension
 * it will be stripped when calculating the flow id. For example, a URL of
 * <code>http://localhost/springtravel/hotels/booking.htm</code> would still map to flow id "hotels/booking", assuming a
 * context path of /springtravel, a servlet path of /hotels/booking.htm (likely mapped with a servlet-mapping of *.htm),
 * and a path info of null.
 * <p>
 * Expects URLs to resume flows to be of this pattern:
 * </p>
 * 
 * <pre>
 * http://&lt;host&gt;/[app context path]/[app servlet path]/&lt;flow path&gt;?execution=&lt;flow execution key&gt;
 * </pre>
 * 
 * As an example, the URL http://localhost/springtravel/app/hotels/booking?execution=e1s1 would attempt to resume
 * execution "e1s1" of the "hotels/booking" flow.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class DefaultFlowUrlHandler implements FlowUrlHandler {

	private static final String FLOW_EXECUTION_KEY_PARAMETER = "execution";

	private String encodingScheme;

	/**
	 * Set the character encoding scheme for flow urls. Default is the request's encoding scheme (which is ISO-8859-1 if
	 * not specified otherwise).
	 */
	public void setEncodingScheme(String encodingScheme) {
		this.encodingScheme = encodingScheme;
	}

	public String getFlowExecutionKey(HttpServletRequest request) {
		return request.getParameter(FLOW_EXECUTION_KEY_PARAMETER);
	}

	public String getFlowId(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			return pathInfo.substring(1);
		} else {
			String servletPath = request.getServletPath();
			if (StringUtils.hasText(servletPath)) {
				int dotIndex = servletPath.lastIndexOf('.');
				if (dotIndex != -1) {
					return servletPath.substring(1, dotIndex);
				} else {
					return servletPath.substring(1);
				}
			} else {
				String contextPath = request.getContextPath();
				if (StringUtils.hasText(contextPath)) {
					return request.getContextPath().substring(1);
				} else {
					return null;
				}
			}
		}
	}

	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURI());
		url.append('?');
		appendQueryParameter(url, FLOW_EXECUTION_KEY_PARAMETER, flowExecutionKey, getEncodingScheme(request));
		return url.toString();
	}

	/**
	 * The flow definition URL for the given flow id will be built by appending the flow id to the base app context and
	 * servlet paths.
	 * 
	 * <p>
	 * Example - given a request originating at:
	 * 
	 * <pre>
	 * http://someHost/someApp/someServlet/nestedPath/foo
	 * </pre>
	 * 
	 * and a request for the flow id "nestedPath/bar", the new flow definition URL would be:
	 * 
	 * <pre>
	 * http://someHost/someApp/someServlet/nestedPath/bar
	 * </pre>
	 */
	public String createFlowDefinitionUrl(String flowId, AttributeMap<?> input, HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		if (request.getPathInfo() != null) {
			url.append(request.getContextPath());
			url.append(request.getServletPath());
			url.append('/');
			url.append(flowId);
		} else {
			String servletPath = request.getServletPath();
			if (StringUtils.hasText(servletPath)) {
				url.append(request.getContextPath());
				url.append('/');
				url.append(flowId);
				int dotIndex = servletPath.lastIndexOf('.');
				if (dotIndex != -1) {
					url.append(servletPath.substring(dotIndex));
				}
			} else {
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

	protected String getEncodingScheme(HttpServletRequest request) {
		if (encodingScheme != null) {
			return encodingScheme;
		} else {
			String encodingScheme = request.getCharacterEncoding();
			if (encodingScheme == null) {
				encodingScheme = WebUtils.DEFAULT_CHARACTER_ENCODING;
			}
			return encodingScheme;
		}
	}

	protected <T> void appendQueryParameters(StringBuilder url, Map<String, T> parameters, String encodingScheme) {
		Iterator<Map.Entry<String, T>> entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<?, ?> entry = entries.next();
			appendQueryParameter(url, entry.getKey(), entry.getValue(), encodingScheme);
			if (entries.hasNext()) {
				url.append('&');
			}
		}
	}

	// internal helpers

	private void appendQueryParameter(StringBuilder url, Object key, Object value, String encodingScheme) {
		String encodedKey = encode(key, encodingScheme);
		String encodedValue = encode(value, encodingScheme);
		url.append(encodedKey).append('=').append(encodedValue);
	}

	private String encode(Object value, String encodingScheme) {
		return value != null ? urlEncode(value.toString(), encodingScheme) : "";
	}

	private String urlEncode(String value, String encodingScheme) {
		try {
			return URLEncoder.encode(value, encodingScheme);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Cannot url encode " + value);
		}
	}

}
