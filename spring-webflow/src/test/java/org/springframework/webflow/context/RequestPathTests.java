package org.springframework.webflow.context;

import junit.framework.TestCase;

import org.springframework.webflow.context.RequestPath;

public class RequestPathTests extends TestCase {
	public void testNewPathParse() {
		RequestPath path = new RequestPath("/users/1");
		assertEquals(2, path.getElementCount());
		assertEquals("users", path.getElement(0));
		assertEquals("1", path.getElement(1));
	}

	public void testNewPathParseTrailingSlash() {
		RequestPath path = new RequestPath("/users/1/");
		assertEquals(2, path.getElementCount());
		assertEquals("users", path.getElement(0));
		assertEquals("1", path.getElement(1));
	}

	public void testNewPathParseNoLeadingSlash() {
		try {
			RequestPath path = new RequestPath("users/1/");
			fail("should have failed");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testOutOfBounds() {
		RequestPath path = new RequestPath("/users/1/");
		assertEquals(2, path.getElementCount());
		assertEquals("users", path.getElement(0));
		try {
			assertEquals("1", path.getElement(2));
			fail("should have failed");
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}

	public void testEmptyPath() {
		RequestPath path = new RequestPath("/");
		assertEquals(1, path.getElementCount());
		assertEquals("", path.getElement(0));
	}

	public void testSinglePathElement() {
		RequestPath path = new RequestPath("/users");
		assertEquals(1, path.getElementCount());
		assertEquals("users", path.getElement(0));
	}

	public void testToString() {
		RequestPath path2 = new RequestPath("/users");
		RequestPath path3 = new RequestPath("/users/1/foo/bar");
		assertEquals("/users", path2.toString());
		assertEquals("/users/1/foo/bar", path3.toString());
	}

	public void testPopElement() {
		RequestPath path = new RequestPath("/users/1");
		assertEquals(2, path.getElementCount());
		path = path.pop(1);
		assertEquals(1, path.getElementCount());
		assertEquals("/1", path.toString());
	}

	public void testPopAllElements() {
		RequestPath path = new RequestPath("/users/1");
		assertEquals(2, path.getElementCount());
		path = path.pop(2);
		assertNull(path);
	}

	public void testPopEmptyPath() {
		RequestPath path = new RequestPath("/");
		assertEquals(1, path.getElementCount());
		path = path.pop();
		assertNull(path);
	}
}
