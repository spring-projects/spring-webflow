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

	/**
	 * Add a validation failure to this context. Called by a validator to report failures against the current object
	 * being validated. Use to report a general failure against the object, or a specific failure against a property on
	 * the object. The failure code provided is typically mapped to one or message codes that result in a Message being
	 * added to the {@link MessageContext}.
	 * @param failure the validation failure
	 * @see #getMessageContext()
	 * @see ValidationFailureMessageResolverFactory
	 */
	public void addFailure(ValidationFailure failure);

	/**
	 * A context for adding messages to display to the user directly. The {@link #addFailure(ValidationFailure)}
	 * operation provides a higher level of abstraction for reporting validation failures. This method provides more
	 * control over the actual message added.
	 */
	public MessageContext getMessageContext();

}
