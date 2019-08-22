package org.springframework.faces.webflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.el.ELContext;

import org.apache.myfaces.test.el.MockELContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Tests for {@link FlowELResolver}.
 * 
 * @author Phillip Webb
 */
public class FlowELResolverTests {

	private final FlowELResolver resolver = new FlowELResolver();

	private final RequestContext requestContext = new MockRequestContext();

	private final ELContext elContext = new MockELContext();

	@Before
	public void setUp() {
		RequestContextHolder.setRequestContext(this.requestContext);
	}

	@After
	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testRequestContextResolve() {
		Object actual = this.resolver.getValue(this.elContext, null, "flowRequestContext");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(this.requestContext, actual);
	}

	@Test
	public void testImplicitFlowResolve() {
		Object actual = this.resolver.getValue(this.elContext, null, "flowScope");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(this.requestContext.getFlowScope(), actual);
	}

	@Test
	public void testFlowResourceResolve() {
		ApplicationContext applicationContext = new StaticWebApplicationContext();
		((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		Object actual = this.resolver.getValue(this.elContext, null, "resourceBundle");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(applicationContext, actual);
	}

	@Test
	public void testScopeResolve() {
		this.requestContext.getFlowScope().put("test", "test");
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertEquals("test", actual);
	}

	@Test
	public void testMapAdaptableResolve() {
		LocalAttributeMap<String> base = new LocalAttributeMap<>();
		base.put("test", "test");
		Object actual = this.resolver.getValue(this.elContext, base, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertEquals("test", actual);
	}

	@Test
	public void testBeanResolveWithRequestContext() {
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		applicationContext.registerSingleton("test", Bean.class);
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertTrue(actual instanceof Bean);
	}

	@Test
	public void testBeanResolveWithoutRequestContext() {
		RequestContextHolder.setRequestContext(null);
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertFalse(this.elContext.isPropertyResolved());
		assertNull(actual);
	}

	public static class Bean {
	}
}
