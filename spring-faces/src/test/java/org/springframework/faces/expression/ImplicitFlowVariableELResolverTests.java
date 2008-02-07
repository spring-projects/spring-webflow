package org.springframework.faces.expression;

import javax.el.ELContext;

import junit.framework.TestCase;

import org.springframework.binding.expression.el.DefaultELContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class ImplicitFlowVariableELResolverTests extends TestCase {

	RequestContext requestContext = new MockRequestContext();
	ELContext elContext = new DefaultELContext(new ImplicitFlowVariableELResolver(), null, null);

	public void setUp() {
		RequestContextHolder.setRequestContext(requestContext);
	}

	public void testGetValue_UnknownVariable() {
		Object result = elContext.getELResolver().getValue(elContext, null, "foo");
		assertNull(result);
		assertFalse(elContext.isPropertyResolved());
	}

	public void testGetValue_FlashScope() {
		Object result = elContext.getELResolver().getValue(elContext, null, "flashScope");
		assertNotNull(result);
		assertTrue(elContext.isPropertyResolved());
		assertSame(requestContext.getFlashScope(), result);
	}

	public void testGetValue_FlowScope() {
		Object result = elContext.getELResolver().getValue(elContext, null, "flowScope");
		assertNotNull(result);
		assertTrue(elContext.isPropertyResolved());
		assertSame(requestContext.getFlowScope(), result);
	}

	public void testGetValue_ConversationScope() {
		Object result = elContext.getELResolver().getValue(elContext, null, "conversationScope");
		assertNotNull(result);
		assertTrue(elContext.isPropertyResolved());
		assertSame(requestContext.getConversationScope(), result);
	}
}
