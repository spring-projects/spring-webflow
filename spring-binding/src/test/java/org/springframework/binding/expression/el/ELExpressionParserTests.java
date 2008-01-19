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
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.support.ParserContextImpl;

public class ELExpressionParserTests extends TestCase {

	private ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());

	public void setUp() {
		parser.putContextFactory(TestBean.class, new TestELContextFactory());
	}

	public void testParseSimpleEvalExpressionNoParserContext() {
		String expressionString = "#{3 + 4}";
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

	public void testParseSimpleEvalExpressionNoEvalContextWithTypeCoersion() {
		String expressionString = "#{3 + 4}";
		Expression exp = parser.parseExpression(expressionString, new ParserContextImpl().expect(Integer.class));
		assertEquals(new Integer(7), exp.getValue(null));
	}

	public void testAssignment() {
		String expressionString = "#{value = 12345}";
		Expression exp = parser.parseExpression(expressionString, new ParserContextImpl().expect(Integer.class));
		assertEquals(new Integer(7), exp.getValue(null));
	}

	public void testParseBeanEvalExpressionNoParserContext() {
		String expressionString = "#{value}";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("foo", exp.getValue(new TestBean()));
	}

	public void testParseEvalExpressionWithContextTypeCoersion() {
		String expressionString = "#{maximum}";
		Expression exp = parser.parseExpression(expressionString, new ParserContextImpl().expect(Long.class));
		assertEquals(new Long(2), exp.getValue(new TestBean()));
	}

	public void testParseEvalExpressionWithContextCustomTestBeanResolver() {
		String expressionString = "#{specialProperty}";
		Expression exp = parser.parseExpression(expressionString, new ParserContextImpl().eval(TestBean.class));
		assertEquals("Custom resolver resolved this special property!", exp.getValue(new TestBean()));
	}

	public void testParseLiteralExpression() {
		String expressionString = "value";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("value", exp.getValue(null));
	}

	public void testParseExpressionWithVariables() {
		String expressionString = "#{value}#{max}";
		Expression exp = parser.parseExpression(expressionString, new ParserContextImpl()
				.variable(new ExpressionVariable("max", "#{maximum}")));
		TestBean target = new TestBean();
		assertEquals("foo2", exp.getValue(target));
	}

	public void testParseImmediateEvalExpression() {
		String expressionString = "${3 + 4}";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals(new Long(7), exp.getValue(null));
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
		public ELContext getELContext(final Object target, final VariableMapper variableMapper) {
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
								return "Custom resolver resolved this special property!";
							} else {
								throw new IllegalStateException();
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
					return variableMapper;
				}
			};
		}
	}

}
