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
package org.springframework.webflow.core;

import java.util.Map;

import ognl.OgnlException;
import ognl.PropertyAccessor;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.support.OgnlExpressionParser;
import org.springframework.webflow.core.collection.MutableAttributeMap;

/**
 * An extension of {@link OgnlExpressionParser} that registers web flow specific
 * property accessors.
 * 
 * @author Keith Donald
 */
class WebFlowOgnlExpressionParser extends OgnlExpressionParser {

	/**
	 * Creates a webflow-specific ognl expression parser.
	 */
	public WebFlowOgnlExpressionParser() {
		addPropertyAccessor(MapAdaptable.class, new MapAdaptablePropertyAccessor());
		addPropertyAccessor(MutableAttributeMap.class, new MutableAttributeMapPropertyAccessor());
	}

	/**
	 * The {@link MapAdaptable} property accessor.
	 * 
	 * @author Keith Donald
	 */
	private static class MapAdaptablePropertyAccessor implements PropertyAccessor {
		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			return ((MapAdaptable)target).asMap().get(name);
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			throw new UnsupportedOperationException(
					"Cannot mutate immutable attribute collections; operation disallowed");
		}
	}

	/**
	 * The {@link MutableAttributeMap} property accessor.
	 * 
	 * @author Keith Donald
	 */
	private static class MutableAttributeMapPropertyAccessor extends MapAdaptablePropertyAccessor {
		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			((MutableAttributeMap)target).put((String)name, value);
		}
	}
}