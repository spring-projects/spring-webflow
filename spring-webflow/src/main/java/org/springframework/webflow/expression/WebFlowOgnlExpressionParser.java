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

import ognl.ObjectPropertyAccessor;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.ognl.OgnlExpressionParser;
import org.springframework.context.MessageSource;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.AnnotatedAction;
import org.springframework.webflow.execution.RequestContext;

/**
 * An extension of {@link OgnlExpressionParser} that registers Web Flow-specific PropertyAccessors.
 * 
 * @author Keith Donald
 */
public class WebFlowOgnlExpressionParser extends OgnlExpressionParser {

	/**
	 * Creates a Web Flow OGNL Expression Parser.
	 */
	public WebFlowOgnlExpressionParser() {
		addPropertyAccessor(MapAdaptable.class, new MapAdaptablePropertyAccessor());
		addPropertyAccessor(MutableAttributeMap.class, new MutableAttributeMapPropertyAccessor());
		addPropertyAccessor(MessageSource.class, new MessageSourcePropertyAccessor());
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

		private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

		private PropertyAccessor delegate;

		public RequestContextPropertyAccessor(PropertyAccessor delegate) {
			this.delegate = delegate;
		}

		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			String property = name.toString();
			RequestContext requestContext = (RequestContext) target;
			if (property.equals("flowRequestContext")) {
				return requestContext;
			}
			if (property.equals("currentUser")) {
				return requestContext.getExternalContext().getCurrentUser();
			}
			if (property.equals("resourceBundle")) {
				return requestContext.getActiveFlow().getApplicationContext();
			}
			if (requestContext.getRequestScope().contains(property)) {
				return requestContext.getRequestScope().get(property);
			} else if (requestContext.getFlashScope().contains(property)) {
				return requestContext.getFlashScope().get(property);
			} else if (requestContext.inViewState() && requestContext.getViewScope().contains(property)) {
				return requestContext.getViewScope().get(property);
			} else if (requestContext.getFlowScope().contains(property)) {
				return requestContext.getFlowScope().get(property);
			} else if (requestContext.getConversationScope().contains(property)) {
				return requestContext.getConversationScope().get(property);
			}
			BeanFactory bf = getBeanFactory(requestContext);
			if (bf.containsBean(property)) {
				return bf.getBean(property);
			}
			return delegate.getProperty(context, target, name);
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			String property = name.toString();
			RequestContext requestContext = (RequestContext) target;
			if (property.equals("flowRequestContext")) {
				throw new OgnlException("The 'flowRequestContext' variable is not writeable");
			}
			if (property.equals("currentUser")) {
				throw new OgnlException("The 'currentUser' variable is not writeable");
			}
			if (property.equals("resourceBundle")) {
				throw new OgnlException("The 'resourceBundle' variable is not writeable");
			}
			if (requestContext.getRequestScope().contains(property)) {
				requestContext.getRequestScope().put(property, value);
			} else if (requestContext.getFlashScope().contains(property)) {
				requestContext.getFlashScope().put(property, value);
			} else if (requestContext.inViewState() && requestContext.getViewScope().contains(property)) {
				requestContext.getViewScope().put(property, value);
			} else if (requestContext.getFlowScope().contains(property)) {
				requestContext.getFlowScope().put(property, value);
			} else if (requestContext.getConversationScope().contains(property)) {
				requestContext.getConversationScope().put(property, value);
			} else {
				delegate.setProperty(context, target, name, value);
			}
		}

		private BeanFactory getBeanFactory(RequestContext requestContext) {
			BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
			return beanFactory != null ? beanFactory : EMPTY_BEAN_FACTORY;
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

	/**
	 * Resolves messages.
	 */
	private static class MessageSourcePropertyAccessor implements PropertyAccessor {
		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			MessageSource messageSource = (MessageSource) target;
			ExternalContext externalContext;
			Object root = Ognl.getRoot(context);
			if (root instanceof RequestContext) {
				externalContext = ((RequestContext) root).getExternalContext();
			} else {
				externalContext = ExternalContextHolder.getExternalContext();
			}
			if (externalContext != null) {
				return messageSource.getMessage(name.toString(), null, null, externalContext.getLocale());
			} else {
				return messageSource.getMessage(name.toString(), null, null, null);
			}
		}

		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			throw new OgnlException("Cannot set properties on a MessageSource instance - operation not allowed");
		}
	}

}