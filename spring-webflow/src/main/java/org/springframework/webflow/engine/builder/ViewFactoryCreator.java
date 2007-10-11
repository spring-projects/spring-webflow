package org.springframework.webflow.engine.builder;

import org.springframework.binding.expression.Expression;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;

public interface ViewFactoryCreator {

	public ViewFactory createViewFactory(Expression viewId);

	public Action createFinalResponseAction(Expression viewId);

}
