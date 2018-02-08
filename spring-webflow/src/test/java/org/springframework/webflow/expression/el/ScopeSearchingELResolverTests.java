package org.springframework.webflow.expression.el;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELResolver;

import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class ScopeSearchingELResolverTests extends FlowDependentELResolverTestCase {

	public void testGetType_RequestScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getRequestScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(MyBean.class, context.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_FlashScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(MyBean.class, context.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_ViewScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		initView(requestContext);

		requestContext.getViewScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(MyBean.class, context.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_FlowScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlowScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(MyBean.class, context.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_ConversationScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getConversationScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(MyBean.class, context.getELResolver().getType(context, null, getBaseVariable()));
	}

	public void testGetType_NotFound() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertNull(context.getELResolver().getType(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	public void testGetValue_RequestScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getRequestScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(foo, context.getELResolver().getValue(context, null, getBaseVariable()));
	}

	public void testGetValue_FlashScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(foo, context.getELResolver().getValue(context, null, getBaseVariable()));
	}

	public void testGetValue_ViewScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		initView(requestContext);
		requestContext.getViewScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(foo, context.getELResolver().getValue(context, null, getBaseVariable()));
	}

	public void testGetValue_FlowScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlowScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(foo, context.getELResolver().getValue(context, null, getBaseVariable()));
	}

	public void testGetValue_ConversationScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getConversationScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(foo, context.getELResolver().getValue(context, null, getBaseVariable()));
	}

	public void testGetValue_NotFound() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertNull(context.getELResolver().getValue(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	public void testIsReadOnly_RequestScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getRequestScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testIsReadOnly_FlashScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testIsReadOnly_ViewScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		initView(requestContext);
		requestContext.getViewScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testIsReadOnly_FlowScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlowScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testIsReadOnly_ConversationScope() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getConversationScope().put(getBaseVariable(), foo);
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testIsReadOnly_NotFound() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	public void testSetValue_RequestScope() {
		MyBean foo1 = new MyBean();
		MyBean foo2 = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getRequestScope().put(getBaseVariable(), foo1);
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo2);
		assertSame(foo2, requestContext.getRequestScope().get(getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testSetValue_FlashScope() {
		MyBean foo1 = new MyBean();
		MyBean foo2 = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().put(getBaseVariable(), foo1);
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo2);
		assertSame(foo2, requestContext.getFlashScope().get(getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testSetValue_ViewScope() {
		MyBean foo1 = new MyBean();
		MyBean foo2 = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		initView(requestContext);
		requestContext.getViewScope().put(getBaseVariable(), foo1);
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo2);
		assertSame(foo2, requestContext.getViewScope().get(getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testSetValue_FlowScope() {
		MyBean foo1 = new MyBean();
		MyBean foo2 = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getFlowScope().put(getBaseVariable(), foo1);
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo2);
		assertSame(foo2, requestContext.getFlowScope().get(getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testSetValue_ConversationScope() {
		MyBean foo1 = new MyBean();
		MyBean foo2 = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.getConversationScope().put(getBaseVariable(), foo1);
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo2);
		assertSame(foo2, requestContext.getConversationScope().get(getBaseVariable()));
		assertTrue(context.isPropertyResolved());
	}

	public void testSetValue_NotFound() {
		MyBean foo = new MyBean();
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		context.getELResolver().setValue(context, null, getBaseVariable(), foo);
		assertFalse(context.isPropertyResolved());
	}

	protected String getBaseVariable() {
		return "foo";
	}

	protected List<ELResolver> getCustomResolvers() {
		List<ELResolver> resolvers = new ArrayList<>();
		resolvers.add(new ScopeSearchingELResolver());
		return resolvers;
	}

	public static class MyBean {

		private String bar = "";

		public void setBar(String bar) {
			this.bar = bar;
		}

		public String getBar() {
			return this.bar;
		}
	}
}
