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

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * A specification for a method consisting of the methodName and an optional set of named arguments. This class provides
 * the ability to resolve a method with parameters and evaluate its argument values as part of a
 * {@link MethodInvoker method invoker attempt}.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class MethodSignature {

	/**
	 * The name of the method, e.g "execute".
	 */
	private String methodName;

	/**
	 * The parameter types of the method, e.g "int param1".
	 */
	private Parameters parameters;

	/**
	 * Creates a method signature with no parameters.
	 * @param methodName the name of the method
	 */
	public MethodSignature(String methodName) {
		this(methodName, Parameters.NONE);
	}

	/**
	 * Creates a method signature with a single parameter.
	 * @param methodName the name of the method
	 * @param parameter the method parameter
	 */
	public MethodSignature(String methodName, Parameter parameter) {
		this(methodName, new Parameters(parameter));
	}

	/**
	 * Creates a method signature with a list of parameters.
	 * @param methodName the name of the method
	 * @param parameters the method parameters
	 */
	public MethodSignature(String methodName, Parameters parameters) {
		Assert.notNull(methodName, "The method name is required");
		Assert.notNull(parameters, "The parameters are required");
		this.methodName = methodName;
		this.parameters = parameters;
	}

	/**
	 * Returns the method name.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Returns the method parameters.
	 */
	public Parameters getParameters() {
		return parameters;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof MethodSignature)) {
			return false;
		}
		MethodSignature other = (MethodSignature) obj;
		return methodName.equals(other.methodName) && parameters.equals(other.parameters);
	}

	public int hashCode() {
		return methodName.hashCode() + parameters.hashCode();
	}

	public String toString() {
		return new ToStringCreator(this).append("methodName", methodName).append("parameters", parameters).toString();
	}
}