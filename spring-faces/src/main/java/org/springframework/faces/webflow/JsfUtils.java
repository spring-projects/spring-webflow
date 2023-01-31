/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.webflow;

import java.util.HashMap;
import java.util.Map;
import jakarta.faces.FactoryFinder;
import jakarta.faces.application.ApplicationFactory;
import jakarta.faces.component.visit.VisitContextFactory;
import jakarta.faces.context.ExceptionHandlerFactory;
import jakarta.faces.context.ExternalContextFactory;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.context.PartialViewContextFactory;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.lifecycle.LifecycleFactory;
import jakarta.faces.render.RenderKitFactory;
import jakarta.faces.view.ViewDeclarationLanguageFactory;
import jakarta.faces.view.facelets.FaceletCacheFactory;
import jakarta.faces.view.facelets.TagHandlerDelegateFactory;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Common support for the JSF integration with Spring Web Flow.
 *
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JsfUtils {

	public static void notifyAfterListeners(PhaseId phaseId, Lifecycle lifecycle, FacesContext context) {
		PhaseEvent afterPhaseEvent = new PhaseEvent(context, phaseId, lifecycle);
		for (int i = (lifecycle.getPhaseListeners().length - 1); i >= 0; i--) {
			PhaseListener listener = lifecycle.getPhaseListeners()[i];
			if (listener.getPhaseId() == phaseId || listener.getPhaseId() == PhaseId.ANY_PHASE) {
				listener.afterPhase(afterPhaseEvent);
			}
		}
	}

	public static void notifyBeforeListeners(PhaseId phaseId, Lifecycle lifecycle, FacesContext context) {
		PhaseEvent beforePhaseEvent = new PhaseEvent(context, phaseId, lifecycle);
		for (int i = 0; i < lifecycle.getPhaseListeners().length; i++) {
			PhaseListener listener = lifecycle.getPhaseListeners()[i];
			if (listener.getPhaseId() == phaseId || listener.getPhaseId() == PhaseId.ANY_PHASE) {
				listener.beforePhase(beforePhaseEvent);
			}
		}
	}

	public static boolean isFlowRequest() {
		return RequestContextHolder.getRequestContext() != null;
	}

	public static boolean isAsynchronousFlowRequest() {
		return isFlowRequest() && RequestContextHolder.getRequestContext().getExternalContext().isAjaxRequest();
	}

	/**
	 * Find a factory of the specified class using JSFs {@link FactoryFinder} class.
	 * @param factoryClass the factory class to find
	 * @return the factory instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findFactory(Class<T> factoryClass) {
		Assert.notNull(factoryClass, "FactoryClass must not be null");
		String name = FACTORY_NAMES.get(factoryClass);
		Assert.state(name != null, "Unknown factory class " + factoryClass.getName());
		return (T) FactoryFinder.getFactory(name);
	}

	private static final Map<Class<?>, String> FACTORY_NAMES;

	static {
		FACTORY_NAMES = new HashMap<>();
		FACTORY_NAMES.put(ApplicationFactory.class, FactoryFinder.APPLICATION_FACTORY);
		FACTORY_NAMES.put(ExceptionHandlerFactory.class, FactoryFinder.EXCEPTION_HANDLER_FACTORY);
		FACTORY_NAMES.put(ExternalContextFactory.class, FactoryFinder.EXTERNAL_CONTEXT_FACTORY);
		FACTORY_NAMES.put(FaceletCacheFactory.class, FactoryFinder.FACELET_CACHE_FACTORY);
		FACTORY_NAMES.put(FacesContextFactory.class, FactoryFinder.FACES_CONTEXT_FACTORY);
		FACTORY_NAMES.put(LifecycleFactory.class, FactoryFinder.LIFECYCLE_FACTORY);
		FACTORY_NAMES.put(PartialViewContextFactory.class, FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY);
		FACTORY_NAMES.put(RenderKitFactory.class, FactoryFinder.RENDER_KIT_FACTORY);
		FACTORY_NAMES.put(TagHandlerDelegateFactory.class, FactoryFinder.TAG_HANDLER_DELEGATE_FACTORY);
		FACTORY_NAMES.put(ViewDeclarationLanguageFactory.class, FactoryFinder.VIEW_DECLARATION_LANGUAGE_FACTORY);
		FACTORY_NAMES.put(VisitContextFactory.class, FactoryFinder.VISIT_CONTEXT_FACTORY);
	}


}
