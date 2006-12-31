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
package org.springframework.webflow.engine.support;

import java.io.Serializable;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.execution.ScopeType;

/**
 * Generic flow attribute mapper implementation that allows mappings to be
 * configured in a declarative fashion.
 * <p>
 * Two types of mappings may be configured, input mappings and output mappings:
 * <ol>
 * <li>Input mappings define the rules for mapping attributes in a parent flow
 * to a spawning subflow.
 * <li>Output mappings define the rules for mapping attributes returned from an
 * ended subflow into the resuming parent.
 * </ol>
 * <p>
 * The mappings defined using the configuration properties fully support bean
 * property access. So an entry name in a mapping can either be "beanName" or
 * "beanName.propName". Nested property values are also supported
 * ("beanName.propName.nestedPropName"). When the <i>from</i> mapping string is
 * enclosed in "${...}", it will be interpreted as an expression that will be
 * evaluated against the flow execution request context.
 * 
 * @see org.springframework.webflow.execution.RequestContext
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 * @author Colin Sampaleanu
 */
public class ConfigurableFlowAttributeMapper extends AbstractFlowAttributeMapper implements Serializable {

	/**
	 * The expression parser that will parse input and output attribute
	 * expressions.
	 */
	private ExpressionParser expressionParser = DefaultExpressionParserFactory.getExpressionParser();

	/**
	 * The mapper that maps attributes into a spawning subflow.
	 */
	private DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();

	/**
	 * The mapper that maps attributes returned by an ended subflow.
	 */
	private DefaultAttributeMapper outputMapper = new DefaultAttributeMapper();

	/**
	 * Set the expression parser responsible for parsing expression strings into
	 * evaluatable expression objects.
	 */
	public void setExpressionParser(ExpressionParser expressionParser) {
		Assert.notNull(expressionParser, "The expression parser is required");
		this.expressionParser = expressionParser;
	}

	/**
	 * Adds a new input mapping. Use when you need full control over defining
	 * how a subflow input attribute mapping will be perfomed.
	 * @param inputMapping the input mapping
	 * @return this, to support call chaining
	 */
	public ConfigurableFlowAttributeMapper addInputMapping(AttributeMapper inputMapping) {
		inputMapper.addMapping(inputMapping);
		return this;
	}

	/**
	 * Adds a collection of input mappings. Use when you need full control over
	 * defining how a subflow input attribute mapping will be perfomed.
	 * @param inputMappings the input mappings
	 */
	public void addInputMappings(AttributeMapper[] inputMappings) {
		inputMapper.addMappings(inputMappings);
	}

	/**
	 * Adds a new output mapping. Use when you need full control over defining
	 * how a subflow output attribute mapping will be perfomed.
	 * @param outputMapping the output mapping
	 * @return this, to support call chaining
	 */
	public ConfigurableFlowAttributeMapper addOutputMapping(AttributeMapper outputMapping) {
		outputMapper.addMapping(outputMapping);
		return this;
	}

	/**
	 * Adds a collection of output mappings. Use when you need full control over
	 * defining how a subflow output attribute mapping will be perfomed.
	 * @param outputMappings the output mappings
	 */
	public void addOutputMappings(AttributeMapper[] outputMappings) {
		outputMapper.addMappings(outputMappings);
	}

	/**
	 * Adds an input mapping that maps a single attribute in parent <i>flow
	 * scope</i> into the subflow input map. For instance: "x" will result in
	 * the "x" attribute in parent flow scope being mapped into the subflow
	 * input map as "x".
	 * @param attributeName the attribute in flow scope to map into the subflow
	 * @return this, to support call chaining
	 */
	public ConfigurableFlowAttributeMapper addInputAttribute(String attributeName) {
		SettableExpression attribute = expressionParser.parseSettableExpression(attributeName);
		Expression scope = new AttributeExpression(attribute, ScopeType.FLOW);
		addInputMapping(new Mapping(scope, attribute, null));
		return this;
	}

	/**
	 * Adds a collection of input mappings that map attributes in parent <i>flow
	 * scope</i> into the subflow input map. For instance: "x" will result in
	 * the "x" attribute in parent flow scope being mapped into the subflow
	 * input map as "x".
	 * @param attributeNames the attributes in flow scope to map into the
	 * subflow
	 */
	public void addInputAttributes(String[] attributeNames) {
		if (attributeNames == null) {
			return;
		}
		for (int i = 0; i < attributeNames.length; i++) {
			addInputAttribute(attributeNames[i]);
		}
	}

	/**
	 * Adds an output mapping that maps a single subflow output attribute into
	 * the <i>flow scope</i> of the resuming parent flow. For instance: "y"
	 * will result in the "y" attribute of the subflow output map being mapped
	 * into the flowscope of the resuming parent flow as "y".
	 * @param attributeName the subflow output attribute to map into the parent
	 * flow scope
	 * @return this, to support call chaining
	 */
	public ConfigurableFlowAttributeMapper addOutputAttribute(String attributeName) {
		Expression attribute = expressionParser.parseExpression(attributeName);
		SettableExpression scope = new AttributeExpression(attribute, ScopeType.FLOW);
		addOutputMapping(new Mapping(attribute, scope, null));
		return this;
	}

	/**
	 * Adds a collection of output mappings that map subflow output attributes
	 * into the scope of the resuming parent flow. For instance: "y" will result
	 * in the "y" attribute of the subflow output map being mapped into the
	 * flowscope of the resuming parent flow as "y".
	 * @param attributeNames the subflow output attributes to map into the
	 * parent flow
	 */
	public void addOutputAttributes(String[] attributeNames) {
		if (attributeNames == null) {
			return;
		}
		for (int i = 0; i < attributeNames.length; i++) {
			addOutputAttribute(attributeNames[i]);
		}
	}

	/**
	 * Returns a typed-array of configured input mappings.
	 * @return the configured input mappings
	 */
	public AttributeMapper[] getInputMappings() {
		return inputMapper.getMappings();
	}

	/**
	 * Returns a typed-array of configured output mappings.
	 * @return the configured output mappings
	 */
	public AttributeMapper[] getOutputMappings() {
		return outputMapper.getMappings();
	}

	/**
	 * Returns the configured expression parser. Can be used by subclasses that
	 * build mappings.
	 */
	protected ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	protected AttributeMapper getInputMapper() {
		return inputMapper;
	}

	protected AttributeMapper getOutputMapper() {
		return outputMapper;
	}

	public String toString() {
		return new ToStringCreator(this).append("inputMapper", inputMapper).append("outputMapper", outputMapper)
				.toString();
	}
}