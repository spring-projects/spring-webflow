package org.springframework.webflow.engine.builder;

import org.springframework.binding.expression.Expression;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;

public interface ViewFactoryCreator {

	public ViewFactory createViewFactory(Expression viewId, ResourceLoader viewResourceLoader);

	public Action createFinalResponseAction(Expression viewId, ResourceLoader viewResourceLoader);

}
