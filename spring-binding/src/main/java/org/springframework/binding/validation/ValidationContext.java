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

package org.springframework.binding.validation;

import java.security.Principal;

import org.springframework.binding.message.MessageContext;

/**
 * A context for validation events.
 * 
 * @author Scott Andrews
 */
public interface ValidationContext {

	/**
	 * Get the context for recording messages
	 */
	public MessageContext getMessageContext();

	/**
	 * Get the current user principal
	 */
	public Principal getUserPrincipal();

	/**
	 * Get the event that triggered validation
	 */
	public String getUserEvent();

}
