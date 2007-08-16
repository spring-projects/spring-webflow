package org.springframework.webflow.test;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;

public class MockActionTests extends TestCase {
	public void testMockActionExecute() {
		MockAction action = new MockAction();
		Event e = action.execute(new MockRequestContext());
		assertEquals("success", e.getId());
		assertTrue(e.getAttributes().isEmpty());
	}

	public void testMockActionExecuteCustomResult() {
		MockAction action = new MockAction("foo");
		Event e = action.execute(new MockRequestContext());
		assertEquals("foo", e.getId());
		assertTrue(e.getAttributes().isEmpty());
	}

	public void testMockActionExecuteCustomResultAttributes() {
		MockAction action = new MockAction("foo");
		LocalAttributeMap resultAttributes = new LocalAttributeMap();
		resultAttributes.put("bar", "baz");
		action.setResultAttributes(resultAttributes);
		Event e = action.execute(new MockRequestContext());
		assertEquals("foo", e.getId());
		assertFalse(e.getAttributes().isEmpty());
		assertEquals(e.getAttributes().get("bar"), "baz");
	}

}
