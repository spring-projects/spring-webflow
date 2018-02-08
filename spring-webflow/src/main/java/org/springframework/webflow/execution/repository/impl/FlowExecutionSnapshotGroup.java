package org.springframework.webflow.execution.repository.impl;

import java.io.Serializable;

import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshot;
import org.springframework.webflow.execution.repository.snapshot.SnapshotNotFoundException;

/**
 * A group of flow execution snapshots. A SPI interface that encapsulates the storage management for a group of flow
 * execution snapshots.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionSnapshotGroup {

	/**
	 * Returns the snapshot with the provided <code>id</code>, or <code>null</code> if no such snapshot exists with that
	 * id.
	 * @param snapshotId the snapshot id
	 * @return the continuation
	 * @throws SnapshotNotFoundException if the id does not match a continuation in this group
	 */
	FlowExecutionSnapshot getSnapshot(Serializable snapshotId) throws SnapshotNotFoundException;

	/**
	 * Add a flow execution snapshot with given id to this group.
	 * @param snapshotId the snapshot id
	 * @param snapshot the snapshot
	 */
	void addSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot);

	/**
	 * Update the snapshot with the given id. Does nothing if no snapshot has been added with the id provided.
	 * @param snapshotId the snapshot id
	 * @param snapshot the new snapshot
	 */
	void updateSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot);

	/**
	 * Remove the snapshot with the given id. Does nothing if no snapshot has been added with the id provided.
	 * @param snapshotId the continuation id
	 */
	void removeSnapshot(Serializable snapshotId);

	/**
	 * Remove all snapshots in this group. Does nothing if no snapshots have been added to this group.
	 */
	void removeAllSnapshots();

	/**
	 * Returns the count of snapshots in this group.
	 */
	int getSnapshotCount();

	/**
	 * Gets the next snapshot id for new snapshot to add to this group.
	 * @return the next snapshot id
	 */
	Serializable nextSnapshotId();

}