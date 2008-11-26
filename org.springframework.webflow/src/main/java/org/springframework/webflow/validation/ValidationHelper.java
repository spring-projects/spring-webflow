/*
 * Copyright 2008 the original author or authors.
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

package org.springframework.webflow.validation;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
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
import org.springframework.validation.Validator;
import org.springframework.webflow.execution.RequestContext;

/**
 * A helper class the encapsulates conventions to invoke validation logic.
 * 
 * @author Scott Andrews
 */
public class ValidationHelper {

	private static final Log logger = LogFactory.getLog(ValidationHelper.class);

	private final Object model;

	private final RequestContext requestContext;

	private final String eventId;

	private final String modelName;

	private final ExpressionParser expressionParser;

	private final MappingResults mappingResults;

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
	public ValidationHelper(Object model, RequestContext requestContext, String eventId, String modelName,
			ExpressionParser expressionParser, MappingResults mappingResults) {
		Assert.notNull(model, "The model to validate is required");
		Assert.notNull(requestContext, "The request context for the validator is required");
		this.model = model;
		this.requestContext = requestContext;
		this.eventId = eventId;
		this.modelName = modelName;
		this.expressionParser = expressionParser;
		this.mappingResults = mappingResults;
	}

	/**
	 * Invoke the validators available by convention.
	 */
	public void validate() {
		invokeModelValidationMethod(model);
		Object validator = getModelValidator();
		if (validator != null) {
			invokeModelValidator(model, validator);
		}
	}

	private void invokeModelValidationMethod(Object model) {
		invokeValidateMethodForCurrentState(model);
		invokeDefaultValidateMethod(model);
	}

	private boolean invokeValidateMethodForCurrentState(Object model) {
		String methodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName,
				new Class[] { ValidationContext.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state model validation method '" + methodName + "(ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { new DefaultValidationContext(
					requestContext, eventId, mappingResults) });
			return true;
		}
		// web flow 2.0.3 or < compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName, new Class[] { MessageContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { requestContext.getMessageContext() });
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), methodName, new Class[] { Errors.class });
		if (validateMethod != null) {
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state model validation method '" + methodName + "(Errors)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { errors });
			return true;
		}
		return false;
	}

	private boolean invokeDefaultValidateMethod(Object model) {
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), "validate",
				new Class[] { ValidationContext.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default model validation method 'validate(ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { new DefaultValidationContext(
					requestContext, eventId, mappingResults) });
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(model.getClass(), "validate", new Class[] { Errors.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default model validation method 'validate(Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { errors });
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

	private void invokeModelValidator(Object model, Object validator) {
		invokeValidatorValidateMethodForCurrentState(model, validator);
		invokeValidatorDefaultValidateMethod(model, validator);
	}

	private boolean invokeValidatorValidateMethodForCurrentState(Object model, Object validator) {
		String methodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(validator.getClass(), methodName, new Class[] {
				model.getClass(), ValidationContext.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state validator method '"
						+ ClassUtils.getShortName(validator.getClass()) + "." + methodName + "("
						+ ClassUtils.getShortName(model.getClass()) + ", ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
					new DefaultValidationContext(requestContext, eventId, mappingResults) });
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(validator.getClass(), methodName, new Class[] { model.getClass(),
				Errors.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking current state validator method '"
						+ ClassUtils.getShortName(validator.getClass()) + "." + methodName + "("
						+ ClassUtils.getShortName(model.getClass()) + ", Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model, errors });
			return true;
		}
		// web flow 2.0.0 to 2.0.3 compatibility only [to remove in web flow 3]
		validateMethod = ReflectionUtils.findMethod(validator.getClass(), methodName, new Class[] { model.getClass(),
				MessageContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
					requestContext.getMessageContext() });
			return true;
		}
		return false;
	}

	private boolean invokeValidatorDefaultValidateMethod(Object model, Object validator) {
		if (validator instanceof Validator) {
			// supports existing validators
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking Spring Validator '" + ClassUtils.getShortName(validator.getClass()) + "'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			((Validator) validator).validate(model, errors);
			return true;
		}
		// preferred
		Method validateMethod = ReflectionUtils.findMethod(validator.getClass(), "validate", new Class[] {
				model.getClass(), ValidationContext.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default validator method '" + ClassUtils.getShortName(validator.getClass())
						+ ".validate(" + ClassUtils.getShortName(model.getClass()) + ", ValidationContext)'");
			}
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
					new DefaultValidationContext(requestContext, eventId, mappingResults) });
			return true;
		}
		// mvc 2 compatibility only
		validateMethod = ReflectionUtils.findMethod(validator.getClass(), "validate", new Class[] { model.getClass(),
				Errors.class });
		if (validateMethod != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking default validator method '" + ClassUtils.getShortName(validator.getClass())
						+ ".validate(" + ClassUtils.getShortName(model.getClass()) + ", Errors)'");
			}
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model, errors });
			return true;
		}
		return false;
	}
}