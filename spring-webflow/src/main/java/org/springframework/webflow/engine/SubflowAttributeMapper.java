package org.springframework.webflow.engine;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy interface used by a subflow state to map subflow input and output attributes.
 * @author Keith Donald
 */
public interface SubflowAttributeMapper {

	/**
	 * Create a map of attributes that should be passed as <i>input</i> to a subflow.
	 * @param context the current request execution context
	 * @return a map of attributes to pass as input
	 */
	public MutableAttributeMap createSubflowInput(RequestContext context);

	/**
	 * Map output attributes of an ended subflow flow to the resuming parent flow.
	 * @param output the output attributes returned by the ended subflow
	 * @param context the current request execution context, which gives access to the parent flow scope
	 */
	public void mapSubflowOutput(AttributeMap output, RequestContext context);
}