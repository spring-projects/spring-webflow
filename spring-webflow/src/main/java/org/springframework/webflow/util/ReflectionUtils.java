/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple utility class for working with the java reflection API. Only intended for internal use. Will likely disappear
 * in a future release of Spring Web Flow and simply rely on {@link org.springframework.util.ReflectionUtils} if
 * necessary.
 * 
 * @author Keith Donald
 */
public class ReflectionUtils {

	/**
	 * Invoke the specified {@link Method} against the supplied target object with no arguments. The target object can
	 * be <code>null</code> when invoking a static {@link Method}. All exceptions are treated as fatal and will be
	 * converted to unchecked exceptions.
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 * @throws RuntimeException when something goes wrong invoking the method or when the method itself throws an
	 * exception
	 */
	public static Object invokeMethod(Method method, Object target) throws RuntimeException {
		return invokeMethod(method, target, null);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object with the supplied arguments. The target
	 * object can be null when invoking a static {@link Method}. All exceptions are treated as fatal and will be
	 * converted to unchecked exceptions.
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 * @throws RuntimeException when something goes wrong invoking the method or when the method itself throws an
	 * exception
	 */
	public static Object invokeMethod(Method method, Object target, Object[] args) throws RuntimeException {
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": "
					+ ex.getMessage());
		} catch (InvocationTargetException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": "
					+ ex.getMessage());
		}
	}

	/**
	 * Handle the given reflection exception. Should only be called if no checked exception is expected to be thrown by
	 * the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of an InvocationTargetException with such a root cause.
	 * Throws an IllegalStateException with an appropriate message else.
	 * @param ex the reflection exception to handle
	 */
	private static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": "
				+ ex.getMessage());
	}

	/**
	 * Handle the given invocation target exception. Should only be called if no checked exception is expected to be
	 * thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of such a root cause. Throws an IllegalStateException
	 * else.
	 * @param ex the invocation target exception to handle
	 */
	private static void handleInvocationTargetException(InvocationTargetException ex) {
		if (ex.getTargetException() instanceof RuntimeException) {
			throw (RuntimeException) ex.getTargetException();
		}
		if (ex.getTargetException() instanceof Error) {
			throw (Error) ex.getTargetException();
		}
		throw new IllegalStateException("Unexpected exception thrown by method - "
				+ ex.getTargetException().getClass().getName() + ": " + ex.getTargetException().getMessage());
	}
}