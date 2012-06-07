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

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;

/**
 * Custom {@link ExternalContext} implementation that supports custom response objects other than
 * {@link HttpServletResponse}.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 * @author Rossen Stoyanchev
 * 
 * @since 2.4
 */
public class FlowExternalContext extends ExternalContextWrapper {

	Log logger = LogFactory.getLog(FlowExternalContext.class);

	private static final String CUSTOM_RESPONSE = FlowExternalContext.class.getName() + ".customResponse";

	private final ExternalContext wrapped;

	private final RequestContext context;

	public FlowExternalContext(RequestContext context, ExternalContext wrapped) {
		this.context = context;
		this.wrapped = wrapped;
	}

	public ExternalContext getWrapped() {
		return this.wrapped;
	}

	public Object getResponse() {
		if (this.context.getRequestScope().contains(CUSTOM_RESPONSE)) {
			return this.context.getRequestScope().get(CUSTOM_RESPONSE);
		}
		return super.getResponse();
	}

	public void setResponse(Object response) {
		this.context.getRequestScope().put(CUSTOM_RESPONSE, response);
		super.setResponse(response);
	}

	public void responseSendError(int statusCode, String message) throws IOException {
		this.logger.debug("Sending error HTTP status code " + statusCode + " with message '" + message + "'");
		super.responseSendError(statusCode, message);
	}

}
