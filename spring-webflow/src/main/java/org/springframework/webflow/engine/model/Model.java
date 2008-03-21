package org.springframework.webflow.engine.model;

/**
 * Interface defining models. All models must be able to handle merging of their content with an eligible model.
 * 
 * @author Scott Andrews
 */
public interface Model {

	/**
	 * Determine if the model is able to be merged into the current model
	 * @param model the model to compare
	 * @return true if able to merge
	 */
	public boolean isMergeableWith(Model model);

	/**
	 * Merge the model into the current model
	 * @param model the model to merge with
	 */
	public void merge(Model model);

}
