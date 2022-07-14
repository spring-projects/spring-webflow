package org.springframework.webflow.expression.el;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.el.ELResolver;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class FlowResourceELResolverTests extends FlowDependentELResolverTestCase {

	@AfterEach
	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testGetType_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals(MessageSource.class, context.getELResolver().getType(context, null, getBaseVariable()),
				getBaseVariable() + " should have a type of MessageSource");
	}

	@Test
	public void testGetType_ResolvableCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals(String.class, context.getELResolver().getType(context, ms, "foo.bar"),
				"Message should resolve to a type of String");
	}

	@Test
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

	@Test
	public void testGetValue_BaseVariable() {
		MockRequestContext requestContext = new MockRequestContext();
		((Flow) requestContext.getActiveFlow()).setApplicationContext(new StaticWebApplicationContext());
		RequestContextHolder.setRequestContext(requestContext);
		assertTrue(context.getELResolver().getValue(context, null, getBaseVariable()) instanceof MessageSource,
				getBaseVariable() + " should resolve to an instance of MessageSource");

	}

	@Test
	public void testGetValue_ResolvableCode() {
		StaticMessageSource ms = new StaticMessageSource();
		ms.addMessage("foo.bar", Locale.getDefault(), "hello");

		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertEquals("hello", context.getELResolver().getValue(context, ms, "foo.bar"),
				"Message should resolve to a valid message value");
	}

	@Test
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

	@Test
	public void testIsReadOnly_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(context.getELResolver().isReadOnly(context, null, getBaseVariable()),
				"isReadOnly should return true for the base variable");
	}

	@Test
	public void testIsReadOnly_MessageSourceBase() {
		StaticMessageSource ms = new StaticMessageSource();

		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(context.getELResolver().isReadOnly(context, ms, "foo"),
				"isReadOnly should return true when the base is a MessageSource");
	}

	@Test
	public void testSetValue_BaseVariable() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		try {
			context.getELResolver().setValue(context, null, getBaseVariable(), null);
			fail("setValue should fail for a base variable of " + getBaseVariable());
		} catch (PropertyNotWritableException ex) {
			// expected
		}
	}

	@Test
	public void testSetValue_MessageSourceBase() {
		StaticMessageSource ms = new StaticMessageSource();
		RequestContextHolder.setRequestContext(new MockRequestContext());
		try {
			context.getELResolver().setValue(context, ms, "foo", null);
			fail("setValue should fail when the base is a MessageSource");
		} catch (PropertyNotWritableException ex) {
			// expected
		}
	}

	protected String getBaseVariable() {
		return FlowResourceELResolver.RESOURCE_BUNDLE_KEY;
	}

	protected List<ELResolver> getCustomResolvers() {
		List<ELResolver> resolvers = new ArrayList<>();
		resolvers.add(new FlowResourceELResolver());
		return resolvers;
	}
}
