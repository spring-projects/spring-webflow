/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.executor.support;

import java.util.Iterator;
import java.util.Map;

import org.springframework.core.style.StylerUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * Default {@link FlowExecutor} argument handler that extracts flow executor
 * method arguments from the {@link ExternalContext#getRequestParameterMap()}
 * and exposes arguments as URL encoded request parameters.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class RequestParameterFlowExecutorArgumentHandler extends FlowExecutorArgumentHandler {

	/**
	 * The default delimiter used when a parameter value is encoded as part of
	 * the name of a parameter, e.g. "_eventId_submit" ("_").
	 * <p>
	 * This form is typically used to support multiple HTML buttons on a form
	 * without resorting to Javascript to communicate the event that corresponds
	 * to a button.
	 */
	private static final String PARAMETER_VALUE_DELIMITER = "_";

	/**
	 * The embedded parameter name/value delimiter, used to parse a parameter
	 * value when a value is embedded in a parameter name (e.g.
	 * "_eventId_submit"). Defaults to {@link #PARAMETER_VALUE_DELIMITER}.
	 */
	private String parameterValueDelimiter = PARAMETER_VALUE_DELIMITER;

	/**
	 * Returns the delimiter used to parse a parameter value when a value is
	 * embedded in a parameter name (e.g. "_eventId_submit"). Defaults to "_".
	 */
	public String getParameterValueDelimiter() {
		return parameterValueDelimiter;
	}

	/**
	 * Set the delimiter used to parse a parameter value when a value is
	 * embedded in a parameter name (e.g. "_eventId_submit").
	 */
	public void setParameterValueDelimiter(String parameterValueDelimiter) {
		this.parameterValueDelimiter = parameterValueDelimiter;
	}

	public boolean isFlowIdPresent(ExternalContext context) {
		return context.getRequestParameterMap().contains(getFlowIdArgumentName());
	}

	public String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String flowId = context.getRequestParameterMap().get(getFlowIdArgumentName());
		flowId = applyDefaultFlowId(flowId);
		if (!StringUtils.hasText(flowId)) {
			throw new FlowExecutorArgumentExtractionException(
					"Unable to extract the flow definition id parameter: make sure the client provides the '"
					+ getFlowIdArgumentName()
					+ "' parameter as input or set the 'defaultFlowId' property; "
					+ "the parameters provided in this request are: "
					+ StylerUtils.style(context.getRequestParameterMap()));
		}
		return flowId;
	}

	public boolean isFlowExecutionKeyPresent(ExternalContext context) {
		return context.getRequestParameterMap().contains(getFlowExecutionKeyArgumentName());
	}

	public String extractFlowExecutionKey(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String encodedKey = context.getRequestParameterMap().get(getFlowExecutionKeyArgumentName());
		if (!StringUtils.hasText(encodedKey)) {
			throw new FlowExecutorArgumentExtractionException(
					"Unable to extract the flow execution key parameter: make sure the client provides the '"
					+ getFlowExecutionKeyArgumentName()
					+ "' parameter as input; the parameters provided in this request are: "
					+ StylerUtils.style(context.getRequestParameterMap()));
		}
		return encodedKey;
	}

	public boolean isEventIdPresent(ExternalContext context) {
		return StringUtils.hasText(findParameter(getEventIdArgumentName(), context.getRequestParameterMap()));
	}

	public String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String eventId = findParameter(getEventIdArgumentName(), context.getRequestParameterMap());
		if (!StringUtils.hasText(eventId)) {
			throw new FlowExecutorArgumentExtractionException(
					"Unable to extract the event id parameter: make sure the client provides the '"
					+ getEventIdArgumentName() + "' parameter as input along with the '"
					+ getFlowExecutionKeyArgumentName()
					+ "' parameter; the parameters provided in this request are: "
					+ StylerUtils.style(context.getRequestParameterMap()));
		}
		return eventId;
	}

	/**
	 * Obtain a named parameter from the request parameters. This method will try
	 * to obtain a parameter value using the following algorithm:
	 * <ol>
	 * <li>Try to get the parameter value using just the given <i>logical</i>
	 * name. This handles parameters of the form <tt>logicalName = value</tt>.
	 * For normal parameters, e.g. submitted using a hidden HTML form field,
	 * this will return the requested value.</li>
	 * <li>Try to obtain the parameter value from the parameter name, where the
	 * parameter name in the request is of the form
	 * <tt>logicalName_value = xyz</tt> with "_" being the configured
	 * delimiter. This deals with parameter values submitted using an HTML form
	 * submit button.</li>
	 * <li>If the value obtained in the previous step has a ".x" or ".y"
	 * suffix, remove that. This handles cases where the value was submitted
	 * using an HTML form image button. In this case the parameter in the request
	 * would actually be of the form <tt>logicalName_value.x = 123</tt>.
	 * </li>
	 * </ol>
	 * @param logicalParameterName the <i>logical</i> name of the request
	 * parameter
	 * @param parameters the available parameter map
	 * @return the value of the parameter, or <code>null</code> if the
	 * parameter does not exist in given request
	 */
	protected String findParameter(String logicalParameterName, ParameterMap parameters) {
		// first try to get it as a normal name=value parameter
		String value = parameters.get(logicalParameterName);
		if (value != null) {
			return value;
		}
		// if no value yet, try to get it as a name_value=xyz parameter
		String prefix = logicalParameterName + getParameterValueDelimiter();
		Iterator paramNames = parameters.asMap().keySet().iterator();
		while (paramNames.hasNext()) {
			String paramName = (String)paramNames.next();
			if (paramName.startsWith(prefix)) {
				String strValue = paramName.substring(prefix.length());
				// support images buttons, which would submit parameters as
				// name_value.x=123
				if (strValue.endsWith(".x") || strValue.endsWith(".y")) {
					strValue = strValue.substring(0, strValue.length() - 2);
				}
				return strValue;
			}
		}
		// we couldn't find the parameter value
		return null;
	}

	public String createFlowDefinitionUrl(FlowDefinitionRedirect flowDefinitionRedirect, ExternalContext context) {
		StringBuffer url = new StringBuffer();
		appendFlowExecutorPath(url, context);
		url.append('?');
		appendQueryParameter(url, getFlowIdArgumentName(), flowDefinitionRedirect.getFlowDefinitionId());
		if (!flowDefinitionRedirect.getExecutionInput().isEmpty()) {
			url.append('&');
		}
		appendQueryParameters(url, flowDefinitionRedirect.getExecutionInput());
		return url.toString();
	}

	public String createFlowExecutionUrl(String flowExecutionKey, FlowExecutionContext flowExecution,
			ExternalContext context) {
		StringBuffer url = new StringBuffer();
		appendFlowExecutorPath(url, context);
		url.append('?');
		appendQueryParameter(url, getFlowExecutionKeyArgumentName(), flowExecutionKey);
		return url.toString();
	}

	public String createExternalUrl(ExternalRedirect redirect, String flowExecutionKey, ExternalContext context) {
		StringBuffer externalUrl = new StringBuffer();
		externalUrl.append(makeRedirectUrlContextRelativeIfNecessary(redirect.getUrl(), context));
		if (flowExecutionKey != null) {
			boolean first = redirect.getUrl().indexOf('?') < 0;
			if (first) {
				externalUrl.append('?');
			}
			else {
				externalUrl.append('&');
			}
			appendQueryParameter(externalUrl, getFlowExecutionKeyArgumentName(), flowExecutionKey);
		}
		return externalUrl.toString();
	}

	// helpers

	/**
	 * Append the URL path to the flow executor capable of accepting new
	 * requests.
	 * @param url the url buffer to append to
	 * @param context the context of this request
	 */
	protected void appendFlowExecutorPath(StringBuffer url, ExternalContext context) {
		url.append(context.getContextPath());
		url.append(context.getDispatcherPath());
		if (context.getRequestPathInfo() != null) {
			url.append(context.getRequestPathInfo());
		}
	}

	/**
	 * Append query parameters to the redirect URL. Stringifies, URL-encodes and
	 * formats model attributes as query parameters.
	 * @param url the StringBuffer to append the parameters to
	 * @param parameters Map that contains attributes
	 */
	protected void appendQueryParameters(StringBuffer url, Map parameters) {
		Iterator entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			appendQueryParameter(url, entry.getKey(), entry.getValue());
			if (entries.hasNext()) {
				url.append('&');
			}
		}
	}

	/**
	 * Appends a single query parameter to a URL.
	 * @param url the target url to append to
	 * @param key the parameter name
	 * @param value the parameter value
	 */
	protected void appendQueryParameter(StringBuffer url, Object key, Object value) {
		String encodedKey = encodeValue(key);
		String encodedValue = encodeValue(value);
		url.append(encodedKey).append('=').append(encodedValue);
	}
}