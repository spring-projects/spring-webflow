package org.springframework.webflow.config;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockExternalContext;

public class FlowExecutorFactoryBeanTests extends TestCase {
	private FlowExecutorFactoryBean factoryBean;

	public void setUp() {
		factoryBean = new FlowExecutorFactoryBean();
	}

	public void testGetFlowExecutorNoPropertiesSet() throws Exception {
		try {
			factoryBean.afterPropertiesSet();
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetFlowExecutorBasicConfig() throws Exception {
		factoryBean.setFlowDefinitionLocator(new FlowDefinitionLocator() {
			public FlowDefinition getFlowDefinition(String id) throws NoSuchFlowDefinitionException,
					FlowDefinitionConstructionException {
				Flow flow = new Flow(id);
				ViewState view = new ViewState(flow, "view", new StubViewFactory());
				view.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("end")));
				new EndState(flow, "end");
				return flow;
			}
		});
		factoryBean.afterPropertiesSet();
		factoryBean.getObject();
	}

	public void testGetFlowExecutorOptionsSpecified() throws Exception {
		factoryBean.setFlowDefinitionLocator(new FlowDefinitionLocator() {
			public FlowDefinition getFlowDefinition(String id) throws NoSuchFlowDefinitionException,
					FlowDefinitionConstructionException {
				Flow flow = new Flow(id);
				ViewState view = new ViewState(flow, "view", new StubViewFactory());
				view.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("end")));
				new EndState(flow, "end");
				return flow;
			}
		});
		Set attributes = new HashSet();
		attributes.add(new FlowElementAttribute("foo", "bar", null));
		factoryBean.setFlowExecutionAttributes(attributes);
		FlowExecutionListener listener = new FlowExecutionListenerAdapter() {

		};
		factoryBean.setFlowExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener));
		factoryBean.setMaxFlowExecutionSnapshots(2);
		factoryBean.setMaxFlowExecutions(1);
		factoryBean.afterPropertiesSet();
		FlowExecutor executor = (FlowExecutor) factoryBean.getObject();
		MockExternalContext context = new MockExternalContext();

		MockExternalContext context2 = new MockExternalContext();
	}
}
