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
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.CachingMapDecorator;

/**
 * A helper for invoking typed methods on arbitrary objects, with support for argument value type conversion from values
 * retrieved from an argument attribute source, and conversion of the result to a desired type.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class MethodInvoker {

	private static final Log logger = LogFactory.getLog(MethodInvoker.class);

	/**
	 * Conversion service for converting arguments to the necessary type if required.
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
	 * Invoke the method on the bean provided. Argument values are pulled from the provided argument source.
	 * @param signature the definition of the method to invoke, including the method name and the method argument types
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
			Object argument = parameter.evaluateArgument(argumentSource);
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
		} catch (InvocationTargetException e) {
			throw new MethodInvocationException(signature, arguments, e.getTargetException());
		} catch (Exception e) {
			throw new MethodInvocationException(signature, arguments, e);
		}
	}

	/**
	 * Apply type conversion on the supplied value
	 * 
	 * @param value the raw value to be converted
	 * @param targetType the target type for the conversion
	 * @return the converted result
	 */
	protected Object applyTypeConversion(Object value, Class targetType) {
		if (value == null || targetType == null) {
			return value;
		}
		return conversionService.getConversionExecutor(value.getClass(), targetType).execute(value);
	}
}