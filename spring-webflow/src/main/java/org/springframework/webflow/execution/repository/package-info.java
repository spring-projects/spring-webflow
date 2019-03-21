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
 * The flow execution repository subsystem for saving and restoring managed flow executions.
 *
 * <p>The central concept defined by this package is the
 * {@link org.springframework.webflow.execution.repository.FlowExecutionRepository}, representing a persistent store
 * for one or more FlowExecution objects that capture the state of user conversations in a form that can be restored
 * on subsequent requests.
 */
package org.springframework.webflow.execution.repository;

