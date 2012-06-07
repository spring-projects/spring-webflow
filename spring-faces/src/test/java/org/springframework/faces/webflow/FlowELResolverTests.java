package org.springframework.faces.webflow;

import javax.el.ELContext;

import junit.framework.TestCase;

import org.apache.myfaces.test.el.MockELContext;
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
public class FlowELResolverTests extends TestCase {

	private final FlowELResolver resolver = new FlowELResolver();

	private final RequestContext requestContext = new MockRequestContext();

	private final ELContext elContext = new MockELContext();

	protected void setUp() throws Exception {
		RequestContextHolder.setRequestContext(this.requestContext);
	}

	protected void tearDown() throws Exception {
		RequestContextHolder.setRequestContext(null);
	}

	public void testRequestContextResolve() throws Exception {
		Object actual = this.resolver.getValue(this.elContext, null, "flowRequestContext");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(this.requestContext, actual);
	}

	public void testImplicitFlowResolve() throws Exception {
		Object actual = this.resolver.getValue(this.elContext, null, "flowScope");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(this.requestContext.getFlowScope(), actual);
	}

	public void testFlowResourceResolve() throws Exception {
		ApplicationContext applicationContext = new StaticWebApplicationContext();
		((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		Object actual = this.resolver.getValue(this.elContext, null, "resourceBundle");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertSame(applicationContext, actual);
	}

	public void testScopeResolve() throws Exception {
		this.requestContext.getFlowScope().put("test", "test");
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertEquals("test", actual);
	}

	public void testMapAdaptableResolve() throws Exception {
		LocalAttributeMap<String> base = new LocalAttributeMap<String>();
		base.put("test", "test");
		Object actual = this.resolver.getValue(this.elContext, base, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertEquals("test", actual);
	}

	public void testBeanResolveWithRequestContext() throws Exception {
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
		applicationContext.registerSingleton("test", Bean.class);
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertTrue(this.elContext.isPropertyResolved());
		assertNotNull(actual);
		assertTrue(actual instanceof Bean);
	}

	public void testBeanResolveWithoutRequestContext() throws Exception {
		RequestContextHolder.setRequestContext(null);
		Object actual = this.resolver.getValue(this.elContext, null, "test");
		assertFalse(this.elContext.isPropertyResolved());
		assertNull(actual);
	}

	public static class Bean {
	}
}
