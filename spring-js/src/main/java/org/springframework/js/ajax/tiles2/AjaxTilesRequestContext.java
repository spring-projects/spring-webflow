/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.js.ajax.tiles2;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.context.TilesRequestContext;

/**
 * Tiles request context implementation that delegates to another {@link TilesRequestContext}.
 * 
 * <p>
 * This implementation will handle all dispatch requests by invoking {@link TilesRequestContext#include(String)} on the
 * delegate request context.
 * </p>
 * 
 * @author Scott Andrews
 */
class AjaxTilesRequestContext implements TilesRequestContext {

	private final TilesRequestContext targetReqestContext;

	/**
	 * Create a TilesRequestContext for use by AjaxTilesView
	 * 
	 * @param targetReqestContext the target {@link TilesRequestContext}
	 */
	public AjaxTilesRequestContext(TilesRequestContext targetReqestContext) {
		this.targetReqestContext = targetReqestContext;
	}

	/**
	 * Delegate call to {@link TilesRequestContext#include(String)}
	 */
	public void dispatch(String path) throws IOException {
		targetReqestContext.include(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getHeader() {
		return targetReqestContext.getHeader();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getHeaderValues() {
		return targetReqestContext.getHeaderValues();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getParam() {
		return targetReqestContext.getParam();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getParamValues() {
		return targetReqestContext.getParamValues();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getRequest() {
		return targetReqestContext.getRequest();
	}

	/**
	 * {@inheritDoc}
	 */
	public Locale getRequestLocale() {
		return targetReqestContext.getRequestLocale();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getRequestScope() {
		return targetReqestContext.getRequestScope();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResponse() {
		return targetReqestContext.getResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getSessionScope() {
		return targetReqestContext.getSessionScope();
	}

	/**
	 * {@inheritDoc}
	 */
	public void include(String path) throws IOException {
		targetReqestContext.include(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isUserInRole(String role) {
		return targetReqestContext.isUserInRole(role);
	}

}
