/*
 * Copyright 2004-2014 the original author or authors.
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
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
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
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
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
import org.springframework.webflow.validation.BeanValidationHintResolver;
import org.springframework.webflow.validation.ValidationHelper;
import org.springframework.webflow.validation.ValidationHintResolver;

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

	private Validator validator;

	private String fieldMarkerPrefix = "_";

	private String eventIdParameterName = "_eventId";

	private String eventId;

	private MappingResults mappingResults;

	private BinderConfiguration binderConfiguration;

	private MessageCodesResolver messageCodesResolver;

	private boolean userEventProcessed;

	private ValidationHintResolver validationHintResolver = new BeanValidationHintResolver();

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

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setValidationHintResolver(ValidationHintResolver validationHintResolver) {
		if (validationHintResolver != null) {
			this.validationHintResolver = validationHintResolver;
		}
	}

	/**
	 * Sets the configuration describing how this view should bind to its model to access data for rendering.
	 * @param binderConfiguration the model binder configuration
	 */
	public void setBinderConfiguration(BinderConfiguration binderConfiguration) {
		this.binderConfiguration = binderConfiguration;
	}

	/**
	 * Set the message codes resolver to use to resolve bind and validation failure message codes.
	 * @param messageCodesResolver the binding error message code resolver to use
	 */
	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
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
		Map<String, Object> model = new HashMap<String, Object>();
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

	public boolean userEventQueued() {
		return !userEventProcessed && getEventId() != null;
	}

	public void processUserEvent() {
		String eventId = getEventId();
		if (eventId == null) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Processing user event '" + eventId + "'");
		}
		Object model = getModelObject();
		if (model != null) {
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
					validate(model, transition);
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No model to bind to; done processing user event");
			}
		}
		userEventProcessed = true;
	}

	public Serializable getUserEventState() {
		return new ViewActionStateHolder(eventId, userEventProcessed, mappingResults);
	}

	public boolean hasFlowEvent() {
		return userEventProcessed && !requestContext.getMessageContext().hasErrorMessages();
	}

	public Event getFlowEvent() {
		if (!hasFlowEvent()) {
			return null;
		}
		return new Event(this, getEventId(), requestContext.getRequestParameters().asAttributeMap());
	}

	public void saveState() {

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
	 * @return the configured ConversionService
	 */
	protected ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Template method subclasses should override to execute the view rendering logic.
	 * @param model the view model data
	 * @throws Exception an exception occurred rendering the view
	 */
	protected abstract void doRender(Map<String, ?> model) throws Exception;

	/**
	 * Returns the id of the user event being processed.
	 * @return the user event
	 */
	protected String getEventId() {
		if (eventId == null) {
			eventId = determineEventId(requestContext);
		}
		return this.eventId;
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
		return transition.getAttributes().getBoolean("bind", true);
	}

	/**
	 * Returns the results of binding to the view's model, if model binding has occurred.
	 * @return the binding (mapping) results
	 */
	protected MappingResults getMappingResults() {
		return mappingResults;
	}

	/**
	 * Returns the binding configuration that defines how to connect properties of the model to UI elements.
	 * @return an instance of {@link BinderConfiguration} or null.
	 */
	protected BinderConfiguration getBinderConfiguration() {
		return binderConfiguration;
	}

	/**
	 * Returns the EL parser to be used for data binding purposes.
	 * @return an instance of {@link ExpressionParser}.
	 */
	protected ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	/**
	 * Returns the prefix that can be used for parameters that mark potentially empty fields.
	 * @return the prefix value.
	 */
	protected String getFieldMarkerPrefix() {
		return fieldMarkerPrefix;
	}

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
	 * <p>
	 * Causes the model to be populated from information contained in request parameters.
	 * </p>
	 * <p>
	 * If a view has binding configuration then only model fields specified in the binding configuration will be
	 * considered. In the absence of binding configuration all request parameters will be used to update matching fields
	 * on the model.
	 * </p>
	 *
	 * @param model the model to be updated
	 * @return an instance of MappingResults with information about the results of the binding.
	 */
	protected MappingResults bind(Object model) {
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

	/**
	 * <p>
	 * Adds a {@link DefaultMapping} for every configured view {@link Binding} for which there is an incoming request
	 * parameter. If there is no matching incoming request parameter, a special mapping is created that will set the
	 * target field on the model to an empty value (typically null).
	 * </p>
	 *
	 * @param mapper the mapper to which mappings will be added
	 * @param parameterNames the request parameters
	 * @param model the model
	 */
	protected void addModelBindings(DefaultMapper mapper, Set<String> parameterNames, Object model) {
		for (Binding binding : binderConfiguration.getBindings()) {
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

	/**
	 * <p>
	 * Creates and adds a {@link DefaultMapping} for the given {@link Binding}. Information such as the model field
	 * name, if the field is required, and whether type conversion is needed will be passed on from the binding to the
	 * mapping.
	 * </p>
	 * <p>
	 * <b>Note:</b> with Spring 3 type conversion and formatting now in use in Web Flow, it is no longer necessary to
	 * use named converters on binding elements. The preferred approach is to register Spring 3 formatters. Named
	 * converters are supported for backwards compatibility only and will not result in use of the Spring 3 type
	 * conversion system at runtime.
	 * </p>
	 *
	 * @param mapper the mapper to add the mapping to
	 * @param binding the binding element
	 * @param model the model
	 */
	protected void addMapping(DefaultMapper mapper, Binding binding, Object model) {
		Expression source = new RequestParameterExpression(binding.getProperty());
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(binding.getProperty(), parserContext);
		DefaultMapping mapping = new DefaultMapping(source, target);
		mapping.setRequired(binding.getRequired());
		if (binding.getConverter() != null) {
			Assert.notNull(conversionService,
					"A ConversionService must be configured to use resolve custom converters to use during binding");
			ConversionExecutor conversionExecutor = conversionService.getConversionExecutor(binding.getConverter(),
					String.class, target.getValueType(model));
			mapping.setTypeConverter(conversionExecutor);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding mapping for parameter '" + binding.getProperty() + "'");
		}
		mapper.addMapping(mapping);
	}

	/**
	 * Add a {@link DefaultMapping} instance for all incoming request parameters except those having a special field
	 * marker prefix. This method is used when binding configuration was not specified on the view.
	 *
	 * @param mapper the mapper to add mappings to
	 * @param parameterNames the request parameter names
	 * @param model the model
	 */
	protected void addDefaultMappings(DefaultMapper mapper, Set<String> parameterNames, Object model) {
		for (String parameterName : parameterNames) {
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

	/**
	 * Adds a special {@link DefaultMapping} that results in setting the target field on the model to an empty value
	 * (typically null).
	 *
	 * @param mapper the mapper to add the mapping to
	 * @param field the field for which a mapping is to be added
	 * @param model the model
	 */
	protected void addEmptyValueMapping(DefaultMapper mapper, String field, Object model) {
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(field, parserContext);
		try {
			Class<?> propertyType = target.getValueType(model);
			Expression source = new StaticExpression(getEmptyValue(propertyType));
			DefaultMapping mapping = new DefaultMapping(source, target);
			if (logger.isDebugEnabled()) {
				logger.debug("Adding empty value mapping for parameter '" + field + "'");
			}
			mapper.addMapping(mapping);
		} catch (EvaluationException e) {
		}
	}

	/**
	 * Adds a {@link DefaultMapping} between the given request parameter name and a matching model field.
	 *
	 * @param mapper the mapper to add the mapping to
	 * @param parameter the request parameter name
	 * @param model the model
	 */
	protected void addDefaultMapping(DefaultMapper mapper, String parameter, Object model) {
		Expression source = new RequestParameterExpression(parameter);
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(parameter, parserContext);
		DefaultMapping mapping = new DefaultMapping(source, target);
		if (logger.isDebugEnabled()) {
			logger.debug("Adding default mapping for parameter '" + parameter + "'");
		}
		mapper.addMapping(mapping);
	}

	// package private

	/**
	 * Restores the internal state of this view from the provided state holder.
	 * @see AbstractMvcViewFactory#getView(RequestContext)
	 */
	void restoreState(ViewActionStateHolder stateHolder) {
		eventId = stateHolder.getEventId();
		userEventProcessed = stateHolder.getUserEventProcessed();
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
	protected boolean shouldValidate(Object model, TransitionDefinition transition) {
		Boolean validateAttribute = getValidateAttribute(transition);
		if (validateAttribute != null) {
			return validateAttribute;
		} else {
			AttributeMap<Object> flowExecutionAttributes = requestContext.getFlowExecutionContext().getAttributes();
			Boolean validateOnBindingErrors = flowExecutionAttributes.getBoolean("validateOnBindingErrors");
			if (validateOnBindingErrors != null) {
				if (!validateOnBindingErrors && mappingResults.hasErrorResults()) {
					return false;
				}
			}
			return true;
		}
	}

	// internal helpers

	private Map<String, Object> flowScopes() {
		if (requestContext.getCurrentState().isViewState()) {
			return requestContext.getConversationScope().union(requestContext.getFlowScope())
					.union(requestContext.getViewScope()).union(requestContext.getFlashScope())
					.union(requestContext.getRequestScope()).asMap();
		} else {
			return requestContext.getConversationScope().union(requestContext.getFlowScope())
					.union(requestContext.getFlashScope()).union(requestContext.getRequestScope()).asMap();
		}
	}

	private void exposeBindingModel(Map<String, Object> model) {
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
			try {
				return model.getValue(requestContext);
			} catch (EvaluationException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	private Expression getModelExpression() {
		return (Expression) requestContext.getCurrentState().getAttributes().get("model");
	}

	private Object getEmptyValue(Class<?> fieldType) {
		if (fieldType != null && boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
			// Special handling of boolean property.
			return false;
		} else if (fieldType != null && fieldType.isArray()) {
			// Special handling of array property.
			return Array.newInstance(fieldType.getComponentType(), 0);
		} else {
			// Default value: try null.
			return null;
		}
	}

	private boolean hasErrors(MappingResults results) {
		return results.hasErrorResults() && !onlyPropertyNotFoundErrorsPresent(results);
	}

	private boolean onlyPropertyNotFoundErrorsPresent(MappingResults results) {
		return results.getResults(PROPERTY_NOT_FOUND_ERROR).size() == mappingResults.getErrorResults().size();
	}

	private void addErrorMessages(MappingResults results) {
		List<MappingResult> errors = results.getResults(MAPPING_ERROR);
		for (MappingResult error : errors) {
			requestContext.getMessageContext().addMessage(createMessageResolver(error));
		}
	}

	protected MessageResolver createMessageResolver(MappingResult error) {
		String model = getModelExpression().getExpressionString();
		String field = error.getMapping().getTargetExpression().getExpressionString();
		Class<?> fieldType = error.getMapping().getTargetExpression().getValueType(getModelObject());
		String[] messageCodes = messageCodesResolver.resolveMessageCodes(error.getCode(), model, field, fieldType);
		return new MessageBuilder().error().source(field).codes(messageCodes).resolvableArg(field)
				.defaultText(error.getCode() + " on " + field).build();
	}

	private Boolean getValidateAttribute(TransitionDefinition transition) {
		if (transition != null) {
			return transition.getAttributes().getBoolean("validate");
		} else {
			return null;
		}
	}

	private void validate(Object model, TransitionDefinition transition) {
		if (logger.isDebugEnabled()) {
			logger.debug("Validating model");
		}
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, getModelExpression()
				.getExpressionString(), expressionParser, messageCodesResolver, mappingResults, validationHintResolver);
		helper.setValidator(this.validator);
		helper.validate();
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

		public Class<?> getValueType(Object context) {
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
