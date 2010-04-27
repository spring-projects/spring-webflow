/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.webflow.expression.spel;

import junit.framework.TestCase;

import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public class ScopeSearchingPropertyAccessorTests extends TestCase {

	private ScopeSearchingPropertyAccessor accessor = new ScopeSearchingPropertyAccessor();

	private MockRequestContext requestContext;

	protected void setUp() throws Exception {
		requestContext = new MockRequestContext();
	}

	public void testGetSpecificTargetClasses() throws Exception {
		Class[] classes = accessor.getSpecificTargetClasses();
		assertEquals(1, classes.length);
		assertEquals(RequestContext.class, classes[0]);
	}

	public void testGetValue() throws Exception {
		Object bean = new Object();
		requestContext.getConversationScope().put("myBean", bean);
		TypedValue actual = accessor.read(new StandardEvaluationContext(), requestContext, "myBean");
		assertSame(bean, actual.getValue());

		bean = new Object();
		requestContext.getFlowScope().put("myBean", bean);
		actual = accessor.read(new StandardEvaluationContext(), requestContext, "myBean");
		assertSame(bean, actual.getValue());

		bean = new Object();
		initView(requestContext);
		requestContext.getViewScope().put("myBean", bean);
		actual = accessor.read(new StandardEvaluationContext(), requestContext, "myBean");
		unsetView(requestContext);
		assertSame(bean, actual.getValue());

		bean = new Object();
		requestContext.getFlashScope().put("myBean", bean);
		actual = accessor.read(new StandardEvaluationContext(), requestContext, "myBean");
		assertSame(bean, actual.getValue());

		bean = new Object();
		requestContext.getRequestScope().put("myBean", bean);
		actual = accessor.read(new StandardEvaluationContext(), requestContext, "myBean");
		assertSame(bean, actual.getValue());
	}

	protected void initView(MockRequestContext requestContext) {
		((MockFlowSession) requestContext.getFlowExecutionContext().getActiveSession()).setState(new ViewState(
				requestContext.getRootFlow(), "view", new ViewFactory() {
					public View getView(RequestContext context) {
						throw new UnsupportedOperationException("Not implemented");
					}
				}));
	}

	protected void unsetView(MockRequestContext requestContext) {
		((MockFlowSession) requestContext.getFlowExecutionContext().getActiveSession()).setState(null);
	}

}
