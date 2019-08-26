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
package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for the {@link CompositeAction} class.
 * 
 * @author Ulrik Sandberg
 */
public class CompositeActionTests {

	private CompositeAction tested;

	private Action actionMock;

	@BeforeEach
	public void setUp() throws Exception {
		actionMock = EasyMock.createMock(Action.class);
		Action[] actions = new Action[] { actionMock };
		tested = new CompositeAction(actions);
	}

	@Test
	public void testDoExecute() throws Exception {
		MockRequestContext mockRequestContext = new MockRequestContext();
		LocalAttributeMap<Object> attributes = new LocalAttributeMap<>();
		attributes.put("some key", "some value");
		EasyMock.expect(actionMock.execute(mockRequestContext)).andReturn(new Event(this, "some event", attributes));
		EasyMock.replay(actionMock);
		Event result = tested.doExecute(mockRequestContext);
		EasyMock.verify(actionMock);
		assertEquals("some event", result.getId());
		assertEquals(1, result.getAttributes().size());
	}

	@Test
	public void testDoExecuteWithError() throws Exception {
		tested.setStopOnError(true);
		MockRequestContext mockRequestContext = new MockRequestContext();
		EasyMock.expect(actionMock.execute(mockRequestContext)).andReturn(new Event(this, "error"));
		EasyMock.replay(actionMock);
		Event result = tested.doExecute(mockRequestContext);
		EasyMock.verify(actionMock);
		assertEquals("error", result.getId());
	}

	@Test
	public void testDoExecuteWithNullResult() throws Exception {
		tested.setStopOnError(true);
		MockRequestContext mockRequestContext = new MockRequestContext();
		EasyMock.expect(actionMock.execute(mockRequestContext)).andReturn(null);
		EasyMock.replay(actionMock);
		Event result = tested.doExecute(mockRequestContext);
		EasyMock.verify(actionMock);
		assertEquals("success", result.getId(), "Expecting success since no check is performed if null result,");
	}

	@Test
	public void testMultipleActions() throws Exception {
		CompositeAction ca = new CompositeAction(
				new Action() {
					public Event execute(RequestContext context) {
						return new Event(this, "foo");
					}
				},
				new Action() {
					public Event execute(RequestContext context) {
						return new Event(this, "bar");
					}
				});
		assertEquals("bar", ca.execute(new MockRequestContext()).getId(),
				"Result of last executed action should be returned");
	}
}
