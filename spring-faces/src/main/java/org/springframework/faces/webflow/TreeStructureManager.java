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
package org.springframework.faces.webflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Helper class for building and restoring the structure of the JSF component tree.
 * 
 * Largely based on MyFaces implementation, with minor changes for Spring Web Flow's state saving strategy.
 * 
 * @author Jeremy Grelle
 * @author Manfred Geiler
 */
class TreeStructureManager {
	public Serializable buildTreeStructureToSave(UIViewRoot viewRoot) {
		return internalBuildTreeStructureToSave(viewRoot);
	}

	private TreeStructComponent internalBuildTreeStructureToSave(UIComponent component) {
		TreeStructComponent structComp = new TreeStructComponent(component.getClass().getName(), component.getId());

		// children
		if (component.getChildCount() > 0) {
			List childList = component.getChildren();
			List structChildList = new ArrayList();
			for (int i = 0, len = childList.size(); i < len; i++) {
				UIComponent child = (UIComponent) childList.get(i);
				if (!child.isTransient()) {
					TreeStructComponent structChild = internalBuildTreeStructureToSave(child);
					structChildList.add(structChild);
				}
			}
			TreeStructComponent[] childArray = (TreeStructComponent[]) structChildList
					.toArray(new TreeStructComponent[structChildList.size()]);
			structComp.setChildren(childArray);
		}

		// facets
		Map facetMap = component.getFacets();
		if (!facetMap.isEmpty()) {
			List structFacetList = new ArrayList();
			for (Iterator it = facetMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				UIComponent child = (UIComponent) entry.getValue();
				if (!child.isTransient()) {
					String facetName = (String) entry.getKey();
					TreeStructComponent structChild = internalBuildTreeStructureToSave(child);
					structFacetList.add(new Object[] { facetName, structChild });
				}
			}
			Object[] facetArray = structFacetList.toArray(new Object[structFacetList.size()]);
			structComp.setFacets(facetArray);
		}

		return structComp;
	}

	public UIViewRoot restoreTreeStructure(Object treeStructRoot) {
		if (treeStructRoot instanceof TreeStructComponent) {
			return (UIViewRoot) internalRestoreTreeStructure((TreeStructComponent) treeStructRoot);
		}
		throw new IllegalArgumentException("TreeStructure of type " + treeStructRoot.getClass().getName()
				+ " is not supported.");
	}

	private UIComponent internalRestoreTreeStructure(TreeStructComponent treeStructComp) {
		String compClass = treeStructComp.getComponentClass();
		String compId = treeStructComp.getComponentId();
		UIComponent component;
		try {
			component = (UIComponent) BeanUtils.instantiateClass(ClassUtils.forName(compClass));
		} catch (Exception ex) {
			throw new FacesException("Could not restore the component tree structure.", ex);
		}
		component.setId(compId);

		// children
		TreeStructComponent[] childArray = treeStructComp.getChildren();
		if (childArray != null) {
			List childList = component.getChildren();
			for (int i = 0, len = childArray.length; i < len; i++) {
				UIComponent child = internalRestoreTreeStructure(childArray[i]);
				childList.add(child);
			}
		}

		// facets
		Object[] facetArray = treeStructComp.getFacets();
		if (facetArray != null) {
			Map facetMap = component.getFacets();
			for (int i = 0, len = facetArray.length; i < len; i++) {
				Object[] tuple = (Object[]) facetArray[i];
				String facetName = (String) tuple[0];
				TreeStructComponent structChild = (TreeStructComponent) tuple[1];
				UIComponent child = internalRestoreTreeStructure(structChild);
				facetMap.put(facetName, child);
			}
		}

		return component;
	}

	public static class TreeStructComponent implements Serializable {
		private static final long serialVersionUID = 5069109074684737231L;
		private String componentClass;
		private String componentId;
		private TreeStructComponent[] children = null; // Array of children
		private Object[] facets = null; // Array of Array-tuples with Facetname and TreeStructComponent

		TreeStructComponent(String componentClass, String componentId) {
			this.componentClass = componentClass;
			this.componentId = componentId;
		}

		public String getComponentClass() {
			return componentClass;
		}

		public String getComponentId() {
			return componentId;
		}

		void setChildren(TreeStructComponent[] children) {
			this.children = children;
		}

		TreeStructComponent[] getChildren() {
			return children;
		}

		Object[] getFacets() {
			return facets;
		}

		void setFacets(Object[] facets) {
			this.facets = facets;
		}

		public String toString() {
			if (JsfUtils.isFlowRequest()) {
				return RequestContextHolder.getRequestContext().getFlowExecutionContext().getKey().toString();
			} else {
				return super.toString();
			}
		}

	}

}
