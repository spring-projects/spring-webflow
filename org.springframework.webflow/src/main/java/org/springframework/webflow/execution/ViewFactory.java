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
package org.springframework.webflow.execution;

/**
 * A factory for a view that allows the client to participate in flow execution. Encapsulates creation and restoration
 * of the view implementation, including any application of request values to determine what user event was signaled.
 * 
 * @author Keith Donald
 */
public interface ViewFactory {

	/**
	 * Get the view to render for this request.
	 * @param context the flow execution request context.
	 * @return the view to render
	 */
	public View getView(RequestContext context);
}