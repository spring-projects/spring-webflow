/*
 * Copyright 2008-2012 the original author or authors.
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

package org.springframework.webflow.validation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageContextErrors;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A helper class the encapsulates conventions to invoke validation logic.
 *
 * @author Scott Andrews
 * @author Canny Duck
 * @author Jeremy Grelle
 */
public class ValidationHelper {

	private static final Log logger = LogFactory.getLog(ValidationHelper.class);

	private final Object model;

	private final RequestContext requestContext;

	private final String eventId;

	private final String modelName;

	private final ExpressionParser expressionParser;

	private final MessageCodesResolver messageCodesResolver;

	private final MappingResults mappingResults;

	private Validator validator;

	private Object[] validationHints;


	/**
	 * Create a throwaway validation helper object. Validation is invoked by the {@link #validate()} method.
	 * <p>
	 * Validation methods invoked include a validation method on the model object or a validator bean. The method name
	 * on either object is in the form of validate[viewStateId]. A ValidationContext is passed in the method signature
	 * along with the model object for validator beans. A MessageContext can be substituted for the ValidationContext.
	 * <p>
	 * For example: <code>model.validateEnterBookingDetails(VaticationContext)</code> or
	 * <code>context.getBean("modelValidator").validateEnterBookingDetails(model, VaticationContext)</code>
	 *
	 * @param model the object to validate
	 * @param requestContext the context for the request
	 * @param eventId the event triggering validation
	 * @param modelName the name of the model object
	 * @param expressionParser the expression parser
	 * @param mappingResults object mapping results
	 */
	public ValidationHelper(Object model, RequestContext requestContext, String eventId,
			String modelName, ExpressionParser expressionParser, MessageCodesResolver messageCodesResolver,
			MappingResults mappingResults, ValidationHintResolver hintResolver) {

		Assert.notNull(model, "The model to validate is required");
		Assert.notNull(requestContext, "The request context for the validator is required");
		this.model = model;
		this.requestContext = requestContext;
		this.eventId = eventId;
		this.modelName = modelName;
		this.expressionParser = expressionParser;
		this.messageCodesResolver = messageCodesResolver;
		this.mappingResults = mappingResults;
		this.validationHints = initValidationHints(model, requestContext, eventId, hintResolver);
	}

	/**
	 * An alternative constructor that creates an instance of {@link BeanValidationHintResolver}.
	 */
	public ValidationHelper(Object model, RequestContext requestContext, String eventId,
			String modelName, ExpressionParser expressionParser, MessageCodesResolver messageCodesResolver,
			MappingResults mappingResults) {

		this(model, requestContext, eventId, modelName, expressionParser,
				messageCodesResolver, mappingResults, new BeanValidationHintResolver());
	}


	private static Object[] initValidationHints(Object model, RequestContext requestContext, String eventId,
			ValidationHintResolver hintResolver) {

		Expression expr = null;
		TransitionDefinition transition = requestContext.getMatchingTransition(eventId);
		if (transition != null) {
			expr = (Expression) transition.getAttributes().get("validationHints");
		}
		if (expr == null) {
			expr = (Expression) requestContext.getCurrentState().getAttributes().get("validationHints");
		}
		if (expr == null) {
			return null;
		}
		String flowId = requestContext.getActiveFlow().getId();
		String stateId = requestContext.getCurrentState().getId();
		try {
			Object hintsValue = expr.getValue(requestContext);
			if (hintsValue instanceof String) {
				String[] hints = StringUtils.commaDelimitedListToStringArray((String) hintsValue);
				return hintResolver.resolveValidationHints(model, flowId, stateId, hints);
			}
			else if (hintsValue instanceof Object[]) {
				return (Object[]) hintsValue;
			}
			else {
				throw new FlowExecutionException(flowId, stateId,
						"Failed to resolve validation hints [" + hintsValue + "]");
			}
		}
		catch (EvaluationException e) {
			throw new FlowExecutionException(flowId, stateId,
					"Failed to resolve validation hints expression [" + expr + "]", e);
		}
	}

	/**
	 * Configure a {@link Validator} to apply to the model.
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Provide validation hints (e.g. JSR-303 validation groups). Note that the constructor
	 * automatically detects validation hints specified on a view state or a transition.
	 * Therefore use of this method should not be needed.
	 */
	public void setValidationHints(Object[] validationHints) {
		this.validationHints = validationHints;
	}

	/**
	 * Invoke the validators available by convention.
	 */
	public void validate() {
		if (this.validator != null) {
			invokeValidatorDefaultValidateMethod(this.validator);
		}
		invokeModelValidationMethod(model);
		Object modelValidator = getModelValidator();
		if (modelValidator != null) {
			invokeModelValidator(modelValidator);
		}
	}

	private void invokeModelValidationMethod(Object model) {
		invokeValidateMethodForCurrentState(model);
		invokeDefaultValidateMethod(model);
	}

	private boolean invokeValidateMethodForCurrentState(Object model) {
		String methodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName, ValidationContext.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state model validation method '" + methodName + "(ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, new DefaultValidationContext(requestContext, eventId, mappingResults));
			return true;
		}
		// web flow 2.0.3 or < compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName, MessageContext.class);
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, requestContext.getMessageContext());
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName, Errors.class);
		if (validateMethod != null) {
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, messageCodesResolver, mappingResults);
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state model validation method '" + methodName + "(Errors)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, errors);
			return true;
		}
		return false;
	}

	private boolean invokeDefaultValidateMethod(Object model) {
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), "validate", ValidationContext.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default model validation method 'validate(ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, new DefaultValidationContext(requestContext, eventId, mappingResults));
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), "validate", Errors.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default model validation method 'validate(Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, messageCodesResolver, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, model, errors);
			return true;
		}
		return false;
	}

	private Object getModelValidator() {
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		if (beanFactory != null && StringUtils.hasText(modelName)) {
			String validatorName = modelName + "Validator";
			if (beanFactory.containsBean(validatorName)) {
				return beanFactory.getBean(validatorName);
			}
		}
		return null;
	}

	private void invokeModelValidator(Object validator) {
		invokeValidatorValidateMethodForCurrentState(validator);
		invokeValidatorDefaultValidateMethod(validator);
	}

	private boolean invokeValidatorValidateMethodForCurrentState(Object validator) {
		String methodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		// preferred
		Method validateMethod = findValidationMethod(model, validator, methodName, ValidationContext.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state validator method '"
						+ ClassUtils.getShortName(validator.getClass()) + "." + methodName + "("
						+ ClassUtils.getShortName(model.getClass()) + ", ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, validator, model,
					new DefaultValidationContext(requestContext, eventId, mappingResults));
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = findValidationMethod(model, validator, methodName, Errors.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state validator method '"
						+ ClassUtils.getShortName(validator.getClass()) + "." + methodName + "("
						+ ClassUtils.getShortName(model.getClass()) + ", Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, messageCodesResolver, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, validator, model, errors);
			return true;
		}
		// web flow 2.0.0 to 2.0.3 compatibility only [to remove in web flow 3]
		validateMethod = findValidationMethod(model, validator, methodName, MessageContext.class);
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, validator,
					model, requestContext.getMessageContext());
			return true;
		}
		return false;
	}

	private boolean invokeValidatorDefaultValidateMethod(Object validator) {
		if (validator instanceof Validator) {
			// Spring Framework Validator type
			Validator springValidator = (Validator) validator;
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking Spring Validator '" + ClassUtils.getShortName(validator.getClass()) + "'");
			}
			if (springValidator.supports(model.getClass())) {
				MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
						model, expressionParser, messageCodesResolver, mappingResults);

				if (this.validationHints != null) {
					if (springValidator instanceof SmartValidator) {
						((SmartValidator) springValidator).validate(model, errors, this.validationHints);
					}
					else {
						logger.warn("Validation hints provided but validator not an instance of SmartValidator: ["
								+ springValidator.getClass().getName() + "]");
					}
				}
				else {
					springValidator.validate(model, errors);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Spring Validator '" + ClassUtils.getShortName(validator.getClass())
							+ "' doesn't support model class " + model.getClass());
				}
			}
			return true;
		}
		// preferred
		Method validateMethod = findValidationMethod(model, validator, "validate", ValidationContext.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default validator method '" + ClassUtils.getShortName(validator.getClass())
						+ ".validate(" + ClassUtils.getShortName(model.getClass()) + ", ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, validator, model,
					new DefaultValidationContext(requestContext, eventId, mappingResults));
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = findValidationMethod(model, validator, "validate", Errors.class);
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default validator method '" + ClassUtils.getShortName(validator.getClass())
						+ ".validate(" + ClassUtils.getShortName(model.getClass()) + ", Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, messageCodesResolver, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, validator, model, errors);
			return true;
		}
		return false;
	}

	private Method findValidationMethod(Object model, Object validator, String methodName, Class<?> context) {
		Class<?> modelClass = AopUtils.getTargetClass(model);

		List<Class<?>> modelSearchClasses = new ArrayList<>();
		while (modelClass != null) {
			modelSearchClasses.add(modelClass);
			modelClass = modelClass.getSuperclass();
		}
		for (Class<?> searchClass : modelSearchClasses) {
			Method method = ReflectionUtils.findMethod(validator.getClass(), methodName, searchClass, context);
			if (method != null) {
				return method;
			}
		}
		return null;
	}

}
