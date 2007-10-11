package org.springframework.webflow.engine.builder.xml;

import javax.faces.FactoryFinder;
import javax.faces.lifecycle.LifecycleFactory;

import org.springframework.binding.expression.Expression;
import org.springframework.faces.webflow.FlowLifecycleFactory;
import org.springframework.faces.webflow.JsfRenderFinalResponseAction;
import org.springframework.faces.webflow.JsfViewFactory;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A ViewFactoryCreator implementation for creating JSF ViewFactories
 * @author Jeremy Grellex
 * 
 */
public class JsfViewFactoryCreator implements ViewFactoryCreator {

	public Action createFinalResponseAction(Expression viewName) {
		return new JsfRenderFinalResponseAction(new JsfViewFactory(((LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY)).getLifecycle(FlowLifecycleFactory.FLOW_LIFECYCLE_ID),
				viewName));
	}

	public ViewFactory createViewFactory(Expression viewName) {
		return new JsfViewFactory(((LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY))
				.getLifecycle(FlowLifecycleFactory.FLOW_LIFECYCLE_ID), viewName);
	}

}
