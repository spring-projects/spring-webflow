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
package org.springframework.binding.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.results.RequiredError;
import org.springframework.binding.mapping.results.SourceAccessError;
import org.springframework.binding.mapping.results.Success;
import org.springframework.binding.mapping.results.TargetAccessError;
import org.springframework.binding.mapping.results.TypeConversionError;

/**
 * Default mapping context implementation.
 * @author Keith Donald
 */
public class DefaultMappingContext {

	private static final Log logger = LogFactory.getLog(DefaultMapping.class);

	private Object source;

	private Object target;

	private Mapping currentMapping;

	private List mappingResults;

	public DefaultMappingContext(Object source, Object target) {
		this.source = source;
		this.target = target;
		this.mappingResults = new ArrayList();
	}

	/**
	 * The object being mapped from.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * The object being mapped to.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Returns the current mapping.
	 * @return the current mapping
	 */
	public Mapping getCurrentMapping() {
		return currentMapping;
	}

	/**
	 * Sets the current mapping. Called when a single mapping operation is about to begin. This updates progress of the
	 * overall mapping transaction.
	 * @param mapping the mapping to make the current mapping
	 */
	public void setCurrentMapping(Mapping mapping) {
		if (currentMapping != null) {
			throw new IllegalStateException("The current mapping has not finished yet");
		}
		currentMapping = mapping;
	}

	/**
	 * Indicates the current mapping completed successfully.
	 * @param originalValue the original value from the source of the mapping
	 * @param mappedValue the successfully mapped value, which may be different from the original if a type conversion
	 * was performed
	 */
	public void setSuccessResult(Object originalValue, Object mappedValue) {
		add(new MappingResult(currentMapping, new Success(mappedValue, originalValue)));
	}

	/**
	 * Indicates the current mapping ended with a 'required' error. This means the value obtained from the source was
	 * empty, and the mapping could not be completed as a result.
	 * @param originalValue the original source value that is empty (null or an empty string, typically)
	 */
	public void setRequiredErrorResult(Object originalValue) {
		add(new MappingResult(currentMapping, new RequiredError(originalValue)));
	}

	/**
	 * Indicates the current mapping ended with a 'type conversion' error. This means the value obtained from the source
	 * could not be converted to a type that could be assigned to the target expression.
	 * @param exception the conversion exception that occurred, containing the original source value that could not be
	 * converted during the mapping attempt, as well as the desired target type to which conversion could not be
	 * performed
	 */
	public void setTypeConversionErrorResult(ConversionExecutionException exception) {
		add(new MappingResult(currentMapping, new TypeConversionError(exception)));
	}

	/**
	 * Indicates a error occurred accessing the source mapping expression.
	 * @param error the error that occurred
	 */
	public void setSourceAccessError(EvaluationException error) {
		add(new MappingResult(currentMapping, new SourceAccessError(error)));
	}

	/**
	 * Indicates a error occurred accessing the target mapping expression.
	 * @param error the error that occurred
	 */
	public void setTargetAccessError(Object originalValue, EvaluationException error) {
		add(new MappingResult(currentMapping, new TargetAccessError(originalValue, error)));
	}

	/**
	 * Returns the mapping results recorded in this context.
	 * @return the mapping results
	 */
	public MappingResults getMappingResults() {
		return new DefaultMappingResults(source, target, mappingResults);
	}

	// internal helpers

	private void add(MappingResult result) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding " + result);
		}
		mappingResults.add(result);
		currentMapping = null;
	}

}
