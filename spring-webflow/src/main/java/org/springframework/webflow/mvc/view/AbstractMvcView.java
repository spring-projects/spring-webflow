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
package org.springframework.webflow.mvc.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.binding.mapping.impl.DefaultMappingContext;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageContextErrors;
import org.springframework.binding.message.MessageResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.definition.TransitionableStateDefinition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;

/**
 * Base view implementation for the Spring Web MVC Servlet and Spring Web MVC Portlet frameworks.
 * 
 * @author Keith Donald
 */
public abstract class AbstractMvcView implements View {

	private static final MappingResultsCriteria PROPERTY_NOT_FOUND_ERROR = new PropertyNotFoundError();

	private static final MappingResultsCriteria MAPPING_ERROR = new MappingError();

	private org.springframework.web.servlet.View view;

	private RequestContext requestContext;

	private ExpressionParser expressionParser = DefaultExpressionParserFactory.getExpressionParser();

	private FormatterRegistry formatterRegistry;

	private MappingResults mappingResults;

	private boolean viewErrors;

	private String eventId;

	/**
	 * Creates a new MVC view.
	 * @param view the Spring MVC view to render
	 * @param requestContext the current flow request context
	 */
	public AbstractMvcView(org.springframework.web.servlet.View view, RequestContext requestContext) {
		this.view = view;
		this.requestContext = requestContext;
	}

	/**
	 * Sets the expression parser to use to parse model expressions.
	 * @param expressionParser the expression parser
	 */
	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	/**
	 * Sets the formatter registry to use to expose formatters for field values.
	 * @param formatterRegistry the formatter registry
	 */
	public void setFormatterRegistry(FormatterRegistry formatterRegistry) {
		this.formatterRegistry = formatterRegistry;
	}

	public void render() throws IOException {
		Map model = new HashMap();
		model.putAll(flowScopes());
		exposeBindingModel(model);
		model.put("flowRequestContext", requestContext);
		FlowExecutionKey key = requestContext.getFlowExecutionContext().getKey();
		if (key != null) {
			model.put("flowExecutionKey", requestContext.getFlowExecutionContext().getKey().toString());
			model.put("flowExecutionUrl", requestContext.getFlowExecutionUrl());
		}
		model.put("currentUser", requestContext.getExternalContext().getCurrentUser());
		try {
			doRender(model);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected exception occurred rendering view " + view, e);
		}
	}

	public void processUserEvent() {
		determineEventId(requestContext);
		if (eventId == null) {
			return;
		}
		Object model = getModelObject();
		if (model == null) {
			return;
		}
		if (shouldBind(model)) {
			mappingResults = bind(model);
			if (hasMappingErrors(mappingResults)) {
				viewErrors = true;
				addErrorMessages(mappingResults);
			} else {
				validate(model);
				if (requestContext.getMessageContext().hasErrorMessages()) {
					viewErrors = true;
				}
			}
		}
	}

	public boolean hasFlowEvent() {
		return eventId != null && !viewErrors;
	}

	public Event getFlowEvent() {
		if (!hasFlowEvent()) {
			return null;
		}
		return new Event(this, eventId, requestContext.getRequestParameters().asAttributeMap());
	}

	// subclassing hooks

	/**
	 * Returns the current flow request context.
	 * @return the flow request context
	 */
	protected RequestContext getRequestContext() {
		return requestContext;
	}

	/**
	 * Returns the Spring MVC view to render
	 * @return the view
	 */
	protected org.springframework.web.servlet.View getView() {
		return view;
	}

	/**
	 * Template method subclasses should override to execute the view rendering logic.
	 * @param model the view model data
	 * @throws Exception an exception occurred rendering the view
	 */
	protected abstract void doRender(Map model) throws Exception;

	// internal helpers

	private Map flowScopes() {
		if (requestContext.getCurrentState().isViewState()) {
			return requestContext.getConversationScope().union(requestContext.getFlowScope()).union(
					requestContext.getViewScope()).union(requestContext.getFlashScope()).union(
					requestContext.getRequestScope()).asMap();
		} else {
			return requestContext.getConversationScope().union(requestContext.getFlowScope()).union(
					requestContext.getFlashScope()).union(requestContext.getRequestScope()).asMap();
		}
	}

	private void exposeBindingModel(Map model) {
		Object modelObject = getModelObject();
		if (modelObject != null) {
			BindingModel bindingModel = new BindingModel(getModelExpression().getExpressionString(), modelObject,
					expressionParser, formatterRegistry, requestContext.getMessageContext());
			bindingModel.setMappingResults(mappingResults);
			model.put(BindingResult.MODEL_KEY_PREFIX + getModelExpression().getExpressionString(), bindingModel);
		}
	}

	private Object getModelObject() {
		Expression model = getModelExpression();
		if (model != null) {
			return model.getValue(requestContext);
		} else {
			return null;
		}
	}

	private Expression getModelExpression() {
		return (Expression) requestContext.getCurrentState().getAttributes().get("model");
	}

	private boolean shouldBind(Object model) {
		TransitionableStateDefinition currentState = (TransitionableStateDefinition) requestContext.getCurrentState();
		TransitionDefinition transition = currentState.getTransition(eventId);
		if (transition != null) {
			if (transition.getAttributes().contains("bind")) {
				return transition.getAttributes().getBoolean("bind").booleanValue();
			}
		}
		return true;
	}

