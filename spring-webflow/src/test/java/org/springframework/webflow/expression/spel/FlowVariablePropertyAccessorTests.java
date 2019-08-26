/*
 * Copyright 2002-2010 the original author or authors.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.AccessException;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

public class FlowVariablePropertyAccessorTests {

	private FlowVariablePropertyAccessor accessor = new FlowVariablePropertyAccessor();

	private MockRequestContext requestContext;

	@BeforeEach
	public void setUp() throws Exception {
		requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
	}

	protected void tearDown() throws Exception {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testFlowRequestContext() throws Exception {
		assertTrue(accessor.canRead(null, null, "flowRequestContext"));
		assertEquals(requestContext, accessor.read(null, null, "flowRequestContext").getValue());
	}

	@Test
	public void testCurrentUser() throws Exception {
		MockExternalContext externalContext = (MockExternalContext) requestContext.getExternalContext();
		externalContext.setCurrentUser("joe");
		assertTrue(accessor.canRead(null, null, "currentUser"));
		assertEquals(externalContext.getCurrentUser(), accessor.read(null, null, "currentUser").getValue());
	}

	@Test
	public void testResourceBundle() throws Exception {
		Flow flow = (Flow) requestContext.getActiveFlow();
		flow.setApplicationContext(new StaticApplicationContext());
		assertTrue(accessor.canRead(null, null, "resourceBundle"));
		assertNotNull(accessor.read(null, null, "resourceBundle").getValue());
		assertEquals(requestContext.getActiveFlow().getApplicationContext(), accessor
				.read(null, null, "resourceBundle").getValue());
	}

	@Test
	public void testWrite() throws Exception {
		assertFalse(accessor.canWrite(null, null, "anyName"));
		try {
			accessor.write(null, null, "anyName", "anyValue");
		} catch (AccessException e) {
		}
	}
}
