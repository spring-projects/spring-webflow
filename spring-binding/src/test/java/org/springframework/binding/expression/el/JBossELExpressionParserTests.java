package org.springframework.binding.expression.el;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.TestBean;
import org.springframework.binding.expression.support.TestMethods;

/**
 * Tests to exercise the extended method invoking expression extensions of JBoss-el.
 * @author Jeremy Grelle
 */
public class JBossELExpressionParserTests extends TestCase {

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

    public void testEmptyMethod() {

	String expStr1 = "#{foo.bar()}";
	Expression result1 = parser.parseExpression(expStr1);
	assertNotNull(result1);
	assertEquals(expStr1, result1.toString());

	String expStr2 = "foo.bar()";
	Expression result2 = parser.parseExpression(expStr2);
	assertNotNull(result2);
	assertEquals(expStr1, result2.toString());
    }

    public void testMethodWithParams() {

	String expStr1 = "#{foo.bar(moe.curly, groucho.harpo)}";
	Expression result1 = parser.parseExpression(expStr1);
	assertNotNull(result1);
	assertEquals(expStr1, result1.toString());

	String expStr2 = "foo.bar(moe.curly, groucho.harpo)";
	Expression result2 = parser.parseExpression(expStr2);
	assertNotNull(result2);
	assertEquals(expStr1, result2.toString());
    }

}
