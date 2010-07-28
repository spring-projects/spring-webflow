package org.springframework.faces.webflow;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

/**
 * Serialized UIViewRoot stored in view scope associated with a Web Flow View State.
 * 
 * @author Jeremy Grelle
 */
public class FlowSerializedView implements Serializable {

	private Object treeStructure;

	private Object componentState;

	private String viewId;

	/**
	 * Creates a new serialized view
	 * @param viewId the view id
	 * @param treeStructure the tree structure of the view
	 * @param componentState the component state
	 */
	public FlowSerializedView(String viewId, Object treeStructure, Object componentState) {
		this.viewId = viewId;
		this.treeStructure = treeStructure;
		this.componentState = componentState;
	}

	public String getViewId() {
		return this.viewId;
	}

	public Object getTreeStructure() {
		return this.treeStructure;
	}

	public Object getComponentState() {
		return this.componentState;
	}

	public Object[] asTreeStructAndCompStateArray() {
		return new Object[] { this.treeStructure, this.componentState };
	}

	public String toString() {
		return new ToStringCreator(this).append("viewId", viewId).toString();
	}
}
