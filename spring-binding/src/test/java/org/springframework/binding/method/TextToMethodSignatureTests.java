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
package org.springframework.binding.method;

import junit.framework.TestCase;

import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.convert.support.TextToExpression;
import org.springframework.binding.expression.support.OgnlExpressionParser;

/**
 * Test case for {@link TextToMethodSignature}.
 * 
 * @author Erwin Vervaet
 */
public class TextToMethodSignatureTests extends TestCase {
	
	private TextToMethodSignature converter;
	
	protected void setUp() throws Exception {
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new TextToExpression(new OgnlExpressionParser()));
		converter = new TextToMethodSignature(conversionService);
	}

	public void testParseNoArguments() {
		MethodSignature signature = (MethodSignature)converter.convert("foo");
		assertEquals("foo", signature.getMethodName());
		assertEquals(0, signature.getParameters().size());
		
		signature = (MethodSignature)converter.convert("foo");
		assertEquals("foo", signature.getMethodName());
		assertEquals(0, signature.getParameters().size());
	}
	
	public void testSingleArgument() {
		MethodSignature signature = (MethodSignature)converter.convert("foo(${flowScope.bar})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(1, signature.getParameters().size());
		assertNull(signature.getParameters().getParameter(0).getType());
		assertEquals("flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
		
		signature = (MethodSignature)converter.convert("foo(${'Foo' + flowScope.bar})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(1, signature.getParameters().size());
		assertEquals("\"Foo\" + flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
	}
	
	public void testSingleArgumentWithType() {
		MethodSignature signature = (MethodSignature)converter.convert("foo(java.lang.String ${flowScope.bar})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(1, signature.getParameters().size());
		assertEquals(String.class, signature.getParameters().getParameter(0).getType());
		assertEquals("flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
		
		signature = (MethodSignature)converter.convert("foo(long ${flowScope.bar})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(1, signature.getParameters().size());
		assertEquals(Long.class, signature.getParameters().getParameter(0).getType());
		assertEquals("flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
	}
	
	public void testMultipleArguments() {
		MethodSignature signature = (MethodSignature)converter.convert(
				"foo(${flowScope.bar}, ${externalContext.requestParameterMap.test})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(2, signature.getParameters().size());
		assertNull(signature.getParameters().getParameter(0).getType());
		assertEquals("flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
		assertNull(signature.getParameters().getParameter(1).getType());
		assertEquals("externalContext.requestParameterMap.test", signature.getParameters().getParameter(1).getName().toString());
	}
	
	public void testMultipleArgumentsWithType() {
		MethodSignature signature = (MethodSignature)converter.convert(
				"foo(long ${flowScope.bar}, java.lang.String ${externalContext.requestParameterMap.test})");
		assertEquals("foo", signature.getMethodName());
		assertEquals(2, signature.getParameters().size());
		assertEquals(Long.class, signature.getParameters().getParameter(0).getType());
		assertEquals("flowScope.bar", signature.getParameters().getParameter(0).getName().toString());
		assertEquals(String.class, signature.getParameters().getParameter(1).getType());
		assertEquals("externalContext.requestParameterMap.test", signature.getParameters().getParameter(1).getName().toString());
	}
}
