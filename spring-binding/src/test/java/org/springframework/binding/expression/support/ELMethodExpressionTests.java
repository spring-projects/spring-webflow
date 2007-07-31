package org.springframework.binding.expression.support;

import java.util.HashMap;
import java.util.Map;
import org.easymock.MockControl;
import org.springframework.binding.expression.el.JBossELExpressionParser;

import junit.framework.TestCase;

/**
 * Tests to exercise the extended method invoking expression extensions of JBoss-el.
 * 
 * @author Jeremy Grelle
 *
 */
public class ELMethodExpressionTests extends TestCase {

	JBossELExpressionParser parser = new JBossELExpressionParser();

	Map context;

	Map container;

	TestMethods target;
	MockControl targetMockControl;

	protected void setUp() throws Exception {
		context = new HashMap();
		container = new HashMap();
		targetMockControl = MockControl.createControl(TestMethods.class);
		target = (TestMethods) targetMockControl.getMock();
		context.put("container", container);
		container.put("myObject", target);
	}

	public void testWithIntParam() {
		String expression = "#{container.myObject.doSomethingWithInt(container.param1)}";
		int param = 5;
		container.put("param1", new Integer(param));
		target.doSomethingWithInt(param);
		targetMockControl.replay();

		parser.parseExpression(expression).evaluate(context, null);
		targetMockControl.verify();

	}

	public void testReturnWithIntParam() {
		String expected = "sucess";
		String expression = "#{container.myObject.returnStringFromInt(container.param1)}";
		int param = 5;
		container.put("param1", new Integer(param));
		targetMockControl.expectAndReturn(target.returnStringFromInt(param), expected);
		targetMockControl.replay();

		String result = (String) parser.parseExpression(expression).evaluate(context, null);
		targetMockControl.verify();
		assertEquals(expected, result);
	}

	public void testReturnWithIntAndObject() {
		String expected = "success";
		String expression = "#{container.myObject.returnStringFromIntAndObject(container.param1, container.param2)}";
		int param1 = 5;
		container.put("param1", new Integer(param1));
		TestBean param2 = new TestBean();
		container.put("param2", param2);
		targetMockControl.expectAndReturn(target.returnStringFromIntAndObject(param1, param2), expected);
		targetMockControl.replay();

		String result = (String) parser.parseExpression(expression).evaluate(context, null);
		targetMockControl.verify();
		assertEquals(expected, result);
	}

}
