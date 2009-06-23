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
package org.springframework.webflow.execution.repository.snapshot;

import java.io.Serializable;

/**
 * A snapshot that can be used to restore a FlowExecution using a {@link FlowExecutionSnapshotFactory}.
 * 
 * @see FlowExecutionSnapshotFactory
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public abstract class FlowExecutionSnapshot implements Serializable {

}