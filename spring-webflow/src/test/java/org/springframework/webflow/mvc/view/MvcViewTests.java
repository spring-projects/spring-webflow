package org.springframework.webflow.mvc.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.binding.convert.converters.StringToDate;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.validation.ValidationContext;
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
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.test.MockRequestControlContext;
import org.springframework.webflow.validation.WebFlowMessageCodesResolver;

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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		assertFalse(view.userEventQueued());
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
		assertTrue(view.userEventQueued());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertFalse(context.getFlashScope().contains(ViewActionStateHolder.KEY));
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
		assertFalse(bindBean.validationMethodInvoked);
	}

	public void testResumeEventBindingErrors() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view2.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view2.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		view2.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view2.restoreState((ViewActionStateHolder) viewActionState);
		assertFalse(view2.userEventQueued());
		view2.render();
		assertEquals(context2.getFlowScope().get("bindBean"), model.get("bindBean"));
		BindingModel bm = (BindingModel) model.get(BindingResult.MODEL_KEY_PREFIX + "bindBean");
		assertNotNull(bm);
		assertEquals(new Integer(3), bm.getFieldValue("integerProperty"));
		assertEquals(new SimpleDateFormat("MM-dd-yyyy").parse("01-01-2008"), bm.getFieldValue("dateProperty"));
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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

	public void testResumeEventModelBindingCustomConverter() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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

	public void testResumeEventModelBindingFieldMarker() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertEquals(true, bindBean.getBooleanProperty());
	}

	public void testResumeEventModelBindAndValidate() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertTrue(bindBean.validationMethodInvoked);
	}

	public void testResumeEventModelBindAndValidateDefaultValidatorFallback() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		assertTrue(view.userEventQueued());
		view.processUserEvent();
		assertFalse(view.userEventQueued());
		assertTrue(view.hasFlowEvent());
		assertEquals("submit", view.getFlowEvent().getId());
		assertTrue(bindBean.validationMethodInvoked);
	}

	public void testResumeEventModelValidateOnBindingErrors() throws Exception {
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
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertTrue(bindBean.validationMethodInvoked);
	}

	public void testResumeEventModelNoValidateOnBindingErrors() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("stringProperty", "foo");
		context.putRequestParameter("integerProperty", "bogus");
		context.putRequestParameter("dateProperty", "2007-01-01");
		BindBean bindBean = new ValidatingBindBean();
		StaticExpression modelObject = new StaticExpression(bindBean);
		modelObject.setExpressionString("bindBean");
		context.getMockFlowExecutionContext().putAttribute("validateOnBindingErrors", Boolean.FALSE);
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getFlowScope().put("bindBean", bindBean);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockMvcView(mvcView, context);
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertFalse(bindBean.validationMethodInvoked);
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

}
