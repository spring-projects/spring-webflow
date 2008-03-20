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
package org.springframework.webflow.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.binding.format.factories.DateFormatterFactory;
import org.springframework.binding.format.factories.NumberFormatterFactory;
import org.springframework.binding.format.impl.FormatterRegistryImpl;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.binding.mapping.results.TargetAccessError;
import org.springframework.validation.BindingResult;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;

class MvcView implements View {

	private static final MappingResultsCriteria PROPERTY_NOT_FOUND_ERRORS = new PropertyNotFoundErrors();

	private org.springframework.web.servlet.View view;

	private RequestContext context;

	private ExpressionParser expressionParser = DefaultExpressionParserFactory.getExpressionParser();

	private FormatterRegistry formatterRegistry;

	private MappingResults mappingResults;

	private boolean postbackSuccess;

	private String eventId;

	public MvcView(org.springframework.web.servlet.View view, RequestContext context) {
		this.view = view;
		this.context = context;
		this.formatterRegistry = createDefaultFormatterRegistry();
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	public void setFormatterRegistry(FormatterRegistry formatterRegistry) {
		this.formatterRegistry = formatterRegistry;
	}

	public void render() throws IOException {
		Map model = new HashMap();
		model.putAll(flowScopes());
		exposeBindingModel(model);
		model.put("flowRequestContext", context);
		model.put("flowExecutionKey", context.getFlowExecutionContext().getKey().toString());
		model.put("flowExecutionUrl", context.getFlowExecutionUrl());
		model.put("currentUser", context.getExternalContext().getCurrentUser());
		try {
			view.render(model, (HttpServletRequest) context.getExternalContext().getNativeRequest(),
					(HttpServletResponse) context.getExternalContext().getNativeResponse());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception occurred rendering view " + view, e);
		}
	}

	public void postback() {
		determineEventId(context);
		Object model = getModelObject();
		if (model == null) {
			postbackSuccess = true;
			return;
		}
		if (shouldBind(model)) {
			mappingResults = bind(model);
			if (!mappingResults.hasErrorResults()) {
				postbackSuccess = true;
			} else {
				if (onlyPropertyNotFoundErrors()) {
					postbackSuccess = true;
				} else {
					postbackSuccess = false;
				}
			}
		}
	}

	public boolean eventSignaled() {
		return postbackSuccess && eventId != null;
	}

	public Event getEvent() {
		return new Event(this, eventId, context.getRequestParameters().asAttributeMap());
	}

	protected FormatterRegistry createDefaultFormatterRegistry() {
		FormatterRegistryImpl registry = new FormatterRegistryImpl();
		registry.registerFormatter(new NumberFormatterFactory());
		registry.registerFormatter(new DateFormatterFactory());
		return registry;
	}

	private Map flowScopes() {
		return context.getConversationScope().union(context.getFlowScope()).union(context.getFlashScope()).union(
				context.getRequestScope()).asMap();
	}

	private void exposeBindingModel(Map model) {
		Object modelObject = getModelObject();
		if (modelObject != null) {
			BindingModel bindingModel = new BindingModel(modelObject, expressionParser, formatterRegistry, context
					.getMessageContext());
			bindingModel.setMappingResults(mappingResults);
			model.put(BindingResult.MODEL_KEY_PREFIX + getModelExpression().getExpressionString(), model);
		}
	}

	private Object getModelObject() {
		Expression model = getModelExpression();
		if (model != null) {
			return model.getValue(context);
		} else {
			return null;
		}
	}

	private Expression getModelExpression() {
		return (Expression) context.getCurrentState().getAttributes().get("model");
	}

	private boolean shouldBind(Object model) {
		// this downcast is not ideal
		// TransitionableState currentState = (TransitionableState) context.getCurrentState();
		// TODO this won't work because last event isn't set yet...
		// Transition transition = currentState.getRequiredTransition(context);
		// if (transition.getAttributes().contains("bind")) {
		// return transition.getAttributes().getBoolean("bind").booleanValue();
		// } else {
		// return false;
		// }
		return false;
	}

	private MappingResults bind(Object model) {
		DefaultMapper mapper = new DefaultMapper();
		addDefaultMappings(mapper, context.getRequestParameters(), model);
		return mapper.map(context.getRequestParameters(), model);
	}

	private void addDefaultMappings(DefaultMapper mapper, ParameterMap requestParameters, Object model) {
		for (Iterator it = requestParameters.asMap().keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			Expression source = expressionParser
					.parseExpression(name, new ParserContextImpl().eval(MapAdaptable.class));
			Expression target = expressionParser.parseExpression(name, new ParserContextImpl().eval(model.getClass()));
			mapper.addMapping(new DefaultMapping(source, target));
		}
	}

	private boolean onlyPropertyNotFoundErrors() {
		return mappingResults.getResults(PROPERTY_NOT_FOUND_ERRORS).size() == mappingResults.getErrorResults().size();
	}

	private void determineEventId(RequestContext context) {
		eventId = findParameter("_eventId", context.getRequestParameters());
	}

	/**
	 * Obtain a named parameter from the request parameters. This method will try to obtain a parameter value using the
	 * following algorithm:
	 * <ol>
	 * <li>Try to get the parameter value using just the given <i>logical</i> name. This handles parameters of the
	 * form <tt>logicalName = value</tt>. For normal parameters, e.g. submitted using a hidden HTML form field, this
	 * will return the requested value.</li>
	 * <li>Try to obtain the parameter value from the parameter name, where the parameter name in the request is of the
	 * form <tt>logicalName_value = xyz</tt> with "_" being the configured delimiter. This deals with parameter values
	 * submitted using an HTML form submit button.</li>
	 * <li>If the value obtained in the previous step has a ".x" or ".y" suffix, remove that. This handles cases where
	 * the value was submitted using an HTML form image button. In this case the parameter in the request would actually
	 * be of the form <tt>logicalName_value.x = 123</tt>. </li>
	 * </ol>
	 * @param logicalParameterName the <i>logical</i> name of the request parameter
	 * @param parameters the available parameter map
	 * @return the value of the parameter, or <code>null</code> if the parameter does not exist in given request
	 */
	private String findParameter(String logicalParameterName, ParameterMap parameters) {
		// first try to get it as a normal name=value parameter
		String value = parameters.get(logicalParameterName);
		if (value != null) {
			return value;
		}
		// if no value yet, try to get it as a name_value=xyz parameter
		String prefix = logicalParameterName + "_";
		Iterator paramNames = parameters.asMap().keySet().iterator();
		while (paramNames.hasNext()) {
			String paramName = (String) paramNames.next();
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

	private static class PropertyNotFoundErrors implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.getResult() instanceof TargetAccessError
					&& result.getResult().getErrorCode().equals("propertyNotFound");
		}
	}

}