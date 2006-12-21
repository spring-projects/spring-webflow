/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.engine.support;

import java.lang.reflect.Modifier;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.webflow.engine.FlowVariable;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * A trivial concrete flow variable subclass that creates new variable values
 * using Java reflection.
 * 
 * @author Keith Donald
 */
public class SimpleFlowVariable extends FlowVariable {

	/**
	 * The concrete variable value class.
	 */
	private Class variableClass;

	/**
	 * Creates a new simple flow variable.
	 * @param name the variable name
	 * @param variableClass the concrete variable class
	 * @param scope the variable scope
	 */
	public SimpleFlowVariable(String name, Class variableClass, ScopeType scope) {
		super(name, scope);
		Assert.notNull(variableClass, "The variable class is required");
		Assert.isTrue(!variableClass.isInterface(), "The variable class cannot be an interface");
		Assert.isTrue(!Modifier.isAbstract(variableClass.getModifiers()), "The variable class cannot be abstract");
		this.variableClass = variableClass;
	}

	/**
	 * Returns the variable value class.
	 */
	public Class getVariableClass() {
		return variableClass;
	}

	protected Object createVariableValue(RequestContext context) {
		return BeanUtils.instantiateClass(variableClass);
	}
}