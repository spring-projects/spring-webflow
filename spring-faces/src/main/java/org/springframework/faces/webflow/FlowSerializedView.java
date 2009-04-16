package org.springframework.faces.webflow;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

public class FlowSerializedView implements Serializable {
	private Object treeStructure;

	private Object componentState;

	private String viewId;

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

	public String toString() {
		return new ToStringCreator(this).append("viewId", viewId).toString();
	}
}
