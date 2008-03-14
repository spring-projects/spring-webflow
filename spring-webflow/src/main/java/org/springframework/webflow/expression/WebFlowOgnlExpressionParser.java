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
package org.springframework.webflow.expression;

import java.util.Map;

import javax.el.PropertyNotWritableException;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.ognl.OgnlExpressionParser;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContext;

/**
 * An extension of {@link OgnlExpressionParser} that registers Web Flow-specific PropertyAccessors.
 * 
 * @author Keith Donald
 */
public class WebFlowOgnlExpressionParser extends OgnlExpressionParser {

	private static final Log logger = LogFactory.getLog(WebFlowOgnlExpressionParser.class);

	/**
	 * Creates a Web Flow OGNL Expression Parser.
	 */
	public WebFlowOgnlExpressionParser() {
		addPropertyAccessor(MapAdaptable.class, new MapAdaptablePropertyAccessor());
		addPropertyAccessor(MutableAttributeMap.class, new MutableAttributeMapPropertyAccessor());
		addPropertyAccessor(RequestContext.class, new RequestContextPropertyAccessor(new ObjectPropertyAccessor()));
		addPropertyAccessor(Action.class, new ActionPropertyAccessor());
	}

	/**
	 * Resolves Map Adaptable properties.
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
	 * Resolves Mutable Attribute Map properties, also capable of setting properties.
	 */
	private static class MutableAttributeMapPropertyAccessor extends MapAdaptablePropertyAccessor {
		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			((MutableAttributeMap) target).put((String) name, value);
		}
	}

	/**
	 * Resolves RequestContext properties. Supports several implicit variables and scope searching routines.
	 */
	private static class RequestContextPropertyAccessor implements PropertyAccessor {

		private static boolean securityPresent = ClassUtils
				.isPresent("org.springframework.security.context.SecurityContextHolder");

		private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

		private PropertyAccessor delegate;

		public RequestContextPropertyAccessor(PropertyAccessor delegate) {
			this.delegate = delegate;
		}

		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			String property = name.toString();
			RequestContext requestContext = (RequestContext) target;
			if (property.equals("flowRequestContext")) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved the current RequestContext under variable '" + property + "'");
				}
				return requestContext;
			}
			if (property.equals("currentUser")) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved implicit flow variable '" + property + "'");
				}
				return requestContext.getExternalContext().getCurrentUser();
			}
			if (requestContext.getRequestScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved request scoped variable '" + property + "'");
				}
				return requestContext.getRequestScope().get(property);
			} else if (requestContext.getFlashScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved flash scoped variable '" + property + "'");
				}
				return requestContext.getFlashScope().get(property);
			} else if (requestContext.getFlowScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved flow scoped variable '" + property + "'");
				}
				return requestContext.getFlowScope().get(property);
			} else if (requestContext.getConversationScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved conversation scoped variable '" + property + "'");
				}
				return requestContext.getConversationScope().get(property);
			}
			BeanFactory bf = getBeanFactory(requestContext);
			if (bf.containsBean(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved variable '" + property + "' in Spring BeanFactory");
				}
				return bf.getBean(property);
			}
			return delegate.getProperty(context, target, name);
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			String property = name.toString();
			RequestContext requestContext = (RequestContext) target;
			if (property.equals("flowRequestContext")) {
				throw new PropertyNotWritableException("The 'flowRequestContext' variable is not writeable");
			}
			if (securityPresent && property.equals("currentUser")) {
				throw new PropertyNotWritableException("The 'currentUser' variable is not writeable");
			}
			if (requestContext.getRequestScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved request scoped variable '" + property + "'");
				}
				requestContext.getRequestScope().put(property, value);
			} else if (requestContext.getFlashScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved flash scoped variable '" + property + "'");
				}
				requestContext.getFlashScope().put(property, value);
			} else if (requestContext.getFlowScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved flow scoped variable '" + property + "'");
				}
				requestContext.getFlowScope().put(property, value);
			} else if (requestContext.getConversationScope().contains(property)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully resolved conversation scoped variable '" + property + "'");
				}
				requestContext.getConversationScope().put(property, value);
			} else {
				delegate.setProperty(context, target, name, value);
			}
		}

		private BeanFactory getBeanFactory(RequestContext requestContext) {
			if (requestContext.getActiveFlow().getBeanFactory() != null) {
				BeanFactory factory = requestContext.getActiveFlow().getBeanFactory();
				return factory;
			} else {
				return EMPTY_BEAN_FACTORY;
			}
		}
	}

	/**
	 * Resolves multi action methods.
	 */
	private static class ActionPropertyAccessor implements PropertyAccessor {
		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			Action action = (Action) target;
			AnnotatedAction annotated = new AnnotatedAction(action);
			annotated.setMethod(name.toString());
			return annotated;
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			throw new OgnlException("Cannot set properties on a Action instance - operation not allowed");
		}
	}

}