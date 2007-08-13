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
package org.springframework.binding.mapping;

import java.util.HashMap;

import junit.framework.TestCase;

import org.springframework.binding.expression.ognl.OgnlExpressionParser;

/**
 * Unit tests for the {@link org.springframework.binding.mapping.RequiredMapping}.
 */
public class RequiredMappingTests extends TestCase {

	public void testRequired() {
		MappingBuilder builder = new MappingBuilder(new OgnlExpressionParser());
		Mapping mapping = builder.source("foo").target("bar").required().value();
		HashMap source = new HashMap();
		source.put("foo", "baz");
		HashMap target = new HashMap();
		mapping.map(source, target, null);
		assertEquals("baz", target.get("bar"));
	}

	public void testRequiredExceptionOnNull() {
		MappingBuilder builder = new MappingBuilder(new OgnlExpressionParser());
		Mapping mapping = builder.source("foo").target("bar").required().value();
		HashMap source = new HashMap();
		source.put("foo", null);
		HashMap target = new HashMap();
		try {
			mapping.map(source, target, null);
		} catch (RequiredMappingException e) {
		}
	}

	public void testRequiredExceptionOnNoKey() {
		MappingBuilder builder = new MappingBuilder(new OgnlExpressionParser());
		Mapping mapping = builder.source("foo").target("bar").required().value();
		HashMap source = new HashMap();
		HashMap target = new HashMap();
		try {
			mapping.map(source, target, null);
		} catch (RequiredMappingException e) {
		}
	}

}
