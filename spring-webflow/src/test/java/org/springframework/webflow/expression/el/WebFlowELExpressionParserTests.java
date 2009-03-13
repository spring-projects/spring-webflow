package org.springframework.webflow.expression.el;

import java.security.Principal;
import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.el.DefaultExpressionFactoryUtils;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.AnnotatedAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

public class WebFlowELExpressionParserTests extends TestCase {
	private WebFlowELExpressionParser parser = new WebFlowELExpressionParser(DefaultExpressionFactoryUtils
			.createExpressionFactory());

	public void testResolveMap() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(AttributeMap.class));
		Expression exp2 = parser.parseExpression("bogus", new FluentParserContext().evaluate(AttributeMap.class));
		assertEquals("bar", exp.getValue(map));
		assertEquals(null, exp2.getValue(map));
	}

	public void testSetMap() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(MutableAttributeMap.class));
		Expression exp2 = parser
				.parseExpression("bogus", new FluentParserContext().evaluate(MutableAttributeMap.class));
		exp.setValue(map, "baz");
		exp2.setValue(map, "new");
		assertEquals("baz", exp.getValue(map));
		assertEquals("new", exp2.getValue(map));
	}

	public void testResolveFlowRequestContext() {
		MockRequestContext context = new MockRequestContext();
		Expression exp = parser.parseExpression("flowRequestContext", new FluentParserContext()
				.evaluate(RequestContext.class));
		assertSame(context, exp.getValue(context));
	}

	public void testResolveCurrentUser() {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setCurrentUser("Keith");
		Expression exp = parser
				.parseExpression("currentUser", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("Keith", ((Principal) exp.getValue(context)).getName());
	}

	public void testResolveRequestScope() {
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetRequestScope() {
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveFlashScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlashScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetFlashScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlashScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveViewScope() {
		MockRequestControlContext context = new MockRequestControlContext();
		ViewState state = new ViewState(context.getRootFlow(), "view", new StubViewFactory());
		context.setCurrentState(state);
		context.getViewScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetViewScope() {
		MockRequestControlContext context = new MockRequestControlContext();
		ViewState state = new ViewState(context.getRootFlow(), "view", new StubViewFactory());
		context.setCurrentState(state);
		context.getViewScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveFlowScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetFlowScope() {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveConversationScope() {
		MockRequestContext context = new MockRequestContext();
		context.getConversationScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testSetConversationScope() {
		MockRequestContext context = new MockRequestContext();
		context.getConversationScope().put("foo", "bar");
		Expression exp = parser.parseExpression("foo", new FluentParserContext().evaluate(RequestContext.class));
		exp.setValue(context, "baz");
		assertEquals("baz", exp.getValue(context));
	}

	public void testResolveSpringBean() {
		MockRequestContext context = new MockRequestContext();
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.getBeanFactory().registerSingleton("testBean", new TestBean());
		context.getRootFlow().setApplicationContext(ac);
		Expression exp = parser.parseExpression("testBean", new FluentParserContext().evaluate(RequestContext.class));
		assertSame(ac.getBean("testBean"), exp.getValue(context));
	}

	public void testResolveAction() {
		MockRequestContext context = new MockRequestContext();
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.getBeanFactory().registerSingleton("action", new TestAction());
		context.getRootFlow().setApplicationContext(ac);
		Expression exp = parser.parseExpression("action", new FluentParserContext().evaluate(RequestContext.class));
		assertSame(ac.getBean("action"), exp.getValue(context));
	}

	public void testResolveMultiAction() {
		MockRequestContext context = new MockRequestContext();
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.getBeanFactory().registerSingleton("multiAction", new FormAction());
		context.getRootFlow().setApplicationContext(ac);
		Expression exp = parser.parseExpression("multiAction.setupForm", new FluentParserContext()
				.evaluate(RequestContext.class));
		AnnotatedAction action = (AnnotatedAction) exp.getValue(context);
		assertSame(ac.getBean("multiAction"), action.getTargetAction());
		assertEquals("setupForm", action.getMethod());
	}

	public void testResolveEventAttributes() {
		MockRequestContext context = new MockRequestContext();
		LocalAttributeMap attributes = new LocalAttributeMap();
		attributes.put("foo", "bar");
		context.setCurrentEvent(new Event(this, "event", attributes));
		Expression exp = parser.parseExpression("currentEvent.attributes.foo", new FluentParserContext()
				.evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}

	public void testResolveNull() {
		MockRequestContext context = new MockRequestContext();
		Expression exp = parser.parseExpression("null", new FluentParserContext().evaluate(RequestContext.class));
		assertEquals(null, exp.getValue(context));
	}

	public void testResolveMessage() {
		MockRequestContext context = new MockRequestContext();
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.getStaticMessageSource().addMessage("foo", Locale.FRANCE, "bar");
		ac.refresh();
		context.getRootFlow().setApplicationContext(ac);
		context.getMockExternalContext().setLocale(Locale.FRANCE);
		Expression exp = parser.parseExpression("resourceBundle.foo", new FluentParserContext()
				.evaluate(RequestContext.class));
		assertEquals("bar", exp.getValue(context));
	}
}
