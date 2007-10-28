package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.lifecycle.LifecycleFactory;

import org.springframework.binding.expression.Expression;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A ViewFactoryCreator implementation for creating JSF ViewFactories
 * @author Jeremy Grelle
 * 
 */
public class JsfViewFactoryCreator implements ViewFactoryCreator {

	public Action createFinalResponseAction(Expression viewName, ResourceLoader resourceLoader) {
		return new JsfRenderFinalResponseAction(new JsfViewFactory(((LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY)).getLifecycle(FlowLifecycleFactory.FLOW_LIFECYCLE_ID),
				viewName));
	}

	public ViewFactory createViewFactory(Expression viewName, ResourceLoader resourceLaoder) {
		return new JsfViewFactory(((LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY))
				.getLifecycle(FlowLifecycleFactory.FLOW_LIFECYCLE_ID), viewName);
	}

}
