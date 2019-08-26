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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Unit test for {@link ValidationHelper}
 */
public class ValidationHelperTests {

	private MockRequestControlContext requestContext;

	private String eventId;

	private String modelName;

	private DefaultMessageCodesResolver codesResolver;


	@BeforeEach
	public void setUp() throws Exception {
		requestContext = new MockRequestControlContext();
		eventId = "userEvent";
		modelName = "model";
		codesResolver = new DefaultMessageCodesResolver();
	}

	@Test
	public void testValidateWithMessageContext() {
		Object model = new StubModelMessageContext();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, null, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("messagecontext").length);
		assertEquals(0, messages.getMessagesBySource("validationcontext").length);
	}

	@Test
	public void testValidateWithValidationContext() {
		Object model = new StubModelValidationContext();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext").length);
	}

	@Test
	public void testValidateWithMessageContextForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelMessageContext.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null,
				this.codesResolver, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("messagecontext-external").length);
	}

	@Test
	public void testValidateWithValidationContextForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelValidationContext.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null,
				this.codesResolver, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext-external").length);
	}

	@Test
	public void testValidateWithErrorsForBeanValidator() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelErrors.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null,
				this.codesResolver, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("errors-external").length);
	}

	@Test
	public void testValidateWithErrorsForBeanValidatorOverridden() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("modelValidator", StubModelErrorsOverridden.class);
		((Flow) requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		ValidationHelper helper = new ValidationHelper(new Object(), requestContext, eventId, modelName, null,
				this.codesResolver, null);
		helper.validate();
		MessageContext messages = requestContext.getMessageContext();
		assertEquals(1, messages.getAllMessages().length);
		assertEquals(1, messages.getMessagesBySource("validationcontext-external").length);
	}

	@Test
	public void testStateAndFallbackModelValidationMethodInvoked() {
		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	@Test
	public void testFallbackModelValidationMethodInvoked() {
		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackErrorsModelValidationMethodInvoked() {
		ErrorsModel model = new ErrorsModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	@Test
	public void testFallbackModelErrorsValidationMethodInvoked() {
		ErrorsModel model = new ErrorsModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(model.state1Invoked);
		assertTrue(model.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackValidatorInvoked() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackValidatorInvokedForSubclass() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testFallbackValidatorInvoked() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testFallbackValidatorInvokedForSubclass() {
		ModelValidator validator = new ModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackLegacyValidatorInvoked() {
		LegacyModelValidator validator = new LegacyModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackLegacyValidatorInvokedForSubclass() {
		LegacyModelValidator validator = new LegacyModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testFallbackLegacyValidatorInvoked() {
		LegacyModelValidator validator = new LegacyModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackErrorsValidatorInvoked() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testStateAndFallbackErrorsValidatorInvokedForSubclass() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state1", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertTrue(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testFallbackErrorsValidatorInvoked() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testFallbackErrorsValidatorInvokedForSubclass() {
		ErrorsModelValidator validator = new ErrorsModelValidator();
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("modelValidator", validator);
		requestContext.getRootFlow().setApplicationContext(applicationContext);

		Model model = new Model();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null,
				this.codesResolver, null);
		ViewState state1 = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		requestContext.setCurrentState(state1);
		helper.validate();
		assertFalse(validator.state1Invoked);
		assertTrue(validator.fallbackInvoked);
	}

	@Test
	public void testSmartValidatorWithClassHint() {
		ViewState state = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		state.getAttributes().put("validationHints", new StaticExpression(new Object[] { Model.State1.class }));
		requestContext.setCurrentState(state);

		LegacyModelValidator validator = new LegacyModelValidator();
		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, codesResolver, null);
		helper.setValidator(validator);

		helper.validate();

		assertTrue(validator.fallbackInvoked);
		assertTrue(validator.hints.length > 0);
		assertEquals(Model.State1.class, validator.hints[0]);
	}

	@Test
	public void testSmartValidatorWithHintResolution() {
		ViewState state = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		state.getAttributes().put("validationHints", new StaticExpression("State1"));
		requestContext.setCurrentState(state);

		LegacyModelValidator validator = new LegacyModelValidator();
		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, codesResolver, null);
		helper.setValidator(validator);

		helper.validate();

		assertTrue(validator.fallbackInvoked);
		assertTrue(validator.hints.length > 0);
		assertEquals(Model.State1.class, validator.hints[0]);
	}

	@Test
	public void testSmartValidatorWithHintOnTransition() {
		Transition transition = new Transition();
		transition.setMatchingCriteria(new DefaultTransitionCriteria(new StaticExpression(eventId)));
		transition.getAttributes().put("validationHints", new StaticExpression("State1"));

		ViewState state = new ViewState(requestContext.getRootFlow(), "state2", new StubViewFactory());
		state.getTransitionSet().add(transition);
		requestContext.setCurrentState(state);

		LegacyModelValidator validator = new LegacyModelValidator();
		ExtendedModel model = new ExtendedModel();
		ValidationHelper helper = new ValidationHelper(model, requestContext, eventId, modelName, null, codesResolver, null);
		helper.setValidator(validator);

		helper.validate();

		assertTrue(validator.fallbackInvoked);
		assertTrue(validator.hints.length > 0);
		assertEquals(Model.State1.class, validator.hints[0]);
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

		private static interface State1 {}
	}

	public static class ExtendedModel extends Model {
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

	public static class LegacyModelValidator implements SmartValidator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;
		private Object[] hints;

		public void validateState1(Model model, Errors errors) {
			state1Invoked = true;
		}

		public void validate(Object object, Errors errors) {
			fallbackInvoked = true;
		}

		public void validate(Object object, Errors errors, Object... hints) {
			fallbackInvoked = true;
			this.hints = hints;
		}

		public boolean supports(Class<?> clazz) {
			return true;
		}
	}

	public static class ModelValidator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Model model, ValidationContext context) {
			state1Invoked = true;
		}

		public void validate(Model model, ValidationContext context) {
			fallbackInvoked = true;
		}
	}

	public static class ErrorsModelValidator {
		private boolean state1Invoked;
		private boolean fallbackInvoked;

		public void validateState1(Model model, Errors context) {
			state1Invoked = true;
		}

		public void validate(Model model, Errors context) {
			fallbackInvoked = true;
		}
	}

}
