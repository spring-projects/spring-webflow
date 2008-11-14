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
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.validation.ValidationHelper;

/**
 * Base view implementation for the Spring Web MVC Servlet and Spring Web MVC Portlet frameworks.
 * 
 * @author Keith Donald
 */
public abstract class AbstractMvcView implements View {

	private static final Log logger = LogFactory.getLog(AbstractMvcView.class);

	private static final MappingResultsCriteria PROPERTY_NOT_FOUND_ERROR = new PropertyNotFoundError();

	private static final MappingResultsCriteria MAPPING_ERROR = new MappingError();

	private org.springframework.web.servlet.View view;

	private RequestContext requestContext;

	private ExpressionParser expressionParser;

	private ConversionService conversionService;

	private String fieldMarkerPrefix = "_";

	private String eventIdParameterName = "_eventId";

	private String eventId;

	private MappingResults mappingResults;

	private BinderConfiguration binderConfiguration;

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
	 * Sets the service to use to expose formatters for field values.
	 * @param conversionService the conversion service
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Sets the configuration describing how this view should bind to its model to access data for rendering.
	 * @param binderConfiguration the model binder configuration
	 */
	public void setBinderConfiguration(BinderConfiguration binderConfiguration) {
		this.binderConfiguration = binderConfiguration;
	}

	/**
	 * Specify a prefix that can be used for parameters that mark potentially empty fields, having "prefix + field" as
	 * name. Such a marker parameter is checked by existence: You can send any value for it, for example "visible". This
	 * is particularly useful for HTML checkboxes and select options.
	 * <p>
	 * Default is "_", for "_FIELD" parameters (e.g. "_subscribeToNewsletter"). Set this to null if you want to turn off
	 * the empty field check completely.
	 * <p>
	 * HTML checkboxes only send a value when they're checked, so it is not possible to detect that a formerly checked
	 * box has just been unchecked, at least not with standard HTML means.
	 * <p>
	 * This auto-reset mechanism addresses this deficiency, provided that a marker parameter is sent for each checkbox
	 * field, like "_subscribeToNewsletter" for a "subscribeToNewsletter" field. As the marker parameter is sent in any
	 * case, the data binder can detect an empty field and automatically reset its value.
	 */
	public void setFieldMarkerPrefix(String fieldMarkerPrefix) {
		this.fieldMarkerPrefix = fieldMarkerPrefix;
	}

