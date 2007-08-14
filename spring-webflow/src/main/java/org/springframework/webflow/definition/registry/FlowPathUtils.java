package org.springframework.webflow.definition.registry;

import org.springframework.util.Assert;

/**
 * Simple utility for working with flow paths. Only intended for internal use.
 * 
 * @author Ben Hale
 */
class FlowPathUtils {

	private static final String PATH_DELIMITER = "/";

	/**
	 * Parses a flow path and returns the namespace part of the path.
	 * @param flowPath The path to parse
	 * @return The namespace part of the path
	 */
	public static String extractFlowNamespace(String flowPath) {
		Assert.hasText(flowPath, "The flow path must not be empty or null");
		int index = flowPath.lastIndexOf(PATH_DELIMITER);
		if (index == -1) {
			return "";
		} else {
			return flowPath.substring(0, index);
		}
	}

	/**
	 * Parses a flow path and returns the id part of the path.
	 * @param flowPath The path to parse
	 * @return The id part of the path
	 */
	public static String extractFlowId(String flowPath) {
		Assert.hasText(flowPath, "The flow path must not be empty or null");
		int index = flowPath.lastIndexOf(PATH_DELIMITER);
		if (index == -1) {
			return flowPath;
		} else {
			return flowPath.substring(index + 1);
		}
	}

	/**
	 * Creates a flow path based on given namespace and id.
	 * @param namespace The namespace of the path
	 * @param id The id of the path
	 * @return The complete flow path
	 */
	public static String buildFlowPath(String namespace, String id) {
		Assert.notNull(namespace, "namespace must have some value");
		Assert.hasText(id, "The id must not be empty or null");
		return namespace + PATH_DELIMITER + id;
	}
}
