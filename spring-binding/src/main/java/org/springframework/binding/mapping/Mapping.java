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
package org.springframework.binding.mapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * A single mapping definition, encapsulating the information necessary to map the result of evaluating an expression on
 * a source object to a property on a target object, optionally applying a type conversion during the mapping process.
 * 
 * @author Keith Donald
 */
public class Mapping implements AttributeMapper {

	private static final Log logger = LogFactory.getLog(Mapping.class);

	/**
	 * The source expression to evaluate against a source object to map from.
	 */
	private final Expression sourceExpression;

	/**
	 * The target expression to set on a target object to map to.
	 */
	private final Expression targetExpression;

	/**
	 * A type converter to apply during the mapping process.
	 */
	private final ConversionExecutor typeConverter;

	/**
	 * Whether or not this is a required mapping; if true, the source expression must return a non-null value.
	 */
	private boolean required;

	/**
	 * Creates a new mapping.
	 * @param sourceExpression the source expression
	 * @param targetExpression the target expression
	 * @param typeConverter a type converter
	 */
	public Mapping(Expression sourceExpression, Expression targetExpression, ConversionExecutor typeConverter) {
		this(sourceExpression, targetExpression, typeConverter, false);
	}

	/**
	 * Creates a new mapping.
	 * @param sourceExpression the source expression
	 * @param targetExpression the target expression
	 * @param typeConverter a type converter
	 * @param required whether or not this mapping is required
	 */
	public Mapping(Expression sourceExpression, Expression targetExpression, ConversionExecutor typeConverter,
			boolean required) {
		Assert.notNull(sourceExpression, "The source expression is required");
		Assert.notNull(targetExpression, "The target expression is required");
		this.sourceExpression = sourceExpression;
		this.targetExpression = targetExpression;
		this.typeConverter = typeConverter;
		this.required = required;
	}

	/**
	 * Map the <code>sourceAttribute</code> in to the <code>targetAttribute</code> target map, performing type
	 * conversion if necessary.
	 * @param source The source data structure
	 * @param target The target data structure
	 */
	public void map(Object source, Object target, MappingContext context) {
		// get source value
		Object sourceValue = sourceExpression.getValue(source);
		if (sourceValue == null) {
			if (required) {
				throw new RequiredMappingException("This mapping is required; evaluation of expression '"
						+ sourceExpression + "' against source of type [" + source.getClass()
						+ "] must return a non-null value");
			} else {
				// source expression returned no value, simply abort mapping
				return;
			}
		}
		Object targetValue = sourceValue;
		if (typeConverter != null) {
			targetValue = typeConverter.execute(sourceValue);
		}
		// set target value
		if (logger.isDebugEnabled()) {
			logger.debug("Mapping '" + sourceExpression + "' value [" + sourceValue + "] to target property '"
					+ targetExpression + "'; setting property value to [" + targetValue + "]");
		}
		targetExpression.setValue(target, targetValue);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Mapping)) {
			return false;
		}
		Mapping other = (Mapping) o;
		return sourceExpression.equals(other.sourceExpression) && targetExpression.equals(other.targetExpression);
	}

	public int hashCode() {
		return sourceExpression.hashCode() + targetExpression.hashCode();
	}

	public String toString() {
		return new ToStringCreator(this).append(sourceExpression + " -> " + targetExpression).toString();
	}
}