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

import junit.framework.TestCase;

import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Unit test for {@link ValidationHelper}
 */
public class ValidationHelperTests extends TestCase {

	private MockRequestControlContext requestContext;

	private String eventId;

	private String modelName;

	protected void setUp() throws Exception {
		requestContext = new MockRequestControlContext();
		eventId = "userEvent";
		modelName = "model";
	}

	public void testValidateWithMessageContext() {
		Object model = new StubModelMessageContext();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("messagecontext").length);
		assertEquals(0, messages.getMessagesBySource("validationcontext").length);
	}

	public void testValidateWithValidationContext() {
		Object model = new StubModelValidationContext();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext").length);
	}

	public void testValidateWithMessageContextForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelMessageContext.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("messagecontext-external").length);
	}

	public void testValidateWithValidationContextForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelValidationContext.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext-external").length);
	}

	public void testValidateWithErrorsForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelErrors.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("errors-external").length);
	}

	public void testValidateWithErrorsForBeanValidatorOverridden() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelErrorsOverridden.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext-external").length);
	}

	public void testStateAndFallbackModelValidationMethodInvoked() {
		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	public void testFallbackModelValidationMethodInvoked() {
		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	public void testStateAndFallbackErrorsModelValidationMethodInvoked() {
		ErrorsModel model = new ErrorsModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	public void testFallbackModelErrorsValidationMethodInvoked() {
		ErrorsModel model = new ErrorsModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	public void testStateAndFallbackValidatorInvoked() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public void testFallbackValidatorInvoked() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public void testStateAndFallbackLegacyValidatorInvoked() {
		LegacyModelValidator validator = new LegacyModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public void testFallbackLegacyValidatorInvoked() {
		LegacyModelValidator validator = new LegacyModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public void testStateAndFallbackErrorsValidatorInvoked() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public void testFallbackErrorsValidatorInvoked() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Object model = new Object();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	public static class Model {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(ValidationContext context) {
			state1Invoked = true;
		}

		public void validate(ValidationContext context) {
			fallbackInvoked = true;
		}
	}

	public static class ErrorsModel {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Errors errors) {
			state1Invoked = true;
		}

		public void validate(Errors errors) {
			fallbackInvoked = true;
		}
	}

	public static class LegacyModelValidator implements Validator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Object object, Errors errors) {
			state1Invoked = true;
		}

		public void validate(Object object, Errors errors) {
			fallbackInvoked = true;
		}

		public boolean supports(Class clazz) {
			return true;
		}
	}

	public static class ModelValidator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Object object, ValidationContext context) {
			state1Invoked = true;
		}

		public void validate(Object object, ValidationContext context) {
			fallbackInvoked = true;
		}
	}

	public static class ErrorsModelValidator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Object object, Errors context) {
			state1Invoked = true;
		}

		public void validate(Object object, Errors context) {
			fallbackInvoked = true;
		}
	}

}
