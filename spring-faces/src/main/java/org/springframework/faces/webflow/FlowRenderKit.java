/*
 * Copyright 2004-2015 the original author or authors.
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

/**
 * A render kit implementation that ensures use of Web Flow's FlowViewResponseStateManager, which takes over reading and
 * writing JSF state and manages that in Web Flow's view scope.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 */
import java.lang.reflect.Constructor;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

public class FlowRenderKit extends RenderKitWrapper {

	private final RenderKit wrapped;

	private final ResponseStateManager flowViewResponseStateManager;

	public FlowRenderKit(RenderKit wrapped) {
		this.wrapped = wrapped;
		this.flowViewResponseStateManager = initResponseStateManager(wrapped.getResponseStateManager());
	}

	private ResponseStateManager initResponseStateManager(ResponseStateManager wrapped) {
		if (JsfRuntimeInformation.isMojarraPresent() && !JsfRuntimeInformation.isMyFacesInUse()) {
			return new FlowResponseStateManager(wrapped);
		}
		Constructor<?> constructor;
		try {
			String className = "org.springframework.faces.webflow.MyFacesFlowResponseStateManager";
			Class<?> clazz = ClassUtils.forName(className, FlowRenderKit.class.getClassLoader());
			constructor = ClassUtils.getConstructorIfAvailable(clazz, FlowResponseStateManager.class);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could not initialize MyFacesFlowResponseStateManager", e);
		} catch (LinkageError e) {
			throw new IllegalStateException("Could not initialize MyFacesFlowResponseStateManager", e);
		}
		return (ResponseStateManager) BeanUtils.instantiateClass(constructor, new FlowResponseStateManager(wrapped));
	}

	public RenderKit getWrapped() {
		return this.wrapped;
	}

	/**
	 * Returns an instance of {@link FlowResponseStateManager} in a JSF 2 environment or returns the delegates's
	 * ResponseStateManager instance otherwise.
	 */
	public ResponseStateManager getResponseStateManager() {
		if (JsfUtils.isFlowRequest()) {
			return this.flowViewResponseStateManager;
		}
		return this.wrapped.getResponseStateManager();
	}
}
