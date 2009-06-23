/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.core;

/**
 * Root class for exceptions thrown by the Spring Web Flow system. All other exceptions within the system should be
 * assignable to this class.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class FlowException extends RuntimeException {

	/**
	 * Creates a new flow exception.
	 * @param msg the message
	 * @param cause the cause
	 */
	public FlowException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Creates a new flow exception.
	 * @param msg the message
	 */
	public FlowException(String msg) {
		super(msg);
	}

}