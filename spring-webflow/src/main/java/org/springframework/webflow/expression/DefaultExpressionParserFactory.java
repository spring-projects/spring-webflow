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
package org.springframework.webflow.expression;

import javax.el.ExpressionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.el.DefaultExpressionFactoryUtils;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;

/**
 * Static factory that returns the default {@link ExpressionParser} used by Spring Web Flow. Marked final with a private
 * constructor to prevent subclassing.
 * <p>
 * This factory employs the following algorithm when the returned ExpressionParser instance is used for the first time:
 * <ul>
 * <li>If a Unified EL implementation is configured for the VM, make a {@link WebFlowELExpressionParser} the default.
 * <li>If no Unified EL implementation is configured and OGNL is configured, make a {@link WebFlowOgnlExpressionParser}
 * the default.
 * <li>If neither Unified EL or OGNL are configured, throw an IllegalStateException with a nice error message.
 * </ul>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public final class DefaultExpressionParserFactory {

	private static final Log logger = LogFactory.getLog(DefaultExpressionParserFactory.class);

	/**
	 * The singleton instance of the default expression parser.
	 */
	private static ExpressionParser INSTANCE;

	// static factory - not instantiable
	private DefaultExpressionParserFactory() {
	}

	/**
	 * Returns the default expression parser for Spring Web Flow. The returned instance is a cached thread-safe object.
	 * @return the expression parser
	 */
	public static synchronized ExpressionParser getExpressionParser() {
		return getDefaultExpressionParser();
	}

	/**
	 * Returns the default expression parser for Spring Web Flow configured with the provided ConversionService for type
	 * conversion. The returned instance is a thread-safe object.
	 * @param conversionService the conversionService
	 * @return the expression parser
	 */
	public static synchronized ExpressionParser getExpressionParser(final ConversionService conversionService) {
		return createDefaultExpressionParser(conversionService);
	}

	/**
	 * Returns the default expression parser, creating it if necessary.
	 * @return the default expression parser
	 */
	private static synchronized ExpressionParser getDefaultExpressionParser() {
		if (INSTANCE == null) {
			INSTANCE = createDefaultExpressionParser(null);
			if (logger.isDebugEnabled()) {
				logger.debug("Initialized shared default Web Flow ExpressionParser " + INSTANCE);
			}
		}
		return INSTANCE;
	}

	/**
	 * Create the default expression parser. This implementation tries EL first, then OGNL if EL is not configured.
	 * @return the default Web Flow expression parser
	 */
	private static ExpressionParser createDefaultExpressionParser(ConversionService conversionService)
			throws IllegalStateException {
		try {
			ExpressionFactory elFactory = DefaultExpressionFactoryUtils.createExpressionFactory();
			WebFlowELExpressionParser expressionParser = new WebFlowELExpressionParser(elFactory);
			if (conversionService != null) {
				expressionParser.setConversionService(conversionService);
			}
			return expressionParser;
		} catch (Exception e) {
			try {
				ClassUtils.forName("ognl.Ognl", DefaultExpressionParserFactory.class.getClassLoader());
				WebFlowOgnlExpressionParser expressionParser = new WebFlowOgnlExpressionParser();
				if (conversionService != null) {
					expressionParser.setConversionService(conversionService);
				}
				return expressionParser;
			} catch (ClassNotFoundException ex) {
				IllegalStateException ise = new IllegalStateException(
						"Unable to create the default expression parser for Spring Web Flow: Neither a Unified EL implementation or OGNL could be found.");
				ise.initCause(ex);
				throw ise;
			} catch (NoClassDefFoundError ex) {
				IllegalStateException ise = new IllegalStateException(
						"Unable to create the default expression parser for Spring Web Flow: Neither a Unified EL implementation or OGNL could be found.");
				ise.initCause(ex);
				throw ise;
			}
		}
	}
}