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
package org.springframework.webflow.action;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action implementation that bundles two or more action execution methods into a single class. Action execution methods
 * defined by subclasses must adhere to the following signature:
 * 
 * <pre>
 *     public Event ${method}(RequestContext context) throws Exception;
 * </pre>
 * 
 * When this action is invoked, by default the <code>id</code> of the calling action state state is treated as the
 * action execution method name. Alternatively, the execution method name may be explicitly specified as a attribute of
 * the calling action state.
 * <p>
 * For example, the following action state definition:
 * 
 * <pre>
 *     &lt;action-state id=&quot;search&quot;&gt;
 *         &lt;evaluate expression=&quot;searchAction.executeSearch&quot;/&gt;
 *         &lt;transition on=&quot;success&quot; to=&quot;results&quot;/&gt;
 *     &lt;/action-state&gt;
 * </pre>
 * 
 * ... when entered, executes the method:
 * 
 * <pre>
 * public Event search(RequestContext context) throws Exception;
 * </pre>
 * 
 * <p>
 * One use of the MultiAction is to centralize all command logic for a flow in one place. Another use is to centralize
 * form setup and submit logic in one place, or CRUD (create/read/update/delete) operations for a single domain object
 * in one place.
 * 
 * @see MultiAction.MethodResolver
 * @see org.springframework.webflow.action.DefaultMultiActionMethodResolver
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class MultiAction extends AbstractAction {

	/**
	 * A cache for dispatched action execute methods. The default signature is
	 * <code>public Event ${method}(RequestContext context) throws Exception;</code>.
	 */
	private DispatchMethodInvoker methodInvoker;

	/**
	 * The action method resolver strategy.
	 */
	private MethodResolver methodResolver = new DefaultMultiActionMethodResolver();

	/**
	 * Protected default constructor; not invokable for direct MultiAction instantiation. Intended for use by
	 * subclasses.
	 * <p>
	 * Sets the target to this multi action instance.
	 * @see #setTarget(Object)
	 */
	protected MultiAction() {
		setTarget(this);
	}

	/**
	 * Constructs a multi action that invokes methods on the specified target object. Note: invokable methods on the
	 * target must conform to the multi action method signature:
	 * 
	 * <pre>
	 *       public Event ${method}(RequestContext context) throws Exception;
	 * </pre>
	 * 
	 * @param target the target of this multi action's invocations
	 */
	public MultiAction(Object target) {
		setTarget(target);
	}

	/**
	 * Sets the target of this multi action's invocations.
	 * @param target the target
	 */
	protected final void setTarget(Object target) {
		methodInvoker = new DispatchMethodInvoker(target, new Class[] { RequestContext.class });
	}

	/**
	 * Get the strategy used to resolve action execution method names.
	 */
	public MethodResolver getMethodResolver() {
		return methodResolver;
	}

	/**
	 * Set the strategy used to resolve action execution method names. Allows full control over the method resolution
	 * algorithm. Defaults to {@link DefaultMultiActionMethodResolver}.
	 */
	public void setMethodResolver(MethodResolver methodResolver) {
		this.methodResolver = methodResolver;
	}

	protected final Event doExecute(RequestContext context) throws Exception {
		String method = getMethodResolver().resolveMethod(context);
		Object obj = methodInvoker.invoke(method, new Object[] { context });
		if (obj != null) {
			Assert.isInstanceOf(Event.class, obj, "The '" + method + "' action execution method on target object '"
					+ methodInvoker.getTarget() + "' did not return an Event object but '" + obj + "' of type "
					+ obj.getClass().getName() + " -- "
					+ "Programmer error; make sure the method signature conforms to "
					+ "'public Event ${method}(RequestContext context) throws Exception;'.");
		}
		return (Event) obj;
	}

	/**
	 * Strategy interface used by the MultiAction to map a request context to the name of an action execution method.
	 * 
	 * @author Keith Donald
	 * @author Erwin Vervaet
	 */
	public interface MethodResolver {

		/**
		 * Resolve a method name from given flow execution request context.
		 * @param context the flow execution request context
		 * @return the name of the method that should handle action execution
		 */
		public String resolveMethod(RequestContext context);
	}
}