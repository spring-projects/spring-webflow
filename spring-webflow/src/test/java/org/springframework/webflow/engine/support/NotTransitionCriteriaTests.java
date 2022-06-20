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
package org.springframework.webflow.engine.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link NotTransitionCriteria}.
 * 
 * @author Erwin Vervaet
 */
public class NotTransitionCriteriaTests {

	@Test
	public void testNull() {
		try {
			new NotTransitionCriteria(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testNegation() {
		assertFalse(new NotTransitionCriteria(WildcardTransitionCriteria.INSTANCE).test(new MockRequestContext()));
	}

}
