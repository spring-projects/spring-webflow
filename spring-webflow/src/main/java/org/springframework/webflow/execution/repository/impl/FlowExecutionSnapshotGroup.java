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
	 * Returns the snapshot with the provided <code>id</code>, or <code>null</code> if no such snapshot exists with
	 * that id.
	 * @param snapshotId the snapshot id
	 * @return the continuation
	 * @throws SnapshotNotFoundException if the id does not match a continuation in this group
	 */
	public FlowExecutionSnapshot getSnapshot(Serializable snapshotId) throws SnapshotNotFoundException;

	/**
	 * Add a flow execution snapshot with given id to this group.
	 * @param snapshotId the snapshot id
	 * @param snapshot the snapshot
	 */
	public void addSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot);

	/**
	 * Update the snapshot with the given id.
	 * @param snapshotId the snapshot id
	 * @param snapshot the new snapshot
	 * @throws SnapshotNotFoundException if there was no previous snapshot to update
	 */
	public void updateSnapshot(Serializable snapshotId, FlowExecutionSnapshot snapshot)
			throws SnapshotNotFoundException;

	/**
	 * Remove the snapshot with the given id.
	 * @param snapshotId the continuation id
	 */
	public void removeSnapshot(Serializable snapshotId);

	/**
	 * Remove all snapshots in this group.
	 */
	public void removeAllSnapshots();

	/**
	 * Returns the count of snapshots in this group.
	 */
	public int getSnapshotCount();

}