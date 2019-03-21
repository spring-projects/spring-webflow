/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Core, stable abstractions for representing runtime executions of flow definitions.
 *
 * <p>The central concept defined by this package is the {@link org.springframework.webflow.execution.FlowExecution}
 * interface, which represents a single instance of a top-level flow definition.
 *
 * <p>The following classes and interfaces are of particular interest:
 * <ul>
 * <li>{@link org.springframework.webflow.execution.FlowExecutionFactory} - An abstract factory for creating new flow
 * executions.</li>
 * <li>{@link org.springframework.webflow.execution.repository.FlowExecutionRepository} - A DAO for persisting and
 * restoring existing flow executions.</li>
 * <li>{@link org.springframework.webflow.execution.FlowExecutionListener} - An observer interface to be implemented
 * by objects that are interested in flow execution lifecycle events.</li>
 * </ul>
 *
 * <p>This package depends on the definition package.
 */
package org.springframework.webflow.execution;

