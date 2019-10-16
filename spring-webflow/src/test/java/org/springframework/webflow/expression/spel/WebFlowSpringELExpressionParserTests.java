/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.expression.spel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

public class WebFlowSpringELExpressionParserTests {

	private ExpressionParser parser = new WebFlowSpringELExpressionParser(new SpelExpressionParser());

	private MockRequestContext requestContext;

	@BeforeEach
	public void setUp() throws Exception {
		requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
	}

	@AfterEach
	public void tearDown() throws Exception {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testResourceBundleRead() {
		MockExternalContext externalContext = (MockExternalContext) requestContext.getExternalContext();
		externalContext.setLocale(Locale.ENGLISH);

		StaticApplicationContext applicationContext = new StaticApplicationContext();
		StaticMessageSource messageSource = applicationContext.getStaticMessageSource();
		messageSource.addMessage("myCode", externalContext.getLocale(), "myCode message");
		messageSource.addMessage("myCode.myCode", externalContext.getLocale(), "myCode myCode message");
		applicationContext.refresh();

		Flow flow = (Flow) requestContext.getActiveFlow();
		flow.setApplicationContext(applicationContext);

		String expressionString = "#{resourceBundle.myCode}";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().template());
		assertEquals("myCode message", exp.getValue(requestContext));

		expressionString = "#{resourceBundle['myCode']}";
		exp = parser.parseExpression(expressionString, new FluentParserContext().template());
		assertEquals("myCode message", exp.getValue(requestContext));

		expressionString = "#{resourceBundle['myCode.myCode']}";
		exp = parser.parseExpression(expressionString, new FluentParserContext().template());
		assertEquals("myCode myCode message", exp.getValue(requestContext));
	}

}
