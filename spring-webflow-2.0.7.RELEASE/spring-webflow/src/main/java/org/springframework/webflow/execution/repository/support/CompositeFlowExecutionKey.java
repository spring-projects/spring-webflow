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
package org.springframework.webflow.execution.repository.support;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.BadlyFormattedFlowExecutionKeyException;

/**
 * A flow execution key that consists of two parts:
 * <ol>
 * <li>A <i>executionId</i>, identifying a logical {@link FlowExecution} that is running.
 * <li>A <i>snapshotId</i>, identifying a physical flow execution snapshot that can be restored.
 * </ol>
 * @author Keith Donald
 */
public class CompositeFlowExecutionKey extends FlowExecutionKey {

	private static final String EXECUTION_ID_PREFIX = "e";

	private static final String SNAPSHOT_ID_PREFIX = "s";

	private static final String FORMAT = EXECUTION_ID_PREFIX + "<executionId>" + SNAPSHOT_ID_PREFIX + "<snapshotId>";

	private Serializable executionId;

	private Serializable snapshotId;

	/**
	 * Create a new composite flow execution key given the composing parts.
	 * @param executionId the execution id
	 * @param snapshotId the snapshot id
	 */
	public CompositeFlowExecutionKey(Serializable executionId, Serializable snapshotId) {
		Assert.notNull(executionId, "The execution id is required");
		Assert.notNull(snapshotId, "The snapshot id is required");
		this.executionId = executionId;
		this.snapshotId = snapshotId;
	}

	/**
	 * Returns the execution id part of this key.
	 */
	public Serializable getExecutionId() {
		return executionId;
	}

	/**
	 * Returns the snapshot id part of this key.
	 */
	public Serializable getSnapshotId() {
		return snapshotId;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof CompositeFlowExecutionKey)) {
			return false;
		}
		CompositeFlowExecutionKey other = (CompositeFlowExecutionKey) obj;
		return executionId.equals(other.executionId) && snapshotId.equals(other.snapshotId);
	}

	public int hashCode() {
		return executionId.hashCode() + snapshotId.hashCode();
	}

	public String toString() {
		return new StringBuffer().append(EXECUTION_ID_PREFIX).append(executionId).append(SNAPSHOT_ID_PREFIX).append(
				snapshotId).toString();
	}

	// static helpers

	/**
	 * Returns a string description of the format of this key.
	 */
	public static String getFormat() {
		return FORMAT;
	}

	/**
	 * Helper that splits the string-form of an instance of this class into its "parts" so the parts can be easily
	 * parsed.
	 * @param encodedKey the string-encoded composite flow execution key
	 * @return the composite key parts as a String array (executionId = 0, snapshotId = 1)
	 */
	public static String[] keyParts(String encodedKey) throws BadlyFormattedFlowExecutionKeyException {
		if (!encodedKey.startsWith(EXECUTION_ID_PREFIX)) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey, FORMAT);
		}
		int snapshotStart = encodedKey.indexOf(SNAPSHOT_ID_PREFIX, EXECUTION_ID_PREFIX.length());
		if (snapshotStart == -1) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey, FORMAT);
		}
		String executionId = encodedKey.substring(EXECUTION_ID_PREFIX.length(), snapshotStart);
		String snapshotId = encodedKey.substring(snapshotStart + SNAPSHOT_ID_PREFIX.length());
		return new String[] { executionId, snapshotId };
	}
}