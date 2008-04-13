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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy that encapsulates the source of a flow variable value.
 * @author Keith Donald
 */
public interface VariableValueFactory {

	/**
	 * Creates the variable's initial value.
	 * @param context the currently executing flow request
	 * @return the value
	 */
	public Object createInitialValue(RequestContext context);

	/**
	 * Restore any references the variable's value needs to other objects. Such references may have been lost during
	 * deserialization, for example, and need to be restored.
	 * @param value the current variable value
	 * @param context the currently executing flow request
	 */
	public void restoreReferences(Object value, RequestContext context);
}
