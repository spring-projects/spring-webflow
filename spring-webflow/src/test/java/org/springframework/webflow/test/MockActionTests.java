package org.springframework.webflow.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;

public class MockActionTests {
	@Test
	public void testMockActionExecute() {
		MockAction action = new MockAction();
		Event e = action.execute(new MockRequestContext());
		assertEquals("success", e.getId());
		assertTrue(e.getAttributes().isEmpty());
	}

	@Test
	public void testMockActionExecuteCustomResult() {
		MockAction action = new MockAction("foo");
		Event e = action.execute(new MockRequestContext());
		assertEquals("foo", e.getId());
		assertTrue(e.getAttributes().isEmpty());
	}

	@Test
	public void testMockActionExecuteCustomResultAttributes() {
		MockAction action = new MockAction("foo");
		LocalAttributeMap<Object> resultAttributes = new LocalAttributeMap<>();
		resultAttributes.put("bar", "baz");
		action.setResultAttributes(resultAttributes);
		Event e = action.execute(new MockRequestContext());
		assertEquals("foo", e.getId());
		assertFalse(e.getAttributes().isEmpty());
		assertEquals(e.getAttributes().get("bar"), "baz");
	}

}
