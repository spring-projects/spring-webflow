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

import jakarta.faces.application.StateManager;
import jakarta.faces.application.StateManagerWrapper;
import jakarta.faces.context.FacesContext;

/**
 * Custom {@link StateManager} that manages ensures web flow's state is always stored server side.
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * 
 * @since 2.4
 */
public class FlowStateManager extends StateManagerWrapper {

	private final StateManager wrapped;

	public FlowStateManager(StateManager wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	public StateManager getWrapped() {
		return this.wrapped;
	}

	public boolean isSavingStateInClient(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return super.isSavingStateInClient(context);
		} else {
			return false;
		}
	}
}
