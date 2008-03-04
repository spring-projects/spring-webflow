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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.convert.support.RuntimeBindingConversionExecutor;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.CollectionAddingExpression;
import org.springframework.util.Assert;

/**
 * A stateful builder that builds {@link Mapping} objects. Designed for convenience to build mappings in a clear,
 * readable manner.
 * <p>
 * Example usage:
 * 
 * <pre>
 * MappingBuilder mapping = new MappingBuilder();
 * Mapping result = mapping.source(&quot;foo&quot;).target(&quot;bar&quot;).from(String.class).to(Long.class).value();
 * </pre>
 * 
 * Calling the {@link #value()} result method clears out this builder's state so it can be reused to build another
 * mapping.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class MappingBuilder {

	/**
	 * The expression string parser.
	 */
	private ExpressionParser expressionParser;

	/**
	 * The conversion service for applying type conversions.
	 */
	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * The source mapping expression.
	 */
	private Expression sourceExpression;

	/**
	 * The target mapping settable expression.
	 */
	private Expression targetExpression;

	/**
	 * The type of the object returned by evaluating the source expression.
	 */
	private Class sourceType;

	/**
	 * The type of the property settable by the target expression.
	 */
	private Class targetType;

	/**
	 * Whether or not the built mapping is a required mapping.
	 */
	private boolean required;

	/**
	 * Creates a mapping builder that uses the expression parser to parse attribute mapping expressions.
	 * @param expressionParser the expression parser
	 */
	public MappingBuilder(ExpressionParser expressionParser) {
		Assert.notNull(expressionParser, "The expression parser is required");
		this.expressionParser = expressionParser;
	}

	/**
	 * Sets the conversion service that will convert the object returned by evaluating the source expression to the
	 * {@link #to(Class)} type if necessary.
	 * @param conversionService the conversion service
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Sets the source expression of the mapping built by this builder.
	 * @param expressionString the expression string
	 * @return this, to support call-chaining
	 */
	public MappingBuilder source(String expressionString) {
		sourceExpression = expressionParser.parseExpression(expressionString, null);
		return this;
	}

	/**
	 * Sets the target property expression of the mapping built by this builder.
	 * @param expressionString the expression string
	 * @return this, to support call-chaining
	 */
	public MappingBuilder target(String expressionString) {
		targetExpression = expressionParser.parseExpression(expressionString, null);
		return this;
	}

	/**
	 * Sets the target collection of the mapping built by this builder.
	 * @param expressionString the expression string, resolving a collection
	 * @return this, to support call-chaining
	 */
	public MappingBuilder targetCollection(String expressionString) {
		targetExpression = new CollectionAddingExpression(expressionParser.parseExpression(expressionString, null));
		return this;
	}

	/**
	 * Sets the expected type of the object returned by evaluating the source expression. Used in conjunction with
	 * {@link #to(Class)} to perform a type conversion during the mapping process.
	 * @param sourceType the source type
	 * @return this, to support call-chaining
	 */
	public MappingBuilder from(Class sourceType) {
		this.sourceType = sourceType;
		return this;
	}

	/**
	 * Sets the target type of the property writeable by the target expression.
	 * @param targetType the target type
	 * @return this, to support call-chaining
	 */
	public MappingBuilder to(Class targetType) {
		this.targetType = targetType;
		return this;
	}

	/**
	 * Marks the mapping to be built a "required" mapping.
	 * @return this, to support call-chaining
	 */
	public MappingBuilder required() {
		this.required = true;
		return this;
	}

	/**
	 * The logical GoF builder getResult method, returning a fully constructed Mapping from the configured pieces. Once
	 * called, the state of this builder is nulled out to support building a new mapping object again.
	 * @return the mapping result
	 */
	public Mapping value() {
		Assert.notNull(sourceExpression, "The source expression must be set at a minimum");
		if (targetExpression == null) {
			targetExpression = sourceExpression;
		}
		ConversionExecutor typeConverter = null;
		if (targetType != null) {
			if (sourceType != null) {
				typeConverter = conversionService.getConversionExecutor(sourceType, targetType);
			} else {
				typeConverter = new RuntimeBindingConversionExecutor(targetType, conversionService);
			}
		}
		Mapping result = new Mapping(sourceExpression, targetExpression, typeConverter, required);
		reset();
		return result;
	}

	/**
	 * Reset this mapping builder.
	 */
	public void reset() {
		sourceExpression = null;
		targetExpression = null;
		sourceType = null;
		targetType = null;
		required = false;
	}
}