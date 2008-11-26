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
package org.springframework.binding.expression.el;

import javax.el.ExpressionFactory;

import org.springframework.util.ClassUtils;

/**
 * A helper for creating a new expression factory instance using the default expression factory class configured for the
 * VM.
 * 
 * @author Keith Donald
 */
public class DefaultExpressionFactoryUtils {

	// TODO - change default to Spring EL when it becomes available
	private static final String DEFAULT_EXPRESSION_FACTORY = "org.jboss.el.ExpressionFactoryImpl";

	/**
	 * Returns the type of ExpressionFactory configured for this VM.
	 */
	public static String getDefaultExpressionFactoryClassName() {
		return DEFAULT_EXPRESSION_FACTORY;
	}

	/**
	 * Creates a new instance of the expression factory configured for this VM.
	 * @throws IllegalStateException if the ExpressionFactory class cannot be instantiated
	 */
	public static ExpressionFactory createExpressionFactory() throws IllegalStateException {
		Class expressionFactoryClass;
		try {
			expressionFactoryClass = ClassUtils.forName(getDefaultExpressionFactoryClassName(),
					DefaultExpressionFactoryUtils.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			IllegalStateException ise = new IllegalStateException(
					"The default ExpressionFactory class '"
							+ getDefaultExpressionFactoryClassName()
							+ "' could not be found in the classpath.  "
							+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.");
			ise.initCause(e);
			throw ise;
		} catch (NoClassDefFoundError e) {
			IllegalStateException ise = new IllegalStateException(
					"The default ExpressionFactory class '"
							+ getDefaultExpressionFactoryClassName()
							+ "' could not be found in the classpath.  "
							+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.");
			ise.initCause(e);
			throw ise;
		}
		try {
			return (ExpressionFactory) expressionFactoryClass.newInstance();
		} catch (Exception e) {
			IllegalStateException ise = new IllegalStateException("An instance of the default ExpressionFactory '"
					+ getDefaultExpressionFactoryClassName()
					+ "' could not be instantiated.  Check your EL implementation configuration.");
			ise.initCause(e);
			throw ise;
		}
	}
}