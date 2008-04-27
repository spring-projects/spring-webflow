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
package org.springframework.webflow.execution.repository.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshot;
import org.springframework.webflow.execution.repository.snapshot.SnapshotNotFoundException;

/**
 * A group of flow execution snapshots. Simple typed data structure backed by a map and linked list. Supports expelling
 * the oldest snapshot if the maximum size is met.
 * 
 * @author Keith Donald
 */
class SimpleFlowExecutionSnapshotGroup implements FlowExecutionSnapshotGroup, Serializable {

	/**
	 * The snapshot map; the key is a snapshot id, and the value is a {@link FlowExecutionSnapshot} object.
	 */
	private Map snapshots = new HashMap();

	/**
	 * An ordered list of snapshot ids. Each snapshot id represents an pointer to a {@link FlowExecutionSnapshot} in the
	 * map. The first element is the oldest snapshot and the last is the youngest.
	 */
	private LinkedList snapshotIds = new LinkedList();

	/**
	 * The maximum number of snapshots allowed in this group.
	 */
	private int maxSnapshots = -1;

	/**
	 * Returns the maximum number of snapshots allowed in this group.
	 */
	public int getMaxSnapshots() {
		return maxSnapshots;
	}

	/**
	 * Sets the maximum number of snapshots allowed in this group.
	 * @param maxSnapshots them max number of snapshots
	 */
	public void setMaxSnapshots(int maxSnapshots) {
		this.maxSnapshots = maxSnapshots;
	}

	public FlowExecutionSnapshot getSnapshot(Serializable snapshotId) throws SnapshotNotFoundException {
		FlowExecutionSnapshot snapshot = (FlowExecutionSnapshot) snapshots.get(snapshotId);
		if (snapshot == null) {
			throw new SnapshotNotFoundException(snapshotId);
		}
		return snapshot;
	}

	public void addSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot) {
		snapshots.put(snapshotId, snapshot);
		if (snapshotIds.contains(snapshotId)) {
			snapshotIds.remove(snapshotId);
		}
		snapshotIds.add(snapshotId);
		if (maxExceeded()) {
			removeOldestSnapshot();
		}
	}

	public void updateSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot)
			throws SnapshotNotFoundException {
		if (!snapshots.containsKey(snapshotId)) {
			throw new SnapshotNotFoundException(snapshotId);
		}
		snapshots.put(snapshotId, snapshot);
	}

	public void removeSnapshot(Serializable snapshotId) {
		snapshots.remove(snapshotId);
		snapshotIds.remove(snapshotId);
	}

	public void removeAllSnapshots() {
		snapshots.clear();
		snapshotIds.clear();
	}

	public int getSnapshotCount() {
		return snapshotIds.size();
	}

	/**
	 * Has the maximum number of snapshots in this group been exceeded?
	 */
	private boolean maxExceeded() {
		return maxSnapshots > 0 && snapshotIds.size() > maxSnapshots;
	}

	/**
	 * Remove the olders snapshot from this group.
	 */
	private void removeOldestSnapshot() {
		snapshots.remove(snapshotIds.removeFirst());
	}

}