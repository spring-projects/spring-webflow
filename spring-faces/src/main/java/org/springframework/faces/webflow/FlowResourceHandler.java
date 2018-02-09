/*
 * Copyright 2004-2012 the original author or authors.
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ResourceResolver;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Resolves Facelets resources using Spring Resource paths such as "classpath:foo.xhtml".
 */
public class FlowResourceHandler extends ResourceHandlerWrapper {

	private final ResourceHandler wrapped;

	public FlowResourceHandler(ResourceHandler delegate) {
		this.wrapped = delegate;
	}

	@Override
	public ResourceHandler getWrapped() {
		return this.wrapped;
	}

	// FIXME will need to copy logic from FlowResourceResolver

}
