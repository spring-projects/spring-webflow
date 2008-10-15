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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageContextErrors;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.webflow.execution.RequestContext;

/**
 * A helper class the encapsulates conventions to invoke validation logic.
 * 
 * @author Scott Andrews
 */
public class ValidationHelper {

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
		Assert.notNull(model, "The model to validate is requried");
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
		String validateMethodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		validateWithContext(model, validateMethodName);
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		if (beanFactory != null && StringUtils.hasText(modelName)) {
			String validatorName = modelName + "Validator";
			if (beanFactory.containsBean(validatorName)) {
				Object validator = beanFactory.getBean(validatorName);
				if (!validateWithModelAndContext(model, validator, validateMethodName)) {
					validateWithModelAndErrors(model, validator, validateMethodName);
				}
			}
		}
	}

	/*
	 * Invoke validate method on the model for the current state passing either a MessageContext or ValidationContext.
	 * Preference is given to the ValidationContext method.
	 */
	private boolean validateWithContext(Object model, String validateMethodName) {
		boolean validationInvoked = false;
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), validateMethodName,
				new Class[] { ValidationContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { new DefaultValidationContext(
					requestContext, eventId) });
			validationInvoked = true;
		} else {
			validateMethod = ReflectionUtils.findMethod(model.getClass(), validateMethodName,
					new Class[] { MessageContext.class });
			if (validateMethod != null) {
				ReflectionUtils
						.invokeMethod(validateMethod, model, new Object[] { requestContext.getMessageContext() });
				validationInvoked = true;
			}
		}
		return validationInvoked;
	}

	/*
	 * Invoke validate method on a distinct validator providing the model to validate and either a MessageContext or
	 * ValidationContext. Preference is given to the ValidationContext method.
	 */
	private boolean validateWithModelAndContext(Object model, Object validator, String validateMethodName) {
		boolean validationInvoked = false;
		Method validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
				model.getClass(), ValidationContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
					new DefaultValidationContext(requestContext, eventId) });
			validationInvoked = true;
		} else {
			validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
					model.getClass(), MessageContext.class });
			if (validateMethod != null) {
				ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
						requestContext.getMessageContext() });
				validationInvoked = true;
			}
		}
		return validationInvoked;
	}

	/*
	 * Invoke validate method on a distinct validator providing the model to validate and an Errors object.
	 */
	private boolean validateWithModelAndErrors(Object model, Object validator, String validateMethodName) {
		boolean validationInvoked = false;
		Method validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
				model.getClass(), Errors.class });
		if (validateMethod != null) {
			MessageContextErrors errors = new MessageContextErrors(requestContext.getMessageContext(), modelName,
					model, expressionParser, mappingResults);
			ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model, errors });
			validationInvoked = true;
		}
		return validationInvoked;
	}

}
