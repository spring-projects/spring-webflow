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

import java.lang.reflect.InvocationTargetException;

import org.springframework.core.style.StylerUtils;

/**
 * Base class for exceptions that report a method invocation failure.
 * 
 * @author Keith Donald
 */
public class MethodInvocationException extends RuntimeException {

	/**
	 * The method signature. Transient because a MethodSignature is not Serializable.
	 */
	private transient MethodSignature methodSignature;

	/**
	 * The method invocation argument values. Transient because we cannot guarantee that the arguments are Serializable.
	 */
	private transient Object[] arguments;

	/**
	 * Signals that the method with the specified signature could not be invoked with the provided arguments.
	 * @param methodSignature the method signature
	 * @param arguments the arguments
	 * @param cause the root cause
	 */
	public MethodInvocationException(MethodSignature methodSignature, Object[] arguments, Throwable cause) {
		super("Unable to invoke method " + methodSignature + " with arguments " + StylerUtils.style(arguments), cause);
		this.methodSignature = methodSignature;
		this.arguments = arguments;
	}

	/**
	 * Returns the invoked method's signature.
	 */
	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	/**
	 * Returns the method invocation arguments.
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * Returns the target root cause exception of the method invocation failure.
	 * @return the target throwable
	 */
	public Throwable getTargetException() {
		Throwable targetException = getCause();
		while (targetException instanceof InvocationTargetException) {
			targetException = ((InvocationTargetException) targetException).getTargetException();
		}
		return targetException;
	}
}