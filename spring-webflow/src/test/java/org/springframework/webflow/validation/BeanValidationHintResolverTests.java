/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.webflow.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.groups.Default;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * Test fixture for {@link BeanValidationHintResolver};
 *
 * @author Rossen Stoyanchev
 */
public class BeanValidationHintResolverTests {

	private BeanValidationHintResolver resolver;

	@BeforeEach
	public void setUp() {
		this.resolver = new BeanValidationHintResolver();
	}

	@Test
	public void testResolveFullyQualifiedClassNameHint() {
		String[] hints = new String[] { this.getClass().getName() };
		Class<?>[] resolvedHints = this.resolver.resolveValidationHints(null, "flowId", "state1", hints);

		assertNotNull(resolvedHints);
		assertEquals(1, resolvedHints.length);
		assertEquals(this.getClass(), resolvedHints[0]);
	}

	@Test
	public void testResolveInnterTypeHints() {
		String[] hints = new String[] {"default", "state1", "state2"};
		Class<?>[] resolvedHints = this.resolver.resolveValidationHints(new TestModel(), "flowId", "state1", hints);

		assertNotNull(resolvedHints);
		assertEquals(3, resolvedHints.length);
		assertEquals(Default.class, resolvedHints[0]);
		assertEquals(BaseTestModel.State1.class, resolvedHints[1]);
		assertEquals(TestModel.State2.class, resolvedHints[2]);
	}

	@Test
	public void testResolveHintNoMatch() {
		try {
			this.resolver.resolveValidationHints(null, "flowId", "state1", new String[] { "foo" });
			fail("Expected exception");
		}
		catch (FlowExecutionException ex) {
			// expected
		}
	}

	public static class BaseTestModel {

		private static class State1 {
		}
	}

	public static class TestModel extends BaseTestModel {

		private static class State2 {
		}
	}

}