	private MappingResults bind(Object model) {
		DefaultMapper mapper = new DefaultMapper();
		addDefaultMappings(mapper, requestContext.getRequestParameters(), model);
		return mapper.map(requestContext.getRequestParameters(), model);
	}

	private void addDefaultMappings(DefaultMapper mapper, ParameterMap requestParameters, Object model) {
		for (Iterator it = requestParameters.asMap().keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			Expression source = new RequestParameterExpression(name);
			Expression target = expressionParser.parseExpression(name, new FluentParserContext().evaluate(model
					.getClass()));
			DefaultMapping mapping = new DefaultMapping(source, target);
			mapping.setTypeConverter(new FormatterBackedMappingConversionExecutor(formatterRegistry));
			mapper.addMapping(mapping);
		}
	}

	private boolean hasMappingErrors(MappingResults results) {
		return results.hasErrorResults() && !onlyPropertyNotFoundErrorsPresent(results);
	}

	private boolean onlyPropertyNotFoundErrorsPresent(MappingResults results) {
		return results.getResults(PROPERTY_NOT_FOUND_ERROR).size() == mappingResults.getErrorResults().size();
	}

	private void addErrorMessages(MappingResults results) {
		List errors = results.getResults(MAPPING_ERROR);
		for (Iterator it = errors.iterator(); it.hasNext();) {
			MappingResult error = (MappingResult) it.next();
			requestContext.getMessageContext().addMessage(createMessageResolver(error));
		}
	}

	private MessageResolver createMessageResolver(MappingResult error) {
		String field = error.getMapping().getTargetExpression().getExpressionString();
		String errorCode = error.getResult().getErrorCode();
		String propertyErrorCode = new StringBuffer().append(getModelExpression().getExpressionString()).append('.')
				.append(field).append('.').append(errorCode).toString();
		return new MessageBuilder().error().source(field).code(propertyErrorCode).code(errorCode).resolvableArg(field)
				.defaultText(errorCode + " on " + field).build();
	}

	private void validate(Object model) {
		String validateMethodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), validateMethodName,
				new Class[] { MessageContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { requestContext.getMessageContext() });
		}
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		if (beanFactory != null) {
			String validatorName = getModelExpression().getExpressionString() + "Validator";
			if (beanFactory.containsBean(validatorName)) {
				Object validator = beanFactory.getBean(validatorName);
				validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
						model.getClass(), MessageContext.class });
				if (validateMethod != null) {
					ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
							requestContext.getMessageContext() });
				} else {
					validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
							model.getClass(), Errors.class });
					if (validateMethod != null) {
						ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
								new MessageContextErrors(requestContext.getMessageContext()) });
					}
				}
			}
		}
	}

	private void determineEventId(RequestContext context) {
		eventId = WebUtils.findParameterValue(context.getRequestParameters().asMap(), "_eventId");
	}

	private static class PropertyNotFoundError implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.getResult().isError() && "propertyNotFound".equals(result.getResult().getErrorCode());
		}
	}

	private static class MappingError implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.getResult().isError() && !PROPERTY_NOT_FOUND_ERROR.test(result);
		}
	}

	private static class RequestParameterExpression implements Expression {

		private String parameterName;

		public RequestParameterExpression(String parameterName) {
			this.parameterName = parameterName;
		}

		public String getExpressionString() {
			return parameterName;
		}

		public Object getValue(Object context) throws EvaluationException {
			ParameterMap map = (ParameterMap) context;
			return map.get(parameterName);
		}

		public Class getValueType(Object context) {
			return String.class;
		}

		public void setValue(Object context, Object value) throws EvaluationException {
			throw new UnsupportedOperationException("Setting request parameters is not allowed");
		}

	}

	private static class FormatterBackedMappingConversionExecutor implements ConversionExecutor {

		private FormatterRegistry formatterRegistry;

		public FormatterBackedMappingConversionExecutor(FormatterRegistry formatterRegistry) {
			this.formatterRegistry = formatterRegistry;
		}

		public Object execute(Object source) throws ConversionExecutionException {
			throw new UnsupportedOperationException("Should never be called");
		}

		public Object execute(Object source, Object context) throws ConversionExecutionException {
			String formattedValue = (String) source;
			DefaultMappingContext mappingContext = (DefaultMappingContext) context;
			Expression target = mappingContext.getCurrentMapping().getTargetExpression();
			Class targetClass = getTargetClass();
			if (targetClass == null) {
				try {
					targetClass = target.getValueType(mappingContext.getTarget());
				} catch (EvaluationException e) {
					// ignore
				}
			}
			if (targetClass == null) {
				return formattedValue;
			}
			Formatter formatter = getFormatter(target, targetClass);
			if (formatter != null) {
				try {
					return formatter.parse(formattedValue);
				} catch (InvalidFormatException e) {
					throw new ConversionExecutionException(formattedValue, String.class, targetClass, e);
				}
			} else {
				return formattedValue;
			}
		}

		private Formatter getFormatter(Expression target, Class targetClass) {
			if (formatterRegistry != null) {
				Formatter formatter = formatterRegistry.getFormatter(target.getExpressionString());
				if (formatter != null) {
					return formatter;
				} else {
					return formatterRegistry.getFormatter(targetClass);
				}
			} else {
				return null;
			}
		}

		public Class getSourceClass() {
			return String.class;
		}

		public Class getTargetClass() {
			return null;
		}

	}

}