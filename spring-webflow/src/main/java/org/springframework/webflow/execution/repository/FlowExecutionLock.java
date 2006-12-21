/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.execution.repository;

/**
 * A pessmistic lock to obtain exclusive rights to a flow execution. Used to
 * prevent conflicts when multiple requests to manipulate a flow execution
 * arrive from different threads concurrently.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionLock {

	/**
	 * Acquire the flow execution lock. This method will block until the lock
	 * becomes available for acquisition.
	 */
	public void lock();

	/**
	 * Release the flow execution lock.
	 */
	public void unlock();
}