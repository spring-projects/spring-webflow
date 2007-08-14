/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.definition.registry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;

/**
 * A generic registry implementation for housing one or more flow definitions.
 * <p>
 * This registry may be refreshed at runtime to "hot reload" refreshable flow definitions. Note that the refresh will
 * only reload already registered flow definitions but will not detect any new flow definitions or remove flow
 * definitions that no longer exist.
 * 
 * @author Keith Donald
 * @author Ben Hale
 */
public class FlowDefinitionRegistryImpl implements FlowDefinitionRegistry {

	private static final Log logger = LogFactory.getLog(FlowDefinitionRegistryImpl.class);

	/**
	 * The map of loaded Flow definitions maintained in this registry.
	 */
	private Map flowDefinitions;

	/**
	 * An optional parent flow definition registry.
	 */
	private FlowDefinitionRegistry parent;

	public FlowDefinitionRegistryImpl() {
		flowDefinitions = new TreeMap();
	}

	// implementing FlowDefinitionRegistryMBean

	public String[] getFlowDefinitionPaths() {
		List flowPaths = new LinkedList();
		for (Iterator namespaces = flowDefinitions.entrySet().iterator(); namespaces.hasNext();) {
			Map.Entry namespaceEntry = (Map.Entry) namespaces.next();
			String namespaceName = (String) namespaceEntry.getKey();
			Map namespace = (Map) namespaceEntry.getValue();
			for (Iterator ids = namespace.keySet().iterator(); ids.hasNext();) {
				flowPaths.add(FlowPathUtils.buildFlowPath(namespaceName, (String) ids.next()));
			}
		}
		return (String[]) flowPaths.toArray(new String[flowPaths.size()]);
	}

	public int getFlowDefinitionCount() {
		int count = 0;
		for (Iterator namespaces = flowDefinitions.values().iterator(); namespaces.hasNext();) {
			Map namespace = (Map) namespaces.next();
			count += namespace.size();
		}
		return count;
	}

	public boolean containsFlowDefinition(String flowPath) {
		Assert.hasText(flowPath, "The flow path is required");
		Map namespace = getNamespace(FlowPathUtils.extractFlowNamespace(flowPath));
		return namespace.containsKey(FlowPathUtils.extractFlowId(flowPath));
	}

	public void refresh() throws FlowDefinitionConstructionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Refreshing flow definition registry '" + this + "'");
		}
		for (Iterator namespaces = flowDefinitions.entrySet().iterator(); namespaces.hasNext();) {
			Map.Entry namespaceEntry = (Map.Entry) namespaces.next();
			String namespaceName = (String) namespaceEntry.getKey();
			Map namespace = (Map) namespaceEntry.getValue();
			for (Iterator ids = namespace.keySet().iterator(); ids.hasNext();) {
				refresh(FlowPathUtils.buildFlowPath(namespaceName, (String) ids.next()));
			}
		}
	}

	public void refresh(String flowPath) throws NoSuchFlowDefinitionException, FlowDefinitionConstructionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Refreshing flow with path '" + flowPath + "'");
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			// workaround for JMX
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			FlowDefinitionHolder holder = getFlowDefinitionHolder(flowPath);
			holder.refresh();
			if (!holder.getFlowDefinitionId().equals(FlowPathUtils.extractFlowId(flowPath))) {
				reindex(holder, FlowPathUtils.extractFlowNamespace(flowPath), flowPath);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	// implementing FlowDefinitionLocator

	public FlowDefinition getFlowDefinition(String path) throws NoSuchFlowDefinitionException,
			FlowDefinitionConstructionException {
		Assert.hasText(path,
				"Unable to load a flow definition: no flow path was provided.  Please provide a valid flow path.");
		if (logger.isDebugEnabled()) {
			logger.debug("Getting flow definition with path '" + path + "'");
		}
		try {
			return getFlowDefinitionHolder(path).getFlowDefinition();
		} catch (NoSuchFlowDefinitionException e) {
			if (parent != null) {
				// try parent
				return parent.getFlowDefinition(path);
			}
			throw e;
		}
	}

	// implementing FlowDefinitionRegistry

	public void setParent(FlowDefinitionRegistry parent) {
		if (logger.isDebugEnabled()) {
			logger.debug("Setting parent flow definition registry to '" + parent + "'");
		}
		this.parent = parent;
	}

	public void registerFlowDefinition(FlowDefinitionHolder flowHolder) {
		registerFlowDefinition(flowHolder, "");
	}

	public void registerFlowDefinition(FlowDefinitionHolder flowHolder, String namespace) {
		Assert.notNull(flowHolder, "The flow definition holder to register is required");
		Assert.notNull(namespace, "The flow namespace is required");
		if (logger.isDebugEnabled()) {
			logger.debug("Registering flow definition with id '" + flowHolder.getFlowDefinitionId()
					+ "' in namespace '" + namespace + "'");
		}
		index(flowHolder, namespace);
	}

	/**
	 * Remove identified flow definition from this registry. If the given id is not known in this registry, nothing will
	 * happen.
	 * @param flowPath the flow definition path
	 */
	public void removeFlowDefinition(String flowPath) {
		Assert.hasText(flowPath, "The flow path is required");
		if (logger.isDebugEnabled()) {
			logger.debug("Removing flow definition with path '" + flowPath + "'");
		}
		Map namespace = getNamespace(FlowPathUtils.extractFlowNamespace(flowPath));
		namespace.remove(FlowPathUtils.extractFlowId(flowPath));
	}

	// internal helpers

	/**
	 * Re-index given flow definition.
	 * @param holder the holder holding the flow definition to re-index
	 * @param namespace the namespace to index the new flow in
	 * @param oldFlowPath the flowPath that was previously assigned to given flow definition
	 */
	private void reindex(FlowDefinitionHolder holder, String namespace, String oldFlowPath) {
		removeFlowDefinition(oldFlowPath);
		index(holder, namespace);
	}

	/**
	 * Index given flow definition.
	 * @param holder the holder holding the flow definition to index
	 * @param namespaceName the namespace to index the flow definition in
	 */
	private void index(FlowDefinitionHolder holder, String namespaceName) {
		Assert.hasText(holder.getFlowDefinitionId(), "The flow holder to index must return a non-blank flow id");
		Map namespace = getNamespace(namespaceName);
		namespace.put(holder.getFlowDefinitionId(), holder);
	}

	/**
	 * Returns the identified flow definition holder. Throws an exception if it cannot be found.
	 */
	private FlowDefinitionHolder getFlowDefinitionHolder(String flowPath) throws NoSuchFlowDefinitionException {
		Map namespace = getNamespace(FlowPathUtils.extractFlowNamespace(flowPath));
		FlowDefinitionHolder flowHolder = (FlowDefinitionHolder) namespace.get(FlowPathUtils.extractFlowId(flowPath));
		if (flowHolder == null) {
			throw new NoSuchFlowDefinitionException(flowPath, getFlowDefinitionPaths());
		}
		return flowHolder;
	}

	/**
	 * Returns the namespace map for a given namespace. Creates the map if it does not exist.
	 */
	private Map getNamespace(String namespace) {
		if (!flowDefinitions.containsKey(namespace)) {
			flowDefinitions.put(namespace, new TreeMap());
		}
		return (Map) flowDefinitions.get(namespace);
	}

	public String toString() {
		return new ToStringCreator(this).append("flowDefinitions", flowDefinitions).append("parent", parent).toString();
	}
}