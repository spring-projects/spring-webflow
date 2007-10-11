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
package org.springframework.webflow.executor;

import org.springframework.webflow.context.ExternalContext;

/**
 * The central facade and entry-point service interface into the Spring Web Flow system for <i>driving the executions of
 * flow definitions</i>. This interface defines a coarse-grained system boundary suitable for invocation by most
 * clients.
 * <p>
 * Implementations of this interface abstract away much of the internal complexity of the web flow execution subsystem,
 * which consists of launching and resuming managed flow executions from repositories.
 * 
 * @author Keith Donald
 */
public interface FlowExecutor {

	/**
	 * Execute the flow request initiated by the provided external context.
	 * @param context the external context, representing a client environment calling into Spring Web Flow
	 */
	public void execute(ExternalContext context);
}