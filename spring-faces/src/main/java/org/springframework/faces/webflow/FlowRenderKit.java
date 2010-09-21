/*
 * Copyright 2004-2010 the original author or authors.
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
 * writing JSF state and manages that in Web Flow's view scope. The FlowViewResponseStateManager is plugged in only in a 
 * JSF 2 environment.
 * 
 * Note that partial state saving in Apache MyFaces is not yet supported. Use the javax.faces.PARTIAL_STATE_SAVING context 
 * parameter in web.xml to disable it.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

public class FlowRenderKit extends RenderKitWrapper {

	private RenderKit delegate;

	private FlowViewResponseStateManager responseStateManager;

	public FlowRenderKit(RenderKit delegate) {
		this.delegate = delegate;
		if (JsfRuntimeInformation.isAtLeastJsf20()) {
			this.responseStateManager = new FlowViewResponseStateManager(delegate.getResponseStateManager());
		}
	}

	public RenderKit getWrapped() {
		return delegate;
	}

	/**
	 * Returns an instance of {@link FlowViewResponseStateManager} in a JSF 2 environment or returns the delegates's
	 * ResponseStateManager instance otherwise.
	 */
	public ResponseStateManager getResponseStateManager() {
		return (JsfRuntimeInformation.isPartialStateSavingSupported()) ? responseStateManager : delegate
				.getResponseStateManager();
	}

}
