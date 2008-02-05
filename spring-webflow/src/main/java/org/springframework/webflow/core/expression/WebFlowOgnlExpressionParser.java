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
package org.springframework.webflow.core.expression;

import java.util.Map;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.ognl.OgnlExpressionParser;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * An extension of {@link OgnlExpressionParser} that registers web flow specific property accessors.
 * 
 * @author Keith Donald
 */
public class WebFlowOgnlExpressionParser extends OgnlExpressionParser {

	/**
	 * Creates a webflow-specific ognl expression parser.
	 */
	public WebFlowOgnlExpressionParser() {
		addPropertyAccessor(RequestContext.class, new RequestContextPropertyAccessor(new ObjectPropertyAccessor()));
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
			return ((MapAdaptable) target).asMap().get(name);
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
			((MutableAttributeMap) target).put((String) name, value);
		}
	}

	private static class RequestContextPropertyAccessor implements PropertyAccessor {
		private PropertyAccessor delegate;

		public RequestContextPropertyAccessor(PropertyAccessor delegate) {
			this.delegate = delegate;
		}

		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			String property = name.toString();
			RequestContext requestContext = (RequestContext) target;
			if (requestContext.getRequestScope().contains(property)) {
				return requestContext.getRequestScope().get(property);
			} else if (requestContext.getFlashScope().contains(property)) {
				return requestContext.getFlashScope().get(property);
			} else if (requestContext.getFlowScope().contains(property)) {
				return requestContext.getFlowScope().get(property);
			} else if (requestContext.getConversationScope().contains(property)) {
				return requestContext.getConversationScope().get(property);
			} else {
				return delegate.getProperty(context, target, name);
			}
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			delegate.setProperty(context, target, name, value);
		}

	}
}