package org.springframework.webflow.expression.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class FlowResourceELResolverTests extends FlowDependentELResolverTestCase {

	public void testGetType_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals(getBaseVariable() + " should have a type of MessageSource", MessageSource.class, context
				.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_ResolvableCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals("Message should resolve to a type of String", String.class, context.getELResolver().getType(
				context, ms, "foo.bar"));
	}

	public void testGetType_InvalidCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		try {
			context.getELResolver().getType(context, ms, "foo.baz");
			fail("Message should not be resolvable");
		} catch (PropertyNotFoundException ex) {
			// expected
		}
	}

	public void testGetValue_BaseVariable() {
		MockRequestContext requestContext = new MockRequestContext();
		((Flow) requestContext.getActiveFlow()).setApplicationContext(new StaticWebApplicationContext());
		RequestContextHolder.setRequestContext(requestContext);
		assertTrue(getBaseVariable() + " should resolve to an instance of MessageSource", context.getELResolver()
				.getValue(context, null, getBaseVariable()) instanceof MessageSource);

	}

	public void testGetValue_ResolvableCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals("Message should resolve to a valid message value", "hello", context.getELResolver().getValue(
				context, ms, "foo.bar"));
	}

	public void testGetValue_InvalidCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		try {
			context.getELResolver().getValue(context, ms, "foo.baz");
			fail("Message should not be resolvable");
		} catch (PropertyNotFoundException ex) {
			// expected
		}
	}

	public void testIsReadOnly_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue("isReadOnly should return true for the base variable", context.getELResolver().isReadOnly(context,
				null, getBaseVariable()));
	}

	public void testSetValue_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		try {
			context.getELResolver().setValue(context, null, getBaseVariable(), null);
			fail("setValue should fail for a base variable of " + getBaseVariable());
		} catch (PropertyNotWritableException ex) {
			// expected
		}
	}

	protected String getBaseVariable() {
		return FlowResourceELResolver.RESOURCE_BUNDLE_KEY;
	}

	protected List getCustomResolvers() {
		List resolvers = new ArrayList();
		resolvers.add(new FlowResourceELResolver());
		return resolvers;
	}
}
