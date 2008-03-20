package org.springframework.webflow.expression.el;

import java.security.Principal;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.el.DefaultExpressionFactoryUtils;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockRequestContext;

public class WebFlowELExpressionParserTests extends TestCase {
	private WebFlowELExpressionParser parser = new WebFlowELExpressionParser(DefaultExpressionFactoryUtils
			.createExpressionFactory());

	public void testResolveMap() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(AttributeMap.class));
		Expression exp2 = parser.parseExpression("bogus", new ParserContextImpl().eval(AttributeMap.class));
		assertEquals("bar", exp.getValue(map));
		assertEquals(null, exp2.getValue(map));
	}

	public void testSetMap() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression exp2 = parser.parseExpression("bogus", new ParserContextImpl().eval(MutableAttributeMap.class));
		exp.setValue(map, "baz");
		exp2.setValue(map, "new");
		assertEquals("baz", exp.getValue(map));
		assertEquals("new", exp2.getValue(map));
	}

	public void testResolveFlowRequestContext() {
		MockRequestContext context = new MockRequestContext();
		Expression exp = parser.parseExpression("flowRequestContext", new ParserContextImpl()
				.eval(RequestContext.class));
		assertSame(context, exp.getValue(context));
	}

	public void testResolveCurrentUser() {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setCurrentUser("Keith");
		Expression exp = parser.parseExpression("currentUser", new ParserContextImpl().eval(RequestContext.class));
		assertEquals("Keith", ((Principal) exp.getValue(context)).getName());
	}

	public void testResolveRequestScope() {
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetRequestScope() {
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveFlashScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlashScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetFlashScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlashScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveFlowScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetFlowScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveConversationScope() {
		MockRequestContext context = new MockRequestContext();
		context.getConversationScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetConversationScope() {
		MockRequestContext context = new MockRequestContext();
		context.getConversationScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new ParserContextImpl().eval(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveSpringBean() {
		MockRequestContext context = new MockRequestContext();
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("testBean", new TestBean());
		context.getRootFlow().setBeanFactory(beanFactory);
		Expression exp = parser.parseExpression("testBean", new ParserContextImpl().eval(RequestContext.class));
		assertSame(beanFactory.getBean("testBean"), exp.getValue(context));
	}

	public void testResolveAction() {
		MockRequestContext context = new MockRequestContext();
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("action", new TestAction());
		context.getRootFlow().setBeanFactory(beanFactory);
		Expression exp = parser.parseExpression("action", new ParserContextImpl().eval(RequestContext.class));
		assertSame(beanFactory.getBean("action"), exp.getValue(context));
	}

	public void testResolveMultiAction() {
		MockRequestContext context = new MockRequestContext();
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("multiAction", new FormAction());
		context.getRootFlow().setBeanFactory(beanFactory);
		Expression exp = parser.parseExpression("multiAction.setupForm", new ParserContextImpl()
				.eval(RequestContext.class));
		AnnotatedAction action = (AnnotatedAction) exp.getValue(context);
		assertSame(beanFactory.getBean("multiAction"), action.getTargetAction());
		assertEquals("setupForm", action.getMethod());
	}

}
