package org.springframework.webflow.mvc.view;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.format.formatters.DateFormatter;
import org.springframework.binding.format.registry.DefaultFormatterRegistry;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

public class MvcViewTests extends TestCase {

	private boolean renderCalled;

	private Map model;

	private DefaultFormatterRegistry formatterRegistry = new DefaultFormatterRegistry();

	protected void setUp() {
		DateFormatter dateFormatter = new DateFormatter();
		dateFormatter.setLocale(Locale.ENGLISH);
		formatterRegistry.registerFormatter(Date.class, dateFormatter);
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
		MvcView view = new MockMvcView(mvcView, context);
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
		MvcView view = new MockMvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.render();
		assertEquals(context.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals(null, bm.getFieldValue("stringProperty"));
		assertEquals("3", bm.getFieldValue("integerProperty"));
		assertEquals("Jan 1, 2008", bm.getFieldValue("dateProperty"));
	}

	public void testResumeNoEvent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MockMvcView(mvcView, context);
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertNull(view.getFlowEvent());
	}

	public void testResumeEventNoModelBinding() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		MvcView view = new MockMvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
	}

	public void testResumeEventModelBinding() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "Jan 1, 2007");
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
		MvcView view = new MockMvcView(mvcView, context);
		view.setFormatterRegistry(formatterRegistry);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(new Integer(5), bindBean.getIntegerProperty());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2007);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
	}

	private class MockMvcView extends MvcView {

		public MockMvcView(View view, RequestContext context) {
			super(view, context);
		}

		protected void doRender(org.springframework.web.servlet.View view, Map model, ExternalContext context)
				throws Exception {
			view.render(model, (HttpServletRequest) context.getNativeRequest(), (HttpServletResponse) context
					.getNativeResponse());
		}

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
