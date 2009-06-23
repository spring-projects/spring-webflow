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
package org.springframework.webflow.test;

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;

/**
 * Trivial flow execution key factory implementation that returns a mock flow execution key each time. The mock key
 * returned is unique: its value is the result of incrementing a sequence managed in static memory.
 * 
 * @author Keith Donald
 */
public class MockFlowExecutionKeyFactory implements FlowExecutionKeyFactory {
	public FlowExecutionKey getKey(FlowExecution execution) {
		return new GeneratedFlowExecutionKey();
	}

	public void removeAllFlowExecutionSnapshots(FlowExecution execution) {
	}

	public void removeFlowExecutionSnapshot(FlowExecution execution) {
	}

	public void updateFlowExecutionSnapshot(FlowExecution execution) {
	}

}
