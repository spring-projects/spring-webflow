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

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * {@link RenderKitFactory} implementation that wraps the current {@link RenderKit} implementation inside the
 * {@link FlowRenderKitWrapper}.
 * 
 * @author Jeremy Grelle
 */
public class FlowRenderKitFactory extends RenderKitFactory {

	private RenderKitFactory delegate;

	public FlowRenderKitFactory(RenderKitFactory renderKitFactory) {
		this.delegate = renderKitFactory;
	}

	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		FlowRenderKitWrapper wrapper = new FlowRenderKitWrapper(renderKit);
		delegate.addRenderKit(renderKitId, wrapper);
	}

	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		RenderKit renderKit = delegate.getRenderKit(context, renderKitId);
		if (!(renderKit instanceof FlowRenderKitWrapper)) {
			return new FlowRenderKitWrapper(renderKit);
		}
		return renderKit;
	}

	public Iterator getRenderKitIds() {
		return delegate.getRenderKitIds();
	}

}
