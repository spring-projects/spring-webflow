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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.AnnotatedAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link TransitionCriteriaChain}.
 * 
 * @author Erwin Vervaet
 */
public class TransitionCriteriaChainTests {

	private TransitionCriteriaChain chain;
	private MockRequestContext context;

	@BeforeEach
	public void setUp() throws Exception {
		chain = new TransitionCriteriaChain();
		context = new MockRequestContext();
	}

	@Test
	public void testEmptyChain() {
		assertTrue(chain.test(context));
	}

	@Test
	public void testAllTrue() {
		TestTransitionCriteria criteria1 = new TestTransitionCriteria(true);
		TestTransitionCriteria criteria2 = new TestTransitionCriteria(true);
		TestTransitionCriteria criteria3 = new TestTransitionCriteria(true);
		chain.add(criteria1);
		chain.add(criteria2);
		chain.add(criteria3);
		assertTrue(chain.test(context));
		assertTrue(criteria1.tested);
		assertTrue(criteria2.tested);
		assertTrue(criteria3.tested);
	}

	@Test
	public void testWithFalse() {
		TestTransitionCriteria criteria1 = new TestTransitionCriteria(true);
		TestTransitionCriteria criteria2 = new TestTransitionCriteria(false);
		TestTransitionCriteria criteria3 = new TestTransitionCriteria(true);
		chain.add(criteria1);
		chain.add(criteria2);
		chain.add(criteria3);
		assertFalse(chain.test(context));
		assertTrue(criteria1.tested);
		assertTrue(criteria2.tested);
		assertFalse(criteria3.tested);
	}

	@Test
	public void testCriteriaChainForNoActions() {
		TransitionCriteria actionChain = TransitionCriteriaChain.criteriaChainFor((Action[]) null);
		assertTrue(actionChain.test(context));
	}

	@Test
	public void testCriteriaChainForActions() {
		AnnotatedAction[] actions = new AnnotatedAction[] { new AnnotatedAction(new TestAction(true)),
				new AnnotatedAction(new TestAction(false)) };
		TransitionCriteria actionChain = TransitionCriteriaChain.criteriaChainFor(actions);
		assertFalse(actionChain.test(context));
	}

	private static class TestTransitionCriteria implements TransitionCriteria {

		public boolean tested = false;
		private boolean result;

		public TestTransitionCriteria(boolean result) {
			this.result = result;
		}

		public boolean test(RequestContext context) {
			tested = true;
			return result;
		}
	}

	private static class TestAction implements Action {

		private boolean result;

		public TestAction(boolean result) {
			this.result = result;
		}

		public Event execute(RequestContext context) throws Exception {
			if (result) {
				return new EventFactorySupport().success(this);
			} else {
				return new EventFactorySupport().error(this);
			}
		}
	}
}
