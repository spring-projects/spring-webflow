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
package org.springframework.webflow.execution.repository.continuation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.springframework.webflow.execution.FlowExecution;

/**
 * A factory that creates new instances of flow execution snapshots based on standard Java serialization.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SerializedFlowExecutionSnapshotFactory implements FlowExecutionSnapshotFactory {

	/**
	 * Flag to toggle snapshot compression; compression is on by default.
	 */
	private boolean compress = true;

	/**
	 * Returns whether or not the snapshots should be compressed.
	 */
	public boolean getCompress() {
		return compress;
	}

	/**
	 * Set whether or not the snapshots should be compressed.
	 */
	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public FlowExecutionSnapshot createSnapshot(FlowExecution flowExecution) throws SnapshotCreationException {
		return new SerializedFlowExecutionSnapshot(flowExecution, compress);
	}

	public FlowExecutionSnapshot restoreSnapshot(byte[] bytes) throws SnapshotUnmarshalException {
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			try {
				return (FlowExecutionSnapshot) ois.readObject();
			} finally {
				ois.close();
			}
		} catch (IOException e) {
			throw new SnapshotUnmarshalException("IO problem while creating the flow execution snapshot", e);
		} catch (ClassNotFoundException e) {
			throw new SnapshotUnmarshalException("Class not found while creating the flow execution snapshot", e);
		}
	}
}