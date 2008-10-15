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
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit test for {@link ValidationHelper}
 */
public class ValidationHelperTests extends TestCase {

	private MockRequestContext requestContext;
	private String eventId;
	private String modelName;

	protected void setUp() throws Exception {
		requestContext = new MockRequestContext();
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

	public void testValidateWithValidatioContext() {
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

}
