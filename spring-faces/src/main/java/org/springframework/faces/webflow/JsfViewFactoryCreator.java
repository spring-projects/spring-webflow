/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.springframework.binding.expression.Expression;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A {@link ViewFactoryCreator} implementation for creating instances of a JSF-specific {@link ViewFactory}.
 * 
 * @author Jeremy Grelle
 */
public class JsfViewFactoryCreator implements ViewFactoryCreator {

	private static final String FACELETS_EXTENSION = ".xhtml";

	private Lifecycle lifecycle;

	public Action createFinalResponseAction(Expression viewName, ResourceLoader resourceLoader) {
		return new JsfFinalResponseAction(new JsfViewFactory(viewName, resourceLoader, getLifecycle()));
	}

	public ViewFactory createViewFactory(Expression viewName, ResourceLoader resourceLoader) {
		return new JsfViewFactory(viewName, resourceLoader, getLifecycle());
	}

	private Lifecycle createFlowFacesLifecycle() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle defaultLifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		return new FlowLifecycle(defaultLifecycle);
	}

	private Lifecycle getLifecycle() {
		if (lifecycle == null) {
			lifecycle = createFlowFacesLifecycle();
		}
		return lifecycle;
	}

	public String createViewIdByConvention(String viewStateId) {
		return viewStateId + FACELETS_EXTENSION;
	}

}
