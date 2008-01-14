package org.springframework.binding.expression.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserException;

public class ELExpressionParserTests extends TestCase {

	private ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());

	public void setUp() {
		parser.putContextFactory(TestBean.class, new TestELContextFactory());
	}

	private static class TestELContextFactory implements ELContextFactory {
		public ELContext getELContext(final Object target, final VariableMapper variableMapper) {
			return new ELContext() {
				public ELResolver getELResolver() {
					return new DefaultELResolver(target, null);
				}

				public FunctionMapper getFunctionMapper() {
					return null;
				}

				public VariableMapper getVariableMapper() {
					return variableMapper;
				}
			};
		}
	}

	public void testParseEvalExpressionExpectedResultTypeNull() {
		String expressionString = "#{value}";
		Class expressionTargetType = null;
		Class expectedEvaluationResultType = null;
		ExpressionVariable[] expressionVariables = null;
		try {
			parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
					expressionVariables);
			fail("Should have failed");
		} catch (ParserException e) {

		}
	}

	public void testParseEvalExpression() {
		String expressionString = "#{value}";
		Class expressionTargetType = TestBean.class;
		Class expectedEvaluationResultType = String.class;
		ExpressionVariable[] expressionVariables = null;
		Expression exp = parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
				expressionVariables);
		TestBean target = new TestBean();
		assertEquals("foo", exp.getValue(target));
	}

	public void testParseEvalExpressionNoTargetType() {
		String expressionString = "#{value}";
		Class expressionTargetType = null;
		Class expectedEvaluationResultType = Object.class;
		ExpressionVariable[] expressionVariables = null;
		try {
			parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
					expressionVariables);
			fail("Should have failed");
		} catch (ParserException e) {

		}
	}

	public void testParseEvalExpressionNotRegisteredTargetType() {
		String expressionString = "#{value}";
		Class expressionTargetType = Map.class;
		Class expectedEvaluationResultType = Object.class;
		ExpressionVariable[] expressionVariables = null;
		try {
			parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
					expressionVariables);
			fail("Should have failed");
		} catch (ParserException e) {
		}
	}

	public void testParseLiteralExpression() {
		String expressionString = "value";
		Class expressionTargetType = TestBean.class;
		Class expectedEvaluationResultType = String.class;
		ExpressionVariable[] expressionVariables = null;
		Expression exp = parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
				expressionVariables);
		TestBean target = new TestBean();
		assertEquals("value", exp.getValue(target));
	}

	public void testParseExpressionWithVariables() {
		String expressionString = "#{value}#{max}";
		Class expressionTargetType = TestBean.class;
		Class expectedEvaluationResultType = String.class;
		ExpressionVariable[] expressionVariables = new ExpressionVariable[] { new ExpressionVariable("max",
				"#{maximum}") };
		Expression exp = parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
				expressionVariables);
		TestBean target = new TestBean();
		assertEquals("foo2", exp.getValue(target));
	}

	public void testParseExpressionWithVariables2() {
		String expressionString = "#{value}#{bean.encode(value)}";
		Class expressionTargetType = TestBean.class;
		Class expectedEvaluationResultType = String.class;
		ExpressionVariable[] expressionVariables = null;
		Expression exp = parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
				expressionVariables);
		TestBean target = new TestBean(new TestBean());
		assertEquals("foo!foo", exp.getValue(target));
	}

	public void testParseExpressionCoerceToInteger() {
		String expressionString = "#{maximum}#{max}";
		Class expressionTargetType = TestBean.class;
		Class expectedEvaluationResultType = Integer.class;
		ExpressionVariable[] expressionVariables = new ExpressionVariable[] { new ExpressionVariable("max",
				"#{maximum}") };
		Expression exp = parser.parseExpression(expressionString, expressionTargetType, expectedEvaluationResultType,
				expressionVariables);
		TestBean target = new TestBean();
		assertEquals(new Integer(22), exp.getValue(target));
	}

	public static class TestBean {
		private String value = "foo";

		private int maximum = 2;

		private TestBean bean;

		private List list = new ArrayList();

		public TestBean() {
			initList();
		}

		public TestBean(TestBean bean) {
			this.bean = bean;
			initList();
		}

		private void initList() {
			list.add("1");
			list.add("2");
			list.add("3");
		}

		public TestBean getBean() {
			return bean;
		}

		public String getValue() {
			return value;
		}

		public String encode(String data) {
			return "!" + data;
		}

		public void setValue(String value) {

		}

		public int getMaximum() {
			return maximum;
		}

		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}

	}
}
