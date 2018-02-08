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
 * A context for a validator to use to access user data and report validation failures.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface ValidationContext {

	/**
	 * A context for adding failure messages to display to the user directly.
	 */
	MessageContext getMessageContext();

	/**
	 * The current user.
	 */
	Principal getUserPrincipal();

	/**
	 * The current user event that triggered validation.
	 */
	String getUserEvent();

	/**
	 * Obtain the value entered by the current user in the UI field bound to the property provided.
	 * @param property the name of a bound property
	 * @return the value the user entered in the field bound to the property
	 */
	Object getUserValue(String property);

}
