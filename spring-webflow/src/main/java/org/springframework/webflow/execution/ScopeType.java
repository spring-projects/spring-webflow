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
package org.springframework.webflow.execution;

import org.springframework.core.enums.StaticLabeledEnum;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;

/**
 * An enumeration of the core scope types of Spring Web Flow. Provides easy access to each scope by <i>type</i> using
 * {@link #getScope(RequestContext)}.
 * <p>
 * A "scope" defines a data structure for storing custom user attributes within a flow execution. Different scope types
 * have different semantics in terms of how long attributes placed in those scope maps remain valid.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class ScopeType extends StaticLabeledEnum {

	/**
	 * The "request" scope type. Attributes placed in request scope exist for the life of the current request into the
	 * flow execution. When the request ends any attributes in request scope go out of scope.
	 */
	public static final ScopeType REQUEST = new ScopeType(0, "Request") {
		public MutableAttributeMap getScope(RequestContext context) {
			return context.getRequestScope();
		}
	};

	/**
	 * The "flash" scope type. Attributes placed in flash scope exist through the life of the current request <i>and
	 * until the next view rendering</i>. After the view renders, flash scope is cleared.
	 * <p>
	 * Flash scope is typically used to store messages that should be preserved until after the next view renders.
	 */
	public static final ScopeType FLASH = new ScopeType(1, "Flash") {
		public MutableAttributeMap getScope(RequestContext context) {
			return context.getFlashScope();
		}
	};

	/**
	 * The "view" scope type. Attributes placed in view scope exist through the life of the current view state <i>and
	 * until the view state exits in a subsequent request</i>.
	 * <p>
	 * View scope is typically used to store view model objects manipulated over a series of Ajax requests.
	 */
	public static final ScopeType VIEW = new ScopeType(1, "View") {
		public MutableAttributeMap getScope(RequestContext context) {
			return context.getViewScope();
		}
	};
	/**
	 * The "flow" scope type. Attributes placed in flow scope exist through the life of an executing flow session,
	 * representing an instance a single {@link FlowDefinition flow definition}. When the flow session ends any data in
	 * flow scope goes out of scope.
	 */
	public static final ScopeType FLOW = new ScopeType(2, "Flow") {
		public MutableAttributeMap getScope(RequestContext context) {
			return context.getFlowScope();
		}
	};

	/**
	 * The "conversation" scope type. Attributes placed in conversation scope are shared by all flow sessions started
	 * within a flow execution and live for the life of the entire flow execution (representing a single logical user
	 * conversation). When the governing execution ends, any data in conversation scope goes out of scope.
	 */
	public static final ScopeType CONVERSATION = new ScopeType(3, "Conversation") {
		public MutableAttributeMap getScope(RequestContext context) {
			return context.getConversationScope();
		}
	};

	/**
	 * Private constructor because this is a typesafe enum!
	 */
	private ScopeType(int code, String label) {
		super(code, label);
	}

	public Class getType() {
		// force ScopeType as type
		return ScopeType.class;
	}

	/**
	 * Accessor that returns the mutable attribute map for this scope type for a given flow execution request context.
	 * @param context the context representing an executing request
	 * @return the scope map of this type for that request, allowing attributes to be accessed and set
	 */
	public abstract MutableAttributeMap getScope(RequestContext context);
}