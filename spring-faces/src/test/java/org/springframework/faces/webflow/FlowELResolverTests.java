package org.springframework.faces.webflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.el.ELContext;

import org.apache.myfaces.test.el.MockELContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	@BeforeEach
	public void setUp() {
		RequestContextHolder.setRequestContext(this.requestContext);
	}

	@AfterEach
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
