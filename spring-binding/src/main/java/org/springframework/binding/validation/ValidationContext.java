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
 * A validator context. Allows for recording validation error messages, as well as validating data entered by the user.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface ValidationContext {

	/**
	 * The context for recording validation messages
	 */
	public MessageContext getMessageContext();

	/**
	 * The current user.
	 */
	public Principal getUserPrincipal();

	/**
	 * The current user event that triggered validation.
	 */
	public String getUserEvent();

	/**
	 * Obtain the value entered by the current user in the UI field bound to the property provided.
	 * @param property the name of a bound property
	 * @return the value the user entered in the field bound to the property
	 */
	public Object getUserValue(String property);
}
