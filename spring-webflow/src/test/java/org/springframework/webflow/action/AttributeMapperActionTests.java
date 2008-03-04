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
package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.MappingBuilder;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit test for the {@link AttributeMapperAction}.
 * 
 * @author Erwin Vervaet
 */
public class AttributeMapperActionTests extends TestCase {

	private MappingBuilder mappingBuilder = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());

	public void testMapping() throws Exception {
		DefaultAttributeMapper mapper = new DefaultAttributeMapper();
		mapper.addMapping(mappingBuilder.source("${externalContext.requestParameterMap.foo}")
				.target("${flowScope.bar}").value());
		AttributeMapperAction action = new AttributeMapperAction(mapper);

		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("foo", "value");

		assertTrue(context.getFlowScope().size() == 0);

		action.execute(context);

		assertEquals(1, context.getFlowScope().size());
		assertEquals("value", context.getFlowScope().get("bar"));
	}

	public void testNullIllegalArgument() {
		try {
			new AttributeMapperAction(null);
			fail("Should've thrown illegal argument");
		} catch (IllegalArgumentException e) {

		}
	}
}