package org.springframework.webflow.mvc.view;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.webflow.action.ViewFactoryActionAdapter;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

public class MvcViewTests extends TestCase {

	private boolean renderCalled;

	private Map model;

	public void testRender() throws Exception {
		MockRequestControlContext context = new MockRequestControlContext();
		context.setCurrentState(new ViewState(context.getRootFlow(), "test", new StubViewFactory()));
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
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

	public void testRenderNoKey() throws Exception {
		MockRequestControlContext context = new MockRequestControlContext();
		EndState endState = new EndState(context.getRootFlow(), "end");
		endState.setFinalResponseAction(new ViewFactoryActionAdapter(new StubViewFactory()));
		context.setCurrentState(endState);
		context.getRequestScope().put("foo", "bar");
		context.getFlowScope().put("bar", "baz");
		context.getFlowScope().put("bindBean", new BindBean());
		context.getConversationScope().put("baz", "boop");
		context.getFlashScope().put("boop", "bing");
		context.getMockExternalContext().setCurrentUser("Keith");
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.render();
		assertTrue(renderCalled);
		assertEquals("bar", model.get("foo"));
		assertEquals("baz", model.get("bar"));
		assertEquals("boop", model.get("baz"));
		assertEquals("bing", model.get("boop"));
		assertFalse(model.containsKey("flowExecutionKey"));
		assertFalse(model.containsKey("flowExecutionUrl"));
		assertEquals("Keith", ((Principal) model.get("currentUser")).getName());
		assertEquals(context, model.get("flowRequestContext"));
		assertNull(model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean"));
	}

	public void testRenderWithBindingModel() throws Exception {
		MockRequestControlContext context = new MockRequestControlContext();
		context.setCurrentState(new ViewState(context.getRootFlow(), "test", new StubViewFactory()));
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setConversionService(new DefaultConversionService());
		view.render();
		assertEquals(context.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals(null, bm.getFieldValue("stringProperty"));
		assertEquals("3", bm.getFieldValue("integerProperty"));
		assertEquals("2008-01-01", bm.getFieldValue("dateProperty"));
	}

	public void testResumeNoEvent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
	}

	public void testResumeEventModelBinding() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "2007-01-01");
		context.putRequestParameter("beanProperty.name", "foo");
		context.putRequestParameter("multipartFile", new MockMultipartFile("foo", new byte[0]));
		context.putRequestParameter("stringArrayProperty", new String[] { "foo", "bar", "baz" });
		context.putRequestParameter("integerArrayProperty", new String[] { "1", "2", "3" });
		context.putRequestParameter("primitiveArrayProperty", new String[] { "1", "2", "3" });
		context.putRequestParameter("listProperty", new String[] { "1", "2", "3" });
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(new Integer(5), bindBean.getIntegerProperty());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2007);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
		assertEquals("foo", bindBean.getBeanProperty().getName());
		assertEquals("foo", bindBean.getMultipartFile().getName());
		assertEquals(3, bindBean.getStringArrayProperty().length);
		assertEquals("foo", bindBean.getStringArrayProperty()[0]);
		assertEquals("bar", bindBean.getStringArrayProperty()[1]);
		assertEquals("baz", bindBean.getStringArrayProperty()[2]);
		assertEquals(3, bindBean.getIntegerArrayProperty().length);
		assertEquals(new Integer(1), bindBean.getIntegerArrayProperty()[0]);
		assertEquals(new Integer(2), bindBean.getIntegerArrayProperty()[1]);
		assertEquals(new Integer(3), bindBean.getIntegerArrayProperty()[2]);
		assertEquals(3, bindBean.getPrimitiveArrayProperty().length);
		assertEquals(1, bindBean.getPrimitiveArrayProperty()[0]);
		assertEquals(2, bindBean.getPrimitiveArrayProperty()[1]);
		assertEquals(3, bindBean.getPrimitiveArrayProperty()[2]);
		assertEquals(3, bindBean.getListProperty().size());
		assertEquals("1", bindBean.getListProperty().get(0));
		assertEquals("2", bindBean.getListProperty().get(1));
		assertEquals("3", bindBean.getListProperty().get(2));
	}

	public void testResumeEventModelBindingAllowedFields() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "2007-01-01");
		context.putRequestParameter("beanProperty.name", "foo");
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		BinderConfiguration binderConfiguration = new BinderConfiguration();
		binderConfiguration.addBinding(new Binding("stringProperty", null, true));
		view.setBinderConfiguration(binderConfiguration);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(new Integer(3), bindBean.getIntegerProperty());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2008);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
		assertEquals(null, bindBean.getBeanProperty().getName());
	}

	public void testResumeEventModelBindingFieldMarker() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("_booleanProperty", "whatever");
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		HashSet allowedBindFields = new HashSet();
		allowedBindFields.add("booleanProperty");
		view.processUserEvent();
		assertEquals(false, bindBean.getBooleanProperty());
	}

	public void testResumeEventModelBindingFieldMarkerFieldPresent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("booleanProperty", "true");
		context.putRequestParameter("_booleanProperty", "whatever");
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
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.processUserEvent();
		assertEquals(true, bindBean.getBooleanProperty());
	}

	private class MockMvcView extends AbstractMvcView {

		public MockMvcView(View view, RequestContext context) {
			super(view, context);
		}

		protected void doRender(Map model) throws Exception {
			getView().render(model, (HttpServletRequest) getRequestContext().getExternalContext().getNativeRequest(),
					(HttpServletResponse) getRequestContext().getExternalContext().getNativeResponse());
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
		private boolean booleanProperty = true;
		private NestedBean beanProperty;
		private MultipartFile multipartFile;

		private String[] stringArrayProperty;
		private Integer[] integerArrayProperty;
		private int[] primitiveArrayProperty;
		private List listProperty;
		private Map mapProperty;

		public BindBean() {
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(Calendar.YEAR, 2008);
			dateProperty = cal.getTime();
			beanProperty = new NestedBean();
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

		public boolean getBooleanProperty() {
			return booleanProperty;
		}

		public void setBooleanProperty(boolean booleanProperty) {
			this.booleanProperty = booleanProperty;
		}

		public Date getDateProperty() {
			return dateProperty;
		}

		public void setDateProperty(Date dateProperty) {
			this.dateProperty = dateProperty;
		}

		public NestedBean getBeanProperty() {
			return beanProperty;
		}

		public MultipartFile getMultipartFile() {
			return multipartFile;
		}

		public void setMultipartFile(MultipartFile multipartFile) {
			this.multipartFile = multipartFile;
		}

		public String[] getStringArrayProperty() {
			return stringArrayProperty;
		}

		public void setStringArrayProperty(String[] stringArrayProperty) {
			this.stringArrayProperty = stringArrayProperty;
		}

		public Integer[] getIntegerArrayProperty() {
			return integerArrayProperty;
		}

		public void setIntegerArrayProperty(Integer[] integerArrayProperty) {
			this.integerArrayProperty = integerArrayProperty;
		}

		public int[] getPrimitiveArrayProperty() {
			return primitiveArrayProperty;
		}

		public void setPrimitiveArrayProperty(int[] primitiveArrayProperty) {
			this.primitiveArrayProperty = primitiveArrayProperty;
		}

		public List getListProperty() {
			return listProperty;
		}

		public void setListProperty(List listProperty) {
			this.listProperty = listProperty;
		}

		public Map getMapProperty() {
			return mapProperty;
		}

		public void setMapProperty(Map mapProperty) {
			this.mapProperty = mapProperty;
		}

		public void setBeanProperty(NestedBean beanProperty) {
			this.beanProperty = beanProperty;
		}

	}

	public static class NestedBean {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
