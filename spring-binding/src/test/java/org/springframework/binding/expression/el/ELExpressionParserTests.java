package org.springframework.binding.expression.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.FluentParserContext;

public class ELExpressionParserTests extends TestCase {

	private ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());

	public void setUp() {
		parser.putContextFactory(TestBean.class, new TestELContextFactory());
	}

	public void testParseSimpleEvalExpressionNoParserContext() {
		String expressionString = "3 + 4";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals(new Long(7), exp.getValue(null));
	}

	public void testParseNullExpressionString() {
		String expressionString = null;
		try {
			parser.parseExpression(expressionString, null);
			fail("should have thrown iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testParseNull() {
		Expression exp = parser.parseExpression("null", null);
		assertEquals(null, exp.getValue(null));
	}

	public void testParseEmptyExpressionString() {
		String expressionString = "";
		try {
			parser.parseExpression(expressionString, null);
			fail("Should have failed");
		} catch (ParserException e) {

		}
	}

	public void testParseSimpleEvalExpressionNoEvalContextWithTypeCoersion() {
		String expressionString = "3 + 4";
		Expression exp = parser
				.parseExpression(expressionString, new FluentParserContext().expectResult(Integer.class));
		assertEquals(new Integer(7), exp.getValue(null));
	}

	public void testParseBeanEvalExpressionNoParserContext() {
		String expressionString = "value";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("foo", exp.getValue(new TestBean()));
	}

	public void testParseEvalExpressionWithContextTypeCoersion() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().expectResult(Long.class));
		assertEquals(new Long(2), exp.getValue(new TestBean()));
	}

	public void testParseEvalExpressionWithContextCustomELVariableResolver() {
		String expressionString = "specialProperty";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().evaluate(TestBean.class));
		assertEquals("Custom resolver resolved this special property!", exp.getValue(new TestBean()));
	}

	public void testParseBeanEvalExpressionInvalidELVariable() {
		try {
			String expressionString = "bogus";
			Expression exp = parser.parseExpression(expressionString, new FluentParserContext()
					.evaluate(TestBean.class));
			exp.getValue(new TestBean());
			fail("Should have failed");
		} catch (EvaluationException e) {

		}
	}

	public void testParseLiteralExpression() {
		String expressionString = "'value'";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("value", exp.getValue(null));
	}

	public void testParseTemplateExpression() {
		String expressionString = "text text text #{value} text text text#{value}";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().template());
		TestBean target = new TestBean();
		assertEquals("text text text foo text text textfoo", exp.getValue(target));
	}

	public void testParseTemplateExpressionWithVariables() {
		String expressionString = "#{value}#{max}";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().template().variable(
				new ExpressionVariable("max", "maximum")));
		TestBean target = new TestBean();
		assertEquals("foo2", exp.getValue(target));
	}

	public void testVariablesWithCoersion() {
		Expression exp = parser.parseExpression("max", new FluentParserContext().variable(new ExpressionVariable("max",
				"maximum", new FluentParserContext().expectResult(Long.class))));
		TestBean target = new TestBean();
		assertEquals(new Long(2), exp.getValue(target));
	}

	public void testTemplateNestedVariables() {
		String expressionString = "#{value}#{max}";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().template().variable(
				new ExpressionVariable("max", "#{maximum}#{var}", new FluentParserContext().template().variable(
						new ExpressionVariable("var", "'bar'")))));
		TestBean target = new TestBean();
		assertEquals("foo2bar", exp.getValue(target));
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

	private static class TestELContextFactory implements ELContextFactory {
		public ELContext getELContext(final Object target) {
			return new ELContext() {
				public ELResolver getELResolver() {
					return new ELResolver() {
						public Class getCommonPropertyType(ELContext arg0, Object arg1) {
							return Object.class;
						}

						public Iterator getFeatureDescriptors(ELContext arg0, Object arg1) {
							return null;
						}

						public Class getType(ELContext arg0, Object arg1, Object arg2) {
							return String.class;
						}

						public Object getValue(ELContext arg0, Object arg1, Object arg2) {
							if (arg1 == null && arg2.equals("specialProperty")) {
								arg0.setPropertyResolved(true);
								return "Custom resolver resolved this special property!";
							} else {
								return null;
							}
						}

						public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2) {
							return true;
						}

						public void setValue(ELContext arg0, Object arg1, Object arg2, Object arg3) {
							throw new UnsupportedOperationException("Not supported");
						}
					};
				}

				public FunctionMapper getFunctionMapper() {
					return null;
				}

				public VariableMapper getVariableMapper() {
					return null;
				}
			};
		}
	}
}
