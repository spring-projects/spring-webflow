package org.springframework.webflow.context.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.webflow.core.collection.AttributeMap;

public class ParameterBasedFlowUrlHandler implements FlowUrlHandler {

	private static final String DEFAULT_URL_ENCODING_SCHEME = "UTF-8";

	private String urlEncodingScheme = DEFAULT_URL_ENCODING_SCHEME;

	public String getFlowExecutionKey(HttpServletRequest request) {
		return request.getParameter("_flowExecutionKey");
	}

	public String getFlowId(HttpServletRequest request) {
		return request.getParameter("_flowId");
	}

	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(request.getContextPath());
		url.append(request.getServletPath());
		if (request.getPathInfo() != null) {
			url.append(request.getPathInfo());
		}
		url.append('?');
		appendQueryParameter(url, "_flowId", flowId);
		url.append('&');
		appendQueryParameter(url, "_flowExecutionKey", flowExecutionKey);
		return url.toString();
	}

	public String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(request.getContextPath());
		url.append(request.getServletPath());
		if (request.getPathInfo() != null) {
			url.append(request.getPathInfo());
		}
		url.append('?');
		appendQueryParameter(url, "_flowId", flowId);
		if (input != null && !input.isEmpty()) {
			url.append('&');
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

}
