/*
 * Copyright 2004-2023 the original author or authors.
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

import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationWrapper;
import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;

import org.springframework.util.Assert;

/**
 * Wraps an {@link Application} instance in order to ensure Web Flow specific implementations of {@link ViewHandler} and
 * {@link StateManager} are inserted at the front of the processing chain in JSF environments. This is done by
 * intercepting the corresponding setters. All other methods are simple delegation methods.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class FlowApplication extends ApplicationWrapper {

	private final Application wrapped;

	/**
	 * Class constructor that accepts a delegate Application instance. If the delegate has default instantiation logic
	 * for its StateManager and ViewHandler instances, those will be wrapped with {@link FlowStateManager} and a
	 * {@link FlowViewHandler} instance.
	 * 
	 * @param wrapped the wrapped Application instance.
	 */
	public FlowApplication(Application wrapped) {
		Assert.notNull(wrapped, "The wrapped Application instance must not be null!");
		this.wrapped = wrapped;

		ViewHandler handler = this.wrapped.getViewHandler();
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		}

		StateManager manager = this.wrapped.getStateManager();
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		}
	}

	public Application getWrapped() {
		return this.wrapped;
	}

	/**
	 * Inserts {@link FlowStateManager} in front of the given StateManager (if not already done).
	 */
	public void setStateManager(StateManager manager) {
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		} else {
			super.setStateManager(manager);
		}
	}

	private boolean shouldWrap(StateManager manager) {
		return (manager != null) && (!(manager instanceof FlowStateManager));
	}

	private void wrapAndSetStateManager(StateManager target) {
		super.setStateManager(new FlowStateManager(target));
	}

	/**
	 * Inserts a {@link FlowViewHandler} in front of the given ViewHandler (if not already done).
	 */
	public void setViewHandler(ViewHandler handler) {
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		} else {
			super.setViewHandler(handler);
		}
	}

	private boolean shouldWrap(ViewHandler delegateViewHandler) {
		return (delegateViewHandler != null) && (!(delegateViewHandler instanceof FlowViewHandler));
	}

	private void wrapAndSetViewHandler(ViewHandler target) {
		if ((target != null) && (!(target instanceof FlowViewHandler))) {
			ViewHandler handler = new FlowViewHandler(target);
			super.setViewHandler(handler);
		}
	}
}
