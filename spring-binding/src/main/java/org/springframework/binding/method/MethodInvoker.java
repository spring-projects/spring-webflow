/*
 * Copyright 2002-2007 the original author or authors.
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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.CachingMapDecorator;

/**
 * A helper for invoking typed methods on abritrary objects, with support for
 * argument value type conversion from values retrieved from a argument
 * attribute source.
 * 
 * @author Keith Donald
 */
public class MethodInvoker {

	private static final Log logger = LogFactory.getLog(MethodInvoker.class);

	/**
	 * Conversion service for converting arguments to the neccessary type if
	 * required.
	 */
	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * A cache of invoked bean methods, keyed weakly.
	 */
	private CachingMapDecorator methodCache = new CachingMapDecorator(true) {
		public Object create(Object key) {
			return ((MethodKey) key).getMethod();
		}
	};

	/**
	 * Sets the conversion service to convert argument values as needed.
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Invoke the method on the bean provided. Argument values are pulled from
	 * the provided argument source.
	 * @param signature the definition of the method to invoke, including the
	 * method name and the method argument types
	 * @param bean the bean to invoke
	 * @param argumentSource the source for method arguments
	 * @return the invoked method's return value
	 * @throws MethodInvocationException the method could not be invoked
	 */
	public Object invoke(MethodSignature signature, Object bean, Object argumentSource)
			throws MethodInvocationException {
		Parameters parameters = signature.getParameters();
		Object[] arguments = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			Parameter parameter = parameters.getParameter(i);
			Object argument = parameter.evaluateArgument(argumentSource, null);
			arguments[i] = applyTypeConversion(argument, parameter.getType());
		}
		Class[] parameterTypes = parameters.getTypesArray();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i] == null) {
				Object argument = arguments[i];
				if (argument != null) {
					parameterTypes[i] = argument.getClass();
				}
			}
		}
		MethodKey key = new MethodKey(bean.getClass(), signature.getMethodName(), parameterTypes);
		try {
			Method method = (Method) methodCache.get(key);
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking method with signature [" + key + "] with arguments "
						+ StylerUtils.style(arguments) + " on bean [" + bean + "]");

			}
			Object returnValue = method.invoke(bean, arguments);
			if (logger.isDebugEnabled()) {
				logger.debug("Invoked method with signature [" + key + "] returned value [" + returnValue + "]");
			}
			return returnValue;
		}
		catch (Exception e) {
			throw new MethodInvocationException(signature, arguments, e);
		}
	}

	/**
	 * Apply type conversion on the event parameter if neccessary
	 * 
	 * @param parameterValue the raw argument value
	 * @param targetType the target type for the matching method argument
	 * @return the converted method argument
	 */
	protected Object applyTypeConversion(Object parameterValue, Class targetType) {
		if (parameterValue == null || targetType == null) {
			return parameterValue;
		}
		return conversionService.getConversionExecutor(parameterValue.getClass(), targetType).execute(parameterValue);
	}
}