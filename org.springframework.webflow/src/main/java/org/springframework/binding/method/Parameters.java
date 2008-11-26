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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An ordered list of method parameters.
 * 
 * @author Keith
 */
public class Parameters {

	/**
	 * Canonical instance for an empty parameters list.
	 */
	public static final Parameters NONE = new Parameters(0);

	/**
	 * The list.
	 */
	private List parameters;

	/**
	 * Create a parameter list of the default size (3 elements).
	 */
	public Parameters() {
		this(3);
	}

	/**
	 * Create a parameter list with the specified size.
	 * @param size the size
	 */
	public Parameters(int size) {
		this.parameters = new ArrayList(size);
	}

	/**
	 * Create a parameter list with one parameter.
	 * @param parameter the single parameter
	 */
	public Parameters(Parameter parameter) {
		this.parameters = new ArrayList(1);
		add(parameter);
	}

	/**
	 * Create a parameter list from the parameter array.
	 * @param parameters the parameters
	 */
	public Parameters(Parameter[] parameters) {
		this.parameters = new ArrayList(parameters.length);
		addAll(parameters);
	}

	/**
	 * Add a new parameter to this list.
	 * @param parameter the parameter
	 */
	public boolean add(Parameter parameter) {
		return this.parameters.add(parameter);
	}

	/**
	 * Add new parameters to this list.
	 * @param parameters the parameters
	 */
	public boolean addAll(Parameter[] parameters) {
		return this.parameters.addAll(Arrays.asList(parameters));
	}

	/**
	 * Return a parameter iterator.
	 * @return the iterator
	 */
	public Iterator iterator() {
		return parameters.iterator();
	}

	/**
	 * Get an array containing each parameter type. The resulting array could contain null values if the corresponding
	 * parameters did not specify a parameter type.
	 * @return the types
	 */
	public Class[] getTypesArray() {
		int i = 0;
		Class[] types = new Class[parameters.size()];
		for (Iterator it = parameters.iterator(); it.hasNext();) {
			Parameter param = (Parameter) it.next();
			types[i] = param.getType();
			i++;
		}
		return types;
	}

	/**
	 * Returns the number of parameters in this list.
	 * @return the size
	 */
	public int size() {
		return parameters.size();
	}

	/**
	 * Return the parameter at the provided index.
	 * @param index the parameter index
	 * @return the parameter at that index
	 * @throws IndexOutOfBoundsException if the provided index is out of bounds
	 */
	public Parameter getParameter(int index) throws IndexOutOfBoundsException {
		return (Parameter) parameters.get(index);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Parameters)) {
			return false;
		}
		Parameters other = (Parameters) obj;
		return parameters.equals(other.parameters);
	}

	public int hashCode() {
		return parameters.hashCode();
	}

	public String toString() {
		return parameters.toString();
	}
}