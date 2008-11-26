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
package org.springframework.webflow.test;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.ParameterMap;

/**
 * A extension of parameter map that allows for mutation of parameters. Useful as a stub for testing.
 * 
 * @see ParameterMap
 * 
 * @author Keith Donald
 */
public class MockParameterMap extends LocalParameterMap {

	/**
	 * Creates a new parameter map, initially empty.
	 */
	public MockParameterMap() {
		super(new HashMap());
	}

	/**
	 * Add a new parameter to this map.
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 * @return this, to support call chaining
	 */
	public MockParameterMap put(String parameterName, String parameterValue) {
		getMapInternal().put(parameterName, parameterValue);
		return this;
	}

	/**
	 * Add a new multi-valued parameter to this map.
	 * @param parameterName the parameter name
	 * @param parameterValues the parameter values
	 * @return this, to support call chaining
	 */
	public MockParameterMap put(String parameterName, String[] parameterValues) {
		getMapInternal().put(parameterName, parameterValues);
		return this;
	}

	/**
	 * Add a new multi-part file parameter to this map.
	 * @param parameterName the parameter name
	 * @param parameterValues the parameter values
	 * @return this, to support call chaining
	 */
	public MockParameterMap put(String parameterName, MultipartFile parameterValues) {
		getMapInternal().put(parameterName, parameterValues);
		return this;
	}

}