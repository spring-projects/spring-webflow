package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.test.MockRequestContext;

public class DefaultTargetResolverTests extends TestCase {
	public void testResolveState() {
		DefaultTargetStateResolver resolver = new DefaultTargetStateResolver("mockState");
		MockRequestContext context = new MockRequestContext();
		Transition transition = new Transition();
		assertEquals("mockState", resolver.resolveTargetState(transition, null, context).getId());
	}

	public void testResolveStateExpression() {
		DefaultTargetStateResolver resolver = new DefaultTargetStateResolver(new StaticExpression("mockState"));
		MockRequestContext context = new MockRequestContext();
		Transition transition = new Transition();
		assertEquals("mockState", resolver.resolveTargetState(transition, null, context).getId());
	}

	public void testResolveStateNull() {
		DefaultTargetStateResolver resolver = new DefaultTargetStateResolver((String) null);
		MockRequestContext context = new MockRequestContext();
		Transition transition = new Transition();
		assertEquals(null, resolver.resolveTargetState(transition, null, context));
	}

}
