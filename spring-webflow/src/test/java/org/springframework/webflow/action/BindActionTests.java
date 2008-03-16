package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.test.MockRequestContext;

public class BindActionTests extends TestCase {
	private ExpressionParser expressionParser = DefaultExpressionParserFactory.getExpressionParser();
	private ConversionService conversionService = new DefaultConversionService();

	private BindAction action;

	public void testSuccessfulBind() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("bindTarget", new BindBean());

		Expression target = expressionParser.parseExpression("bindTarget", new ParserContextImpl()
				.eval(RequestContext.class));
		action = new BindAction(target, expressionParser, conversionService);

		LocalAttributeMap eventData = new LocalAttributeMap();
		eventData.put("stringProperty", "foo");
		eventData.put("integerProperty", "3");
		Event event = new Event(this, "submit", eventData);
		context.setLastEvent(event);

		Event result = action.execute(context);
		assertEquals("success", result.getId());

		BindBean bean = (BindBean) context.getFlowScope().get("bindTarget");
		assertEquals("foo", bean.getStringProperty());
		assertEquals(new Integer(3), bean.getIntegerProperty());
	}

	public void testBindNonexistantProperties() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("bindTarget", new BindBean());

		Expression target = expressionParser.parseExpression("bindTarget", new ParserContextImpl()
				.eval(RequestContext.class));
		action = new BindAction(target, expressionParser, conversionService);

		LocalAttributeMap eventData = new LocalAttributeMap();
		eventData.put("stringProperty", "foo");
		eventData.put("bogusProperty", "bar");
		eventData.put("integerProperty", "3");
		Event event = new Event(this, "submit", eventData);
		context.setLastEvent(event);

		Event result = action.execute(context);
		assertEquals("success", result.getId());

		BindBean bean = (BindBean) context.getFlowScope().get("bindTarget");
		assertEquals("foo", bean.getStringProperty());
		assertEquals(new Integer(3), bean.getIntegerProperty());
		assertEquals(0, context.getMessageContext().getMessages().length);
	}

	public void testBindWithTypeConversionErrors() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("bindTarget", new BindBean());

		Expression target = expressionParser.parseExpression("bindTarget", new ParserContextImpl()
				.eval(RequestContext.class));
		action = new BindAction(target, expressionParser, conversionService);

		LocalAttributeMap eventData = new LocalAttributeMap();
		eventData.put("stringProperty", "foo");
		eventData.put("integerProperty", "malformed");
		Event event = new Event(this, "submit", eventData);
		context.setLastEvent(event);

		Event result = action.execute(context);
		assertEquals("error", result.getId());

		BindBean bean = (BindBean) context.getFlowScope().get("bindTarget");
		assertEquals("foo", bean.getStringProperty());
		assertEquals(new Integer(3), bean.getIntegerProperty());
	}

	public void testBindWithEmptyAttributes() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("bindTarget", new BindBean());

		Expression target = expressionParser.parseExpression("bindTarget", new ParserContextImpl()
				.eval(RequestContext.class));
		action = new BindAction(target, expressionParser, conversionService);

		LocalAttributeMap eventData = new LocalAttributeMap();
		eventData.put("stringProperty", "");
		eventData.put("integerProperty", null);
		Event event = new Event(this, "submit", eventData);
		context.setLastEvent(event);

		Event result = action.execute(context);
		System.out.println(context.getMessageContext());
		assertEquals("success", result.getId());

		BindBean bean = (BindBean) context.getFlowScope().get("bindTarget");
		assertEquals("", bean.getStringProperty());
		assertEquals(null, bean.getIntegerProperty());
		assertEquals(0, context.getMessageContext().getMessages().length);
	}

	public static class BindBean {
		private String stringProperty;
		private Integer integerProperty = new Integer(3);

		public String getStringProperty() {
			return stringProperty;
		}

		public void setStringProperty(String stringProperty) {
			this.stringProperty = stringProperty;
		}

		public Integer getIntegerProperty() {
			return integerProperty;
		}

		public void setIntegerProperty(Integer integerProperty) {
			this.integerProperty = integerProperty;
		}
	}
}
