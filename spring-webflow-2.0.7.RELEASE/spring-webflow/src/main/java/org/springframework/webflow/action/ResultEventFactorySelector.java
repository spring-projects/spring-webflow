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
package org.springframework.webflow.action;

import java.lang.reflect.Method;

/**
 * Helper that selects the {@link ResultEventFactory} to use for a particular result object.
 * 
 * @see EvaluateAction
 * 
 * @author Keith Donald
 */
public class ResultEventFactorySelector {

	/**
	 * The event factory instance for mapping a return value to a success event.
	 */
	private SuccessEventFactory successEventFactory = new SuccessEventFactory();

	/**
	 * The event factory instance for mapping a result object to an event, using the type of the result object as the
	 * mapping criteria.
	 */
	private ResultObjectBasedEventFactory resultObjectBasedEventFactory = new ResultObjectBasedEventFactory();

	/**
	 * Select the appropriate result event factory for attempts to invoke the given method.
	 * @param method the method
	 * @return the result event factory
	 */
	public ResultEventFactory forMethod(Method method) {
		return forType(method.getReturnType());
	}

	/**
	 * Select the appropriate result event factory for the given result.
	 * @param result the result
	 * @return the result event factory
	 */
	public ResultEventFactory forResult(Object result) {
		if (result == null) {
			return successEventFactory;
		} else {
			return forType(result.getClass());
		}
	}

	/**
	 * Select the appropriate result event factory for given result type. This implementation returns
	 * {@link ResultObjectBasedEventFactory} if the type is
	 * {@link ResultObjectBasedEventFactory#isMappedValueType(Class) mapped} by that result event factory, otherwise
	 * {@link SuccessEventFactory} is returned.
	 * @param resultType the result type
	 * @return the result event factory
	 */
	protected ResultEventFactory forType(Class resultType) {
		if (resultObjectBasedEventFactory.isMappedValueType(resultType)) {
			return resultObjectBasedEventFactory;
		} else {
			return successEventFactory;
		}
	}
}