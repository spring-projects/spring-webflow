package org.springframework.webflow.mvc.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.converters.StringToDate;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.convert.service.GenericConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.webflow.action.ViewFactoryActionAdapter;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.test.MockRequestControlContext;
import org.springframework.webflow.validation.WebFlowMessageCodesResolver;

public class MvcViewTests {

	private boolean renderCalled;

	private Map<String, ?> model;

	@Test
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
		view.setExpressionParser(createExpressionParser());
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

	@Test
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
		view.setExpressionParser(createExpressionParser());
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

	@Test
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
		view.setExpressionParser(createExpressionParser());
		view.setConversionService(new DefaultConversionService());
		view.render();
		assertEquals(context.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals(null, bm.getFieldValue("stringProperty"));
		assertEquals("3", bm.getFieldValue("integerProperty"));
		assertEquals("2008-01-01", bm.getFieldValue("dateProperty"));
	}

	@Test
	public void testResumeNoEvent() {
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		assertFalse(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertNull(view.getFlowEvent());
	}

	@Test
	public void testResumeEventNoModelBinding() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
	}

	@Test
	public void testResumeEventModelBinding() {
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
		view.setExpressionParser(createExpressionParser());
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertFalse(context.getFlashScope().contains(ViewActionStateHolder.KEY));
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(Integer.valueOf(5), bindBean.getIntegerProperty());
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
		assertEquals(Integer.valueOf(1), bindBean.getIntegerArrayProperty()[0]);
		assertEquals(Integer.valueOf(2), bindBean.getIntegerArrayProperty()[1]);
		assertEquals(Integer.valueOf(3), bindBean.getIntegerArrayProperty()[2]);
		assertEquals(3, bindBean.getPrimitiveArrayProperty().length);
		assertEquals(1, bindBean.getPrimitiveArrayProperty()[0]);
		assertEquals(2, bindBean.getPrimitiveArrayProperty()[1]);
		assertEquals(3, bindBean.getPrimitiveArrayProperty()[2]);
		assertEquals(3, bindBean.getListProperty().size());
		assertEquals("1", bindBean.getListProperty().get(0));
		assertEquals("2", bindBean.getListProperty().get(1));
		assertEquals("3", bindBean.getListProperty().get(2));
		assertFalse(bindBean.validationMethodInvoked);
	}

	@Test
	public void testResumeEventBindingErrors() throws IOException {
		MockRequestControlContext context = new MockRequestControlContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("integerProperty", "bogus 1");
		context.putRequestParameter("dateProperty", "bogus 2");
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
		view.setExpressionParser(createExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		context.setAlwaysRedirectOnPause(true);
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		view.render();
		assertEquals(context.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals("bogus 1", bm.getFieldValue("integerProperty"));
		assertEquals("bogus 2", bm.getFieldValue("dateProperty"));
	}

	@Test
	public void testResumeEventNoModelInScope() {
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
		Expression modelObject = new Expression() {
			public String getExpressionString() {
				return "foo";
			}

			public Object getValue(Object context) throws EvaluationException {
				throw new EvaluationException(Object.class, "foo", "Model expression failed to evaluate");
			}

			public Class<?> getValueType(Object context) throws EvaluationException {
				return Object.class;
			}

			public void setValue(Object context, Object value) throws EvaluationException {
				throw new IllegalStateException("Should not be called");
			}
		};
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setExpressionParser(createExpressionParser());
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertFalse(context.getFlashScope().contains(ViewActionStateHolder.KEY));
		assertEquals("submit", view.getFlowEvent().getId());
	}

	@Test
	public void testResumeEventBindingErrorsRedirectAfterPost() throws Exception {
		MockRequestControlContext context = new MockRequestControlContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("integerProperty", "bogus 1");
		context.putRequestParameter("dateProperty", "bogus 2");
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
		view.setExpressionParser(createExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		context.setAlwaysRedirectOnPause(true);
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertFalse(view.hasFlowEvent());
		Object viewActionState = view.getUserEventState();
		assertNotNull(viewActionState);

		MockRequestControlContext context2 = new MockRequestControlContext();
		context2.getFlashScope().put(org.springframework.webflow.execution.View.USER_EVENT_STATE_ATTRIBUTE,
				viewActionState);
		BindBean bindBean2 = new BindBean();
		StaticExpression modelObject2 = new StaticExpression(bindBean2);
		modelObject2.setExpressionString("bindBean");
		context2.getCurrentState().getAttributes().put("model", modelObject);
		context2.getFlowScope().put("bindBean", bindBean);
		context2.getMockExternalContext().setNativeContext(new MockServletContext());
		context2.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context2.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context2.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		AbstractMvcView view2 = new MockMvcView(mvcView, context2);
		view2.setExpressionParser(createExpressionParser());
		view2.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view2.restoreState((ViewActionStateHolder) viewActionState);
		assertFalse(view2.userEventQueued());
		view2.render();
		assertEquals(context2.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals("bogus 1", bm.getFieldValue("integerProperty"));
		assertEquals("bogus 2", bm.getFieldValue("dateProperty"));
	}

	@Test
	public void testResumeEventBindingErrorsRedirectToReplicatedSessionAfterPost() throws Exception {
		MockRequestControlContext context = new MockRequestControlContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("integerProperty", "bogus 1");
		context.putRequestParameter("dateProperty", "bogus 2");
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
		view.setExpressionParser(createExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		context.setAlwaysRedirectOnPause(true);
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertFalse(view.hasFlowEvent());
		Object viewActionState = view.getUserEventState();
		assertNotNull(viewActionState);

		viewActionState = saveAndRestoreViewActionState(viewActionState);

		MockRequestControlContext context2 = new MockRequestControlContext();
		context2.getFlashScope().put(org.springframework.webflow.execution.View.USER_EVENT_STATE_ATTRIBUTE,
				viewActionState);
		BindBean bindBean2 = new BindBean();
		StaticExpression modelObject2 = new StaticExpression(bindBean2);
		modelObject2.setExpressionString("bindBean");
		context2.getCurrentState().getAttributes().put("model", modelObject);
		context2.getFlowScope().put("bindBean", bindBean);
		context2.getMockExternalContext().setNativeContext(new MockServletContext());
		context2.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context2.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context2.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		AbstractMvcView view2 = new MockMvcView(mvcView, context2);
		view2.setExpressionParser(createExpressionParser());
		view2.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view2.restoreState((ViewActionStateHolder) viewActionState);
		assertFalse(view2.userEventQueued());
		view2.render();
		assertEquals(context2.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals("3", bm.getFieldValue("integerProperty"));
		assertEquals("2008-01-01", bm.getFieldValue("dateProperty"));
	}

	private Object saveAndRestoreViewActionState(Object viewActionState) throws Exception {
		File tempFile = new File("serializable.tmp");

		FileOutputStream fos = new FileOutputStream(tempFile);
		ObjectOutputStream objOut = new ObjectOutputStream(fos);
		objOut.writeObject(viewActionState);
		objOut.close();

		FileInputStream fis = new FileInputStream(tempFile);
		ObjectInputStream objIn = new ObjectInputStream(fis);
		Object restoredState = objIn.readObject();
		objIn.close();

		tempFile.delete();

		assertNotSame(viewActionState, restoredState);

		return restoredState;
	}

	@Test
	public void testResumeEventModelBindingAllowedFields() {
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
		view.setExpressionParser(createExpressionParser());
		BinderConfiguration binderConfiguration = new BinderConfiguration();
		binderConfiguration.addBinding(new Binding("stringProperty", null, true));
		view.setBinderConfiguration(binderConfiguration);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals("foo", bindBean.getStringProperty());
		assertEquals(Integer.valueOf(3), bindBean.getIntegerProperty());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2008);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
		assertEquals(null, bindBean.getBeanProperty().getName());
	}

	@Test
	public void testResumeEventModelBindingCustomConverter() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("dateProperty", "01-01-2007");
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
		view.setExpressionParser(createExpressionParser());
		DefaultConversionService conversionService = new DefaultConversionService();
		StringToDate stringToDate = new StringToDate();
		stringToDate.setPattern("MM-dd-yyyy");
		conversionService.addConverter("customDateConverter", stringToDate);
		view.setConversionService(conversionService);
		BinderConfiguration binderConfiguration = new BinderConfiguration();
		binderConfiguration.addBinding(new Binding("dateProperty", "customDateConverter", true));
		view.setBinderConfiguration(binderConfiguration);
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2007);
		assertEquals(cal.getTime(), bindBean.getDateProperty());
	}

	@Test
	public void testResumeEventModelBindingFieldMarker() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("_booleanProperty", "whatever");
		BindBean bindBean = new BindBean();
		bindBean.setBooleanProperty(true);
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
		view.setExpressionParser(createExpressionParser());
		view.processUserEvent();
		assertEquals(false, bindBean.getBooleanProperty());
	}

	@Test
	public void testResumeEventModelBindingFieldMarkerFieldPresent() {
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
		view.setExpressionParser(createExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertEquals(true, bindBean.getBooleanProperty());
	}

	@Test
	public void testResumeEventModelBindAndValidate() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "2007-01-01");
		BindBean bindBean = new ValidatingBindBean();
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
		view.setExpressionParser(createExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertTrue(bindBean.validationMethodInvoked);
	}

	@Test
	public void testResumeEventModelBindAndValidateDefaultValidatorFallback() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "5");
		context.putRequestParameter("dateProperty", "2007-01-01");
		BindBean bindBean = new ValidatingBindBeanFallback();
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
		view.setExpressionParser(createExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertTrue(bindBean.validationMethodInvoked);
	}

	@Test
	public void testResumeEventModelValidateOnBindingErrors() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "bogus");
		context.putRequestParameter("dateProperty", "2007-01-01");
		BindBean bindBean = new ValidatingBindBean();
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
		view.setExpressionParser(createExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertTrue(bindBean.validationMethodInvoked);
	}

	@Test
	public void testResumeEventModelNoValidateOnBindingErrors() {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "bogus");
		context.putRequestParameter("dateProperty", "2007-01-01");
		BindBean bindBean = new ValidatingBindBean();
		StaticExpression modelObject = new StaticExpression(bindBean);
		modelObject.setExpressionString("bindBean");
		context.getMockFlowExecutionContext().putAttribute("validateOnBindingErrors", false);
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getFlowScope().put("bindBean", bindBean);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setExpressionParser(createExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertFalse(bindBean.validationMethodInvoked);
	}

	@Test
	public void testResumeEventStringValidationHint() {
		StubSmartValidator validator = new StubSmartValidator();
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		TestModel testModel = new TestModel();
		StaticExpression validationHintsExpression = new StaticExpression("State1,AllStates");
		context.getCurrentState().getAttributes().put("validationHints", validationHintsExpression);
		StaticExpression modelExpression = new StaticExpression(testModel);
		modelExpression.setExpressionString("testModel");
		context.getCurrentState().getAttributes().put("model", modelExpression);
		context.getFlowScope().put("testModel", testModel);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setValidator(validator);
		view.setExpressionParser(createExpressionParser());

		view.processUserEvent();

		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals(TestModel.State1.class, validator.hints[0]);
		assertEquals(TestModel.AllStates.class, validator.hints[1]);
		assertTrue(validator.invoked);
	}

	@Test
	public void testResumeEventObjectArrayValidationHint() {
		StubSmartValidator validator = new StubSmartValidator();
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		TestModel testModel = new TestModel();
		Object[] validationHints = new Object[] { TestModel.State1.class };
		StaticExpression validationHintsExpression = new StaticExpression(validationHints);
		context.getCurrentState().getAttributes().put("validationHints", validationHintsExpression);
		StaticExpression modelExpression = new StaticExpression(testModel);
		modelExpression.setExpressionString("testModel");
		context.getCurrentState().getAttributes().put("model", modelExpression);
		context.getFlowScope().put("testModel", testModel);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setValidator(validator);
		view.setExpressionParser(createExpressionParser());

		view.processUserEvent();

		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertEquals(validationHints, validator.hints);
		assertTrue(validator.invoked);
	}

	private SpringELExpressionParser createExpressionParser() {
		StringToDate c = new StringToDate();
		c.setPattern("yyyy-MM-dd");
		SpringELExpressionParser parser = new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		GenericConversionService cs = (GenericConversionService) parser.getConversionService();
		cs.addConverter(c);
		return parser;
	}

	private class MockMvcView extends AbstractMvcView {

		public MockMvcView(View view, RequestContext context) {
			super(view, context);
		}

		protected void doRender(Map<String, ?> model) throws Exception {
			getView().render(model, (HttpServletRequest) getRequestContext().getExternalContext().getNativeRequest(),
					(HttpServletResponse) getRequestContext().getExternalContext().getNativeResponse());
		}

	}

	private class MockView implements View {

		public String getContentType() {
			return "text/html";
		}

		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
			renderCalled = true;
			MvcViewTests.this.model = model;
		}

	}

	public static class BindBean {
		private String stringProperty;
		private Integer integerProperty = 3;
		private Date dateProperty;
		private boolean booleanProperty = true;
		private NestedBean beanProperty;
		private MultipartFile multipartFile;

		private String[] stringArrayProperty;
		private Integer[] integerArrayProperty;
		private int[] primitiveArrayProperty;
		private List<Object> listProperty;
		private Map<Object, Object> mapProperty;
		private boolean validationMethodInvoked;

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

		public List<Object> getListProperty() {
			return listProperty;
		}

		public void setListProperty(List<Object> listProperty) {
			this.listProperty = listProperty;
		}

		public Map<Object, Object> getMapProperty() {
			return mapProperty;
		}

		public void setMapProperty(Map<Object, Object> mapProperty) {
			this.mapProperty = mapProperty;
		}

		public void setBeanProperty(NestedBean beanProperty) {
			this.beanProperty = beanProperty;
		}

	}

	public static class ValidatingBindBean extends BindBean {

		public void validateMockState(ValidationContext context) {
			super.validationMethodInvoked = true;
		}
	}

	public static class ValidatingBindBeanFallback extends BindBean {

		public void validate(ValidationContext context) {
			assertEquals("submit", context.getUserEvent());
			assertNull(context.getUserPrincipal());
			assertEquals("foo", context.getUserValue("stringProperty"));
			super.validationMethodInvoked = true;
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

	public static class StubSmartValidator implements SmartValidator {
		private boolean invoked;
		private Object[] hints;

		public void validate(Object object, Errors errors) {
			invoked = true;
		}

		public void validate(Object object, Errors errors, Object... hints) {
			invoked = true;
			this.hints = hints;
		}

		public boolean supports(Class<?> clazz) {
			return true;
		}
	}

	private static class TestModel {

		public static class State1 {
		}

		public static class AllStates {
		}
	}

}
