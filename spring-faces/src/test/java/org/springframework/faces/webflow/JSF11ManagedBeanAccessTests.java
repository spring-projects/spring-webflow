package org.springframework.faces.webflow;

import java.io.FileNotFoundException;

import javax.faces.el.ValueBinding;

import org.easymock.EasyMock;
import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.faces.el.Jsf11ELExpressionParser;
import org.springframework.faces.webflow.el.DelegatingFlowVariableResolver;
import org.springframework.faces.webflow.el.FlowPropertyResolver;
import org.springframework.faces.webflow.el.FlowVariableResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.jsf.DelegatingVariableResolver;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

public class JSF11ManagedBeanAccessTests extends AbstractXmlFlowExecutionTests {

	JSFMockHelper jsf;
	JSFManagedBean jsfBean;
	JSFModel jsfModel;
	FlowPhaseListener flowPhaseListener;
	FlowNavigationHandler flowNavigationHandler;
	MockService service;
	GenericWebApplicationContext ctx;

	protected void setUp() throws Exception {
		super.setUp();
		service = (MockService) EasyMock.createMock(MockService.class);
		jsf = new JSFMockHelper();
		jsf.setUp();
		configureJSFForSWF();
	}

	private void configureJSFForSWF() {
		DelegatingVariableResolver dvr = new DelegatingVariableResolver(jsf.application().getVariableResolver());
		DelegatingFlowVariableResolver dfvr = new DelegatingFlowVariableResolver(dvr);
		FlowVariableResolver fvr = new FlowVariableResolver(dfvr);
		jsf.application().setVariableResolver(fvr);
		FlowPropertyResolver fpr = new FlowPropertyResolver(jsf.application().getPropertyResolver());
		jsf.application().setPropertyResolver(fpr);

		flowNavigationHandler = new FlowNavigationHandler(jsf.application().getNavigationHandler());
		jsf.application().setNavigationHandler(flowNavigationHandler);

		jsf.externalContext().getRequestMap().put("JsfBean", new JSFManagedBean());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		jsf.tearDown();
	}

	public void testManagedBeanExpression() {
		ValueBinding vb = jsf.application().createValueBinding("#{JsfBean}");
		jsfBean = (JSFManagedBean) vb.getValue(jsf.facesContext());
		assertNotNull(jsfBean);
	}

	public void testSWFExplicitlyScopedPropertyInjection() {
		testManagedBeanExpression();
		startFlow();

		ValueBinding propBinding = jsf.application().createValueBinding("#{flowScope.jsfModel}");
		jsfModel = (JSFModel) propBinding.getValue(jsf.facesContext());
		assertNotNull(jsfModel);
	}

	public void testManagedBeanPropertyAsArgument() {
		testManagedBeanExpression();
		jsfBean.setProp1("arg");
		service.doSomething(jsfBean.getProp1());
		EasyMock.replay(new Object[] { service });

		startFlow();
		signalEvent("event1");
		EasyMock.verify(new Object[] { service });
		assertCurrentStateEquals("viewState2");
	}

	public void testEvalManagedBeanMethod() {
		testManagedBeanExpression();
		startFlow();

		ValueBinding propBinding = jsf.application().createValueBinding("#{flowScope.jsfModel}");
		jsfModel = (JSFModel) propBinding.getValue(jsf.facesContext());
		assertNotNull(jsfModel);
		jsfModel.setValue("foo");

		signalEvent("event2");
		assertFalse(jsfBean.getValues().isEmpty());
		String addedValue = jsfBean.getValues().get(0).toString();
		assertEquals(jsfModel.getValue(), addedValue);
		assertCurrentStateEquals("viewState2");
	}

	public void testSWFScopedPropertyInjection() {
		startFlow();

		ValueBinding propBinding = jsf.application().createValueBinding("#{flowScopedModel}");
		jsfModel = (JSFModel) propBinding.getValue(jsf.facesContext());
		assertNotNull("This test won't pass until custom scopes are implemented.", jsfModel);
	}

	protected ViewSelection startFlow() throws FlowExecutionException {
		ViewSelection view = super.startFlow();
		FlowExecutionHolder holder = new FlowExecutionHolder(getFlowExecution());
		FlowExecutionHolderUtils.setFlowExecutionHolder(holder, jsf.facesContext());
		holder.setViewSelection(view);
		return view;
	}

	protected FlowDefinitionResource getFlowDefinitionResource() {
		try {
			return createFlowDefinitionResource(ResourceUtils.getFile(
					"classpath:org/springframework/faces/webflow/jsf-flow.xml").getPath());
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
			return null;
		}
	}

	protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
		serviceRegistry.setExpressionParser(new Jsf11ELExpressionParser(new ExpressionFactoryImpl()));
		serviceRegistry.registerBean("serviceBean", service);

		ctx = new GenericWebApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("org/springframework/faces/webflow/jsf-flow-beans.xml"));
		ctx.refresh();

		jsf.externalContext().getApplicationMap().put(
				GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
	}
}
