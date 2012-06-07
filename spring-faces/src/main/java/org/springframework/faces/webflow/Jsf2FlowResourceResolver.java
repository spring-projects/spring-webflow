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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated Use FlowResourceResolver
 */
@Deprecated
public class Jsf2FlowResourceResolver extends FlowResourceResolver {

	Log logger = LogFactory.getLog(FlowExternalContext.class);

	public Jsf2FlowResourceResolver() {
		this.logger.warn("Jsf2FlowResourceResolver has been deprecated, please update your faces-config.xml to use FlowResourceResolver");
	}
}
