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

import junit.framework.TestCase;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.AccessException;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

public class FlowVariablePropertyAccessorTests extends TestCase {

	private FlowVariablePropertyAccessor accessor = new FlowVariablePropertyAccessor();

	private MockRequestContext requestContext;

	protected void setUp() throws Exception {
		requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
	}

	protected void tearDown() throws Exception {
		RequestContextHolder.setRequestContext(null);
	}

	public void testFlowRequestContext() throws Exception {
		assertTrue(accessor.canRead(null, null, "flowRequestContext"));
		assertEquals(requestContext, accessor.read(null, null, "flowRequestContext").getValue());
	}

	public void testCurrentUser() throws Exception {
		MockExternalContext externalContext = (MockExternalContext) requestContext.getExternalContext();
		externalContext.setCurrentUser("joe");
		assertTrue(accessor.canRead(null, null, "currentUser"));
		assertEquals(externalContext.getCurrentUser(), accessor.read(null, null, "currentUser").getValue());
	}

	public void testResourceBundle() throws Exception {
		Flow flow = (Flow) requestContext.getActiveFlow();
		flow.setApplicationContext(new StaticApplicationContext());
		assertTrue(accessor.canRead(null, null, "resourceBundle"));
		assertNotNull(accessor.read(null, null, "resourceBundle").getValue());
		assertEquals(requestContext.getActiveFlow().getApplicationContext(), accessor
				.read(null, null, "resourceBundle").getValue());
	}

	public void testWrite() throws Exception {
		assertFalse(accessor.canWrite(null, null, "anyName"));
		try {
			accessor.write(null, null, "anyName", "anyValue");
		} catch (AccessException e) {
		}
	}
}
