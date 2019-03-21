/*
 * Copyright 2008 the original author or authors.
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

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

/**
 * Support class for {@link ValidationHelperTests}
 */
public class StubModelMessageContext {

	public void validateMockState(MessageContext context) {
		context.addMessage(new MessageBuilder().source("messagecontext").defaultText("").build());
	}

	public void validateMockState(Object model, MessageContext context) {
		context.addMessage(new MessageBuilder().source("messagecontext-external").defaultText("").build());
	}

}
