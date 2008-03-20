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
package org.springframework.binding.mapping.impl;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.mapping.Mapping;

/**
 * A context for a mapping transaction.
 * 
 * @author Keith Donald
 */
public interface MappingContext {

	/**
	 * The object being mapped from.
	 */
	public Object getSource();

	/**
	 * The object being mapped to.
	 */
	public Object getTarget();

	/**
	 * Returns the conversion service that can be used to perform type conversions during the mapping process. May be
	 * null if no externally managed conversion service is provided.
	 */
	public ConversionService getConversionService();

	/**
	 * Returns the current mapping.
	 * @return the current mapping
	 */
	public Mapping getCurrentMapping();

	/**
	 * Sets the current mapping. Called when a single mapping operation is about to begin. This updates progress of the
	 * overall mapping transaction.
	 * @param mapping the mapping to make the current mapping
	 */
	public void setCurrentMapping(Mapping mapping);

	/**
	 * Indicates the current mapping completed successfully.
	 * @param originalValue the original value from the source of the mapping
	 * @param mappedValue the successfully mapped value, which may be different from the original if a type conversion
	 * was performed
	 */
	public void setSuccessResult(Object originalValue, Object mappedValue);

	/**
	 * Indicates the current mapping ended with a 'required' error. This means the value obtained from the source was
	 * empty, and the mapping could not be completed as a result.
	 * @param originalValue the original source value that is empty (null or an empty string, typically)
	 */
	public void setRequiredErrorResult(Object originalValue);

	/**
	 * Indicates the current mapping ended with a 'type conversion' error. This means the value obtained from the source
	 * could not be converted to a type that could be assigned to the target expression.
	 * @param originalValue the original source value that could not be converted during the mapping attempt
	 * @param targetType the desired target type to which conversion could not be performed
	 */
	public void setTypeConversionErrorResult(Object originalValue, Class targetType);

	/**
	 * Indicates a error occurred accessing the source mapping expression.
	 * @param error the error that occurred
	 */
	public void setSourceAccessError(EvaluationException error);

	/**
	 * Indicates a error occurred accessing the target mapping expression.
	 * @param error the error that occurred
	 */
	public void setTargetAccessError(Object originalValue, EvaluationException error);

}