	/**
	 * Sets the name of the request parameter to use to lookup user events signaled by this view. If not specified, the
	 * default is <code>_eventId</code>
	 * @param eventIdParameterName the event id parameter name
	 */
	public void setEventIdParameterName(String eventIdParameterName) {
		this.eventIdParameterName = eventIdParameterName;
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
			if (logger.isDebugEnabled()) {
				logger.debug("Rendering MVC [" + view + "] with model map [" + model + "]");
			}
			doRender(model);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			IllegalStateException ise = new IllegalStateException("Exception occurred rendering view " + view);
			ise.initCause(e);
			throw ise;
		}
	}

	public void processUserEvent() {
		eventId = determineEventId(requestContext);
		if (eventId == null) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("User event '" + eventId + "' raised");
		}
		Object model = getModelObject();
		if (model == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No model to work with; done processing user event");
			}
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Resolved model " + model);
		}
		TransitionDefinition transition = requestContext.getMatchingTransition(eventId);
		if (shouldBind(model, transition)) {
			mappingResults = bind(model);
			if (hasErrors(mappingResults)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Model binding resulted in errors; adding error messages to context");
				}
				addErrorMessages(mappingResults);
			}
			if (shouldValidate(model, transition)) {
				validate(model);
			}
		}
		requestContext.getFlashScope().put(ViewActionStateHolder.KEY,
				new ViewActionStateHolder(eventId, mappingResults));
	}

	public boolean hasFlowEvent() {
		return eventId != null && !requestContext.getMessageContext().hasErrorMessages();
	}

	public Event getFlowEvent() {
		if (!hasFlowEvent()) {
			return null;
		}
		return new Event(this, eventId, requestContext.getRequestParameters().asAttributeMap());
	}

	public String toString() {
		return new ToStringCreator(this).append("view", view).toString();
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

	/**
	 * Obtain the user event from the current flow request. The default implementation returns the value of the request
	 * parameter with name {@link #setEventIdParameterName(String) eventIdParameterName}. Subclasses may override.
	 * @param context the current flow request context
	 * @return the user event that occurred
	 */
	protected String determineEventId(RequestContext context) {
		return WebUtils.findParameterValue(context.getRequestParameters().asMap(), eventIdParameterName);
	}

	/**
	 * Returns the id of the user event being processed.
	 * @return the user event
	 */
	protected String getEventId() {
		return eventId;
	}

	/**
	 * Determines if model data binding should be invoked given the Transition that matched the current user event being
	 * processed. Returns true unless the <code>bind</code> attribute of the Transition has been set to false.
	 * Subclasses may override.
	 * @param model the model data binding would be performed on
	 * @param transition the matched transition
	 * @return true if binding should occur, false if not
	 */
	protected boolean shouldBind(Object model, TransitionDefinition transition) {
		if (transition == null) {
			return true;
		}
		return transition.getAttributes().getBoolean("bind", Boolean.TRUE).booleanValue();
	}

	/**
	 * Returns the results of binding to the view's model, if model binding has occurred.
	 * @return the binding (mapping) results
	 */
	protected MappingResults getMappingResults() {
		return mappingResults;
	}

	// package private

	/**
	 * Restores the internal state of this view from the provided state holder.
	 * @see AbstractMvcViewFactory#getView(RequestContext)
	 */
	void restoreState(ViewActionStateHolder stateHolder) {
		eventId = stateHolder.getEventId();
		mappingResults = stateHolder.getMappingResults();
	}

	/**
	 * Determines if model validation should execute given the Transition that matched the current user event being
	 * processed. Returns true unless the <code>validate</code> attribute of the Transition has been set to false, or
	 * model data binding errors occurred and the global <code>validateOnBindingErrors</code> flag is set to false.
	 * Subclasses may override.
	 * @param model the model data binding would be performed on
	 * @param transition the matched transition
	 * @return true if binding should occur, false if not
	 */
	private boolean shouldValidate(Object model, TransitionDefinition transition) {
		Boolean validateAttribute = getValidateAttribute(transition);
		if (validateAttribute != null) {
			return validateAttribute.booleanValue();
		} else {
			AttributeMap flowExecutionAttributes = requestContext.getFlowExecutionContext().getAttributes();
			Boolean validateOnBindingErrors = flowExecutionAttributes.getBoolean("validateOnBindingErrors");
			if (validateOnBindingErrors != null) {
				if (!validateOnBindingErrors.booleanValue() && mappingResults.hasErrorResults()) {
					return false;
				}
			}
			return true;
		}
	}

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
					expressionParser, conversionService, requestContext.getMessageContext());
			bindingModel.setBinderConfiguration(binderConfiguration);
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

	private MappingResults bind(Object model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Binding to model");
		}
		DefaultMapper mapper = new DefaultMapper();
		ParameterMap requestParameters = requestContext.getRequestParameters();
		if (binderConfiguration != null) {
			addModelBindings(mapper, requestParameters.asMap().keySet(), model);
		} else {
			addDefaultMappings(mapper, requestParameters.asMap().keySet(), model);
		}
		return mapper.map(requestParameters, model);
	}

	private void addModelBindings(DefaultMapper mapper, Set parameterNames, Object model) {
		Iterator it = binderConfiguration.getBindings().iterator();
		while (it.hasNext()) {
			Binding binding = (Binding) it.next();
			String parameterName = binding.getProperty();
			if (parameterNames.contains(parameterName)) {
				addMapping(mapper, binding, model);
			} else {
				if (fieldMarkerPrefix != null && parameterNames.contains(fieldMarkerPrefix + parameterName)) {
					addEmptyValueMapping(mapper, parameterName, model);
				}
			}
		}
	}

	private void addMapping(DefaultMapper mapper, Binding binding, Object model) {
		Expression source = new RequestParameterExpression(binding.getProperty());
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(binding.getProperty(), parserContext);
		DefaultMapping mapping = new DefaultMapping(source, target);
		mapping.setRequired(binding.getRequired());
		if (binding.getConverter() != null) {
			ConversionExecutor conversionExecutor = conversionService.getConversionExecutor(binding.getConverter(),
					String.class, target.getValueType(model));
			mapping.setTypeConverter(conversionExecutor);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding mapping for parameter '" + binding.getProperty() + "'");
		}
		mapper.addMapping(mapping);
	}

	private void addDefaultMappings(DefaultMapper mapper, Set parameterNames, Object model) {
		for (Iterator it = parameterNames.iterator(); it.hasNext();) {
			String parameterName = (String) it.next();
			if (fieldMarkerPrefix != null && parameterName.startsWith(fieldMarkerPrefix)) {
				String field = parameterName.substring(fieldMarkerPrefix.length());
				if (!parameterNames.contains(field)) {
					addEmptyValueMapping(mapper, field, model);
				}
			} else {
				addDefaultMapping(mapper, parameterName, model);
			}
		}
	}

	private void addEmptyValueMapping(DefaultMapper mapper, String field, Object model) {
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(field, parserContext);
		try {
			Class propertyType = target.getValueType(model);
			Expression source = new StaticExpression(getEmptyValue(propertyType));
			DefaultMapping mapping = new DefaultMapping(source, target);
			if (logger.isDebugEnabled()) {
				logger.debug("Adding empty value mapping for parameter '" + field + "'");
			}
			mapper.addMapping(mapping);
		} catch (EvaluationException e) {

		}
	}

	private Object getEmptyValue(Class fieldType) {
		if (fieldType != null && boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
			// Special handling of boolean property.
			return Boolean.FALSE;
		} else if (fieldType != null && fieldType.isArray()) {
			// Special handling of array property.
			return Array.newInstance(fieldType.getComponentType(), 0);
		} else {
			// Default value: try null.
			return null;
		}
	}

	private void addDefaultMapping(DefaultMapper mapper, String parameter, Object model) {
		Expression source = new RequestParameterExpression(parameter);
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(parameter, parserContext);
		DefaultMapping mapping = new DefaultMapping(source, target);
		if (logger.isDebugEnabled()) {
			logger.debug("Adding default mapping for parameter '" + parameter + "'");
		}
		mapper.addMapping(mapping);
	}

	private boolean hasErrors(MappingResults results) {
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
		String errorCode = error.getCode();
		String propertyErrorCode = new StringBuffer().append(getModelExpression().getExpressionString()).append('.')
				.append(field).append('.').append(errorCode).toString();
		return new MessageBuilder().error().source(field).code(propertyErrorCode).code(errorCode).resolvableArg(field)
				.defaultText(errorCode + " on " + field).build();
	}

	private Boolean getValidateAttribute(TransitionDefinition transition) {
		if (transition != null) {
			return transition.getAttributes().getBoolean("validate");
		} else {
			return null;
		}
	}

	private void validate(Object model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Validating model");
		}
		new ValidationHelper(model, requestContext, eventId, getModelExpression().getExpressionString(),
				expressionParser, mappingResults).validate();
	}

	private static class PropertyNotFoundError implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.isError() && "propertyNotFound".equals(result.getCode());
		}
	}

	private static class MappingError implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.isError() && !PROPERTY_NOT_FOUND_ERROR.test(result);
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
			ParameterMap parameters = (ParameterMap) context;
			return parameters.asMap().get(parameterName);
		}

		public Class getValueType(Object context) {
			return String.class;
		}

		public void setValue(Object context, Object value) throws EvaluationException {
			throw new UnsupportedOperationException("Setting request parameters is not allowed");
		}

		public String toString() {
			return "parameter:'" + parameterName + "'";
		}
	}

}