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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.webflow.test.MockAction;
import org.springframework.webflow.test.MockRequestContext;

public class ActionTransitionCriteriaTests extends TestCase {

	private MockAction action;

	private ActionTransitionCriteria criteria;

	protected void setUp() throws Exception {
		action = new MockAction();
		criteria = new ActionTransitionCriteria(action);
	}

	public void testExecuteSuccessResult() throws Exception {
		MockRequestContext context = new MockRequestContext();
		assertTrue(criteria.test(context));
	}

	public void testExecuteTrueResult() throws Exception {
		action.setResultEventId("true");
		MockRequestContext context = new MockRequestContext();
		assertTrue(criteria.test(context));
	}

	public void testExecuteYesResult() throws Exception {
		action.setResultEventId("yes");
		MockRequestContext context = new MockRequestContext();
		assertTrue(criteria.test(context));
	}

	public void testExecuteErrorResult() throws Exception {
		action.setResultEventId("whatever");
		MockRequestContext context = new MockRequestContext();
		assertFalse(criteria.test(context));
	}

}