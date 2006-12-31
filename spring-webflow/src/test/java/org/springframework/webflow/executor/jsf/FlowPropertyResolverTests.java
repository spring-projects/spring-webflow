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
package org.springframework.webflow.executor.jsf;

import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockFlowSession;

/**
 * @author Colin Sampaleanu
 * @since 1.0
 */
public class FlowPropertyResolverTests extends TestCase {

	private FlowPropertyResolver resolver;

	private FlowExecution flowEx;

	protected void setUp() throws Exception {
		resolver = new FlowPropertyResolver(new OriginalPropertyResolver());
		flowEx = (FlowExecution)EasyMock.createMock(FlowExecution.class);
	}

	protected void tearDown() throws Exception {
		resolver = null;
	}

	public void testGetTypeBaseIndex() {
		Class type = resolver.getType(flowEx, 22);
		assertNull("can't get property from flow via index", type);
	}

	public void testGetTypeBaseProperty() {
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getScope().put("name", "joe");
		flowEx.getActiveSession();
		EasyMock.expectLastCall().andReturn(flowSession);
		EasyMock.replay(new Object[] { flowEx });
		Class type = resolver.getType(flowEx, "name");
		assertTrue("returned type must match property type", type.equals(String.class));
	}

	public void testGetValueBaseIndex() {
		try {
			resolver.getValue(flowEx, 2);
			fail("not legal to get flow property by index");
		}
		catch (ReferenceSyntaxException e) {
			// expected
		}
	}

	public void testGetValueBaseProperty() {
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getScope().put("name", "joe");
		flowEx.getActiveSession();
		EasyMock.expectLastCall().andReturn(flowSession);
		EasyMock.replay(new Object[] { flowEx });
		Object value = resolver.getValue(flowEx, "name");
		assertTrue("must return expected property", value.equals("joe"));
	}

	public void testSetValueBaseIndex() {
		try {
			resolver.setValue(flowEx, 2, "whatever");
			fail("not legal to set flow property by index");
		}
		catch (ReferenceSyntaxException e) {
			// expected
		}
	}

	public void testSetValueBaseProperty() {
		MockFlowSession flowSession = new MockFlowSession();
		flowEx.getActiveSession();
		EasyMock.expectLastCall().andReturn(flowSession);
		EasyMock.replay(new Object[] { flowEx });
		resolver.setValue(flowEx, "name", "joe");
		assertTrue(flowSession.getScope().get("name").equals("joe"));
	}

	private static class OriginalPropertyResolver extends PropertyResolver {

		public Class getType(Object base, int index) throws EvaluationException, PropertyNotFoundException {
			return Object.class;
		}

		public Class getType(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
			return Object.class;
		}

		public Object getValue(Object base, int index) throws EvaluationException, PropertyNotFoundException {
			return new String("Some value");
		}

		public Object getValue(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
			return new String("Some value");
		}

		public boolean isReadOnly(Object base, int index) throws EvaluationException, PropertyNotFoundException {
			return false;
		}

		public boolean isReadOnly(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
			return false;
		}

		public void setValue(Object base, int index, Object value) throws EvaluationException,
				PropertyNotFoundException {
		}

		public void setValue(Object base, Object property, Object value) throws EvaluationException,
				PropertyNotFoundException {
		}
	}
}