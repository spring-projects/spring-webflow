package org.springframework.webflow.executor.jsf;

import java.io.FileNotFoundException;

import javax.faces.el.ValueBinding;

import org.easymock.MockControl;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

public class JSF11ManagedBeanAccessTests extends AbstractXmlFlowExecutionTests {

    JSF jsf;
    JSFManagedBean jsfBean;
    JSFModel jsfModel;
    FlowPhaseListener flowPhaseListener;
    FlowNavigationHandler flowNavigationHandler;
    MockService service;
    MockControl serviceControl;

    protected void setUp() throws Exception {
	super.setUp();
	serviceControl = MockControl.createControl(MockService.class);
	service = (MockService) serviceControl.getMock();
	jsf = new JSF("JSFManagedBeanAccessTests");
	jsf.setUp();
	configureJSFForSWF();
    }

    private void configureJSFForSWF() {
	DelegatingFlowVariableResolver dfvr = new DelegatingFlowVariableResolver(jsf.application()
		.getVariableResolver());
	FlowVariableResolver fvr = new FlowVariableResolver(dfvr);
	jsf.application().setVariableResolver(fvr);
	FlowPropertyResolver fpr = new FlowPropertyResolver(jsf.application().getPropertyResolver());
	jsf.application().setPropertyResolver(fpr);

	// flowPhaseListener = new FlowPhaseListener();
	// jsf.lifecycle().addPhaseListener(flowPhaseListener);
	flowNavigationHandler = new FlowNavigationHandler(jsf.application().getNavigationHandler());
	jsf.application().setNavigationHandler(flowNavigationHandler);

	jsf.externalContext().getRequestMap().put("JsfBean", new JSFManagedBean());

	// flowPhaseListener.setupELContext(jsf.facesContext());
    }

    protected void tearDown() throws Exception {
	super.tearDown();
	jsf.tearDown();
	// flowPhaseListener.teardownELContext();
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
	jsfBean.setModel(jsfModel);
    }

    public void testManagedBeanProperyAsArgument() {
	testManagedBeanExpression();
	jsfBean.setProp1("arg");
	service.doSomething(jsfBean.getProp1());
	serviceControl.replay();

	startFlow();
	signalEvent("event1");
	serviceControl.verify();
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
	// testManagedBeanExpression();
	// startFlow();

	// TODO - Add a flow scoped bean definition to the test application context
	// ValueBinding propBinding = jsf.application().createValueBinding("#{flowScopedModel}");
	// jsfModel = (JSFModel) propBinding.getValue(jsf.facesContext());
	// assertNotNull("This test won't pass until custom scopes are implemented.", jsfModel);
	// jsfBean.setModel(jsfModel);
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
	    return createFlowDefinitionResource(ResourceUtils.getFile("classpath:jsf-flow.xml").getPath());
	} catch (FileNotFoundException e) {
	    fail(e.getMessage());
	    return null;
	}
    }

    protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
	serviceRegistry.setExpressionParser(new Jsf11ELExpressionParser());
	serviceRegistry.registerBean("serviceBean", service);
	StaticWebApplicationContext ctx = new StaticWebApplicationContext();
	ctx.registerPrototype("jsfModel", JSFModel.class);
	jsf.externalContext().getApplicationMap()
		.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
    }
}
