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
package org.springframework.webflow.action;

import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.MappingContext;
import org.springframework.binding.mapping.MappingContextImpl;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action that executes an attribute mapper to map information in the request context. Both the source and the target of
 * the mapping will be the request context. This allows for maximum flexibility when defining attribute mapping
 * expressions (e.g. "${flowScope.someAttribute}").
 * <p>
 * This action always returns the {@link org.springframework.webflow.action.AbstractAction#success() success} event. If
 * something goes wrong while executing the mapping, an exception is thrown.
 * 
 * @see org.springframework.binding.mapping.AttributeMapper
 * @see org.springframework.webflow.execution.RequestContext
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class AttributeMapperAction extends AbstractAction {

	/**
	 * The attribute mapper strategy to delegate to perform the mapping.
	 */
	private AttributeMapper attributeMapper;

	/**
	 * Creates a new attribute mapper action that delegates to the configured attribute mapper to complete the mapping
	 * process.
	 * @param attributeMapper the mapper
	 */
	public AttributeMapperAction(AttributeMapper attributeMapper) {
		Assert.notNull(attributeMapper, "The attribute mapper is required");
		this.attributeMapper = attributeMapper;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		// map attributes from and to the request context
		attributeMapper.map(context, context, getMappingContext(context));
		return success();
	}

	/**
	 * Returns a context containing extra data available during attribute mapping. The default implementation just
	 * returns null. Subclasses can override this if necessary.
	 */
	protected MappingContext getMappingContext(RequestContext context) {
		return new MappingContextImpl(context.getMessageContext());
	}
}