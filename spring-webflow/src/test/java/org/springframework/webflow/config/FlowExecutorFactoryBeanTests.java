package org.springframework.webflow.config;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;

public class FlowExecutorFactoryBeanTests {
	private FlowExecutorFactoryBean factoryBean;

	@BeforeEach
	public void setUp() {
		factoryBean = new FlowExecutorFactoryBean();
	}

	@Test
	public void testGetFlowExecutorNoPropertiesSet() throws Exception {
		try {
			factoryBean.afterPropertiesSet();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetFlowExecutorBasicConfig() throws Exception {
		factoryBean.setFlowDefinitionLocator(id -> {
			Flow flow = new Flow(id);
			ViewState view = new ViewState(flow, "view", new StubViewFactory());
			view.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("end")));
			new EndState(flow, "end");
			return flow;
		});
		factoryBean.afterPropertiesSet();
		factoryBean.getObject();
	}

	@Test
	public void testGetFlowExecutorOptionsSpecified() throws Exception {
		factoryBean.setFlowDefinitionLocator(id -> {
			Flow flow = new Flow(id);
			ViewState view = new ViewState(flow, "view", new StubViewFactory());
			view.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("end")));
			new EndState(flow, "end");
			return flow;
		});
		Set<FlowElementAttribute> attributes = new HashSet<>();
		attributes.add(new FlowElementAttribute("foo", "bar", null));
		factoryBean.setFlowExecutionAttributes(attributes);
		FlowExecutionListener listener = new FlowExecutionListener() {};
		factoryBean.setFlowExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener));
		factoryBean.setMaxFlowExecutionSnapshots(2);
		factoryBean.setMaxFlowExecutions(1);
		factoryBean.afterPropertiesSet();
		factoryBean.getObject();
	}
}
