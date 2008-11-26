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

import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;

/**
 * Thrown when a flow execution snapshot cannot be found This usually occurs when the client references a snapshot that
 * has since been removed.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SnapshotNotFoundException extends FlowExecutionRepositoryException {

	private Serializable snapshotId;

	/**
	 * Creates a snapshot not found exception.
	 * @param snapshotId the snapshot id that could not be found
	 */
	public SnapshotNotFoundException(Serializable snapshotId) {
		super("No flow execution snapshot could be found with id '" + snapshotId
				+ "'; perhaps the snapshot has been removed? ");
		this.snapshotId = snapshotId;
	}

	/**
	 * The id of the snapshot that was not found.
	 */
	public Serializable getSnapshotId() {
		return snapshotId;
	}
}