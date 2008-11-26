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
package org.springframework.binding.method;


/**
 * Thrown when a method key could not be resolved to an invokable java Method on a Class.
 * 
 * @author Keith Donald
 */
public class InvalidMethodKeyException extends RuntimeException {

	/**
	 * The method key that could not be resolved.
	 */
	private MethodKey methodKey;

	/**
	 * Creates an exception signaling an invalid method signature.
	 * @param methodKey the class method key
	 * @param cause the cause
	 */
	public InvalidMethodKeyException(MethodKey methodKey, Exception cause) {
		super("Could not resolve method with key " + methodKey, cause);
		this.methodKey = methodKey;
	}

	/**
	 * Returns the invalid method key.
	 * @return the method key.
	 */
	public MethodKey getMethodKey() {
		return methodKey;
	}
}