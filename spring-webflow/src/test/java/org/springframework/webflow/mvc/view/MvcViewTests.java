package org.springframework.webflow.mvc.view;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.format.factories.DateFormatterFactory;
import org.springframework.binding.format.factories.NumberFormatterFactory;
import org.springframework.binding.format.impl.FormatterRegistryImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

public class MvcViewTests extends TestCase {

	private boolean renderCalled;

	private Map model;

	private FormatterRegistryImpl formatterRegistry = new FormatterRegistryImpl();

	public void setUp() {
		formatterRegistry.registerFormatter(new NumberFormatterFactory());
		formatterRegistry.registerFormatter(new DateFormatterFactory());
	}

	public void testRender() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("foo", "bar");
		context.getFlowScope().put("bar", "baz");
		context.getFlowScope().put("bindBean", new BindBean());
		context.getConversationScope().put("baz", "boop");
		context.getFlashScope().put("boop", "bing");
		context.getMockExternalContext().setCurrentUser("Keith");
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.render();
		assertTrue(renderCalled);
		assertEquals("bar", model.get("foo"));
		assertEquals("baz", model.get("bar"));
		assertEquals("boop", model.get("baz"));
		assertEquals("bing", model.get("boop"));
		assertEquals("c1v1", model.get("flowExecutionKey"));
		assertEquals("Keith", ((Principal) model.get("currentUser")).getName());
		assertEquals(context, model.get("flowRequestContext"));
		assertEquals("/mockFlow?execution=c1v1", model.get("flowExecutionUrl"));
		assertNull(model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean"));
	}

	public void testRenderWithBindingModel() throws Exception {
		MockRequestContext context = new MockRequestContext();
		Object bindBean = new BindBean();
		StaticExpression modelObject = new StaticExpression(bindBean);
		modelObject.setExpressionString("bindBean");
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getFlowScope().put("bindBean", bindBean);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.render();
		assertEquals(context.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals(null, bm.getFieldValue("stringProperty"));
		assertEquals("3", bm.getFieldValue("integerProperty"));
		assertEquals("1/1/08", bm.getFieldValue("dateProperty"));
	}

	public void testResumeNoEvent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MvcView(mvcView, context);
		view.resume();
		assertFalse(view.eventSignaled());
		assertNull(view.getEvent());
	}

	public void testResumeEventNoModelBinding() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.resume();
		assertTrue(view.eventSignaled());
		assertEquals("submit", view.getEvent().getId());
	}

	public void testResumeEventModelBinding() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "1/1/07");
		BindBean bindBean = new BindBean();
		StaticExpression modelObject = new StaticExpression(bindBean);
		modelObject.setExpressionString("bindBean");
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getFlowScope().put("bindBean", bindBean);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.resume();
		assertTrue(view.eventSignaled());
		assertEquals("submit", view.getEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(new Integer(5), bindBean.getIntegerProperty());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2007);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
	}

	private class MockView implements View {

		public String getContentType() {
			return "text/html";
		}

		public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			renderCalled = true;
			MvcViewTests.this.model = model;
		}

	}

	public static class BindBean {
		private String stringProperty;
		private Integer integerProperty = new Integer(3);
		private Date dateProperty;

		public BindBean() {
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(Calendar.YEAR, 2008);
			dateProperty = cal.getTime();
		}

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

		public Date getDateProperty() {
			return dateProperty;
		}

		public void setDateProperty(Date dateProperty) {
			this.dateProperty = dateProperty;
		}

	}

}
