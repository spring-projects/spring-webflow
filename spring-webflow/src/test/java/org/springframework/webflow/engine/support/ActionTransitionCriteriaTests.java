/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for the ActionTransitionCriteria class.
 * 
 * @author Ulrik Sandberg
 */
public class ActionTransitionCriteriaTests extends TestCase {

	private Action actionMock;

	private ActionTransitionCriteria tested;

	protected void setUp() throws Exception {
		super.setUp();
		actionMock = (Action)EasyMock.createMock(Action.class);
		tested = new ActionTransitionCriteria(actionMock);
	}

	public void testGetTrueEventId() {
		String id = tested.getTrueEventId();
		assertEquals("success", id);
	}

	public void testSetTrueEventId() {
		tested.setTrueEventId("something");
		String id = tested.getTrueEventId();
		assertEquals("something", id);
	}

	public void testGetAction() {
		Action action = tested.getAction();
		assertSame(actionMock, action);
	}

	public void testTest() throws Exception {
		MockRequestContext mockRequestContext = new MockRequestContext();
		EasyMock.expect(actionMock.execute(mockRequestContext)).andReturn(new Event(this, "success"));
		EasyMock.replay(new Object[] { actionMock });
		boolean result = tested.test(mockRequestContext);
		EasyMock.verify(new Object[] { actionMock });
		assertEquals(true, result);
	}
}