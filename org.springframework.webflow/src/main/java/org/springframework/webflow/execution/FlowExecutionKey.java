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

import java.io.Serializable;

import org.springframework.webflow.execution.repository.FlowExecutionRepository;

/**
 * A key that uniquely identifies a flow execution in a managed {@link FlowExecutionRepository}. Serves as a flow
 * execution's persistent identity.
 * <p>
 * This class is abstract. The repository subsystem encapsulates the structure of concrete key implementations.
 * 
 * @author Keith Donald
 */
public abstract class FlowExecutionKey implements Serializable {

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public abstract String toString();
}