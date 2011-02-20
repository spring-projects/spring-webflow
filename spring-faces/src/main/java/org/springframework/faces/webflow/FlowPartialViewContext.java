/*
 * Copyright 2004-2011 the original author or authors.
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

import java.util.Arrays;
import java.util.Collection;

import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * Web Flow {@link PartialViewContext} implementation allowing ids for partial rendering to be specified from the
 * server-side. This is done in a flow definition with the &lt;render fragments="..." /&gt; action.
 * 
 * @author Rossen Stoyanchev
 */
public class FlowPartialViewContext extends PartialViewContextWrapper {

	private PartialViewContext delegate;

	public FlowPartialViewContext(PartialViewContext delegate) {
		this.delegate = delegate;
	}

	@Override
	public PartialViewContext getWrapped() {
		return delegate;
	}

	@Override
	public void setPartialRequest(boolean isPartialRequest) {
		getWrapped().setPartialRequest(isPartialRequest);
	}

	@Override
	public Collection<String> getRenderIds() {
		if (JsfUtils.isFlowRequest()) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			String[] fragmentIds = (String[]) requestContext.getFlashScope().get(View.RENDER_FRAGMENTS_ATTRIBUTE);
			if (fragmentIds != null && fragmentIds.length > 0) {
				return Arrays.asList(fragmentIds);
			}
		}
		return getWrapped().getRenderIds();
	}

}
