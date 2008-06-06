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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.core.style.StylerUtils;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * Multi-action that implements common logic dealing with input forms. This class uses the Spring Web data binding code
 * to do binding and validation.
 * <p>
 * Several action execution methods are provided:
 * <ul>
 * <li> {@link #setupForm(RequestContext)} - Prepares the form object for display on a form,
 * {@link #createFormObject(RequestContext) creating it} and an associated {@link Errors errors} if necessary, then
 * caching them in the configured {@link #getFormObjectScope() form object scope} and
 * {@link #getFormErrorsScope() form errors scope}, respectively. Also
 * {@link #registerPropertyEditors(PropertyEditorRegistry) installs} any custom property editors for formatting form
 * object field values. This action method will return the "success" event unless an exception is thrown. </li>
 * <li> {@link #bindAndValidate(RequestContext)} - Binds all incoming request parameters to the form object and then
 * validates the form object using a {@link #setValidator(Validator) registered validator}. This action method will
 * return the "success" event if there are no binding or validation errors, otherwise it will return the "error" event.
 * </li>
 * <li> {@link #bind(RequestContext)} - Binds all incoming request parameters to the form object. No additional
 * validation is performed. This action method will return the "success" event if there are no binding errors, otherwise
 * it will return the "error" event. </li>
 * <li> {@link #validate(RequestContext)} - Validates the form object using a registered validator. No data binding is
 * performed. This action method will return the "success" event if there are no validation errors, otherwise it will
 * return the "error" event. </li>
 * <li> {@link #resetForm(RequestContext)} - Resets the form by reloading the backing form object and reinstalling any
 * custom property editors. Returns "success" on completion, an exception is thrown when a failure occurs. </li>
 * </ul>
 * <p>
 * Since this is a multi-action a subclass could add any number of additional action execution methods, e.g.
 * "setupReferenceData(RequestContext)", or "processSubmit(RequestContext)".
 * <p>
 * Using this action, it becomes very easy to implement form preparation and submission logic in your flow. One way to
 * do this follows:
 * <ol>
 * <li> Create a view state to display the form. In a render action of that state, invoke
 * {@link #setupForm(RequestContext) setupForm} to prepare the new form for display. </li>
 * <li> On a matching "submit" transition execute an action that invokes
 * {@link #bindAndValidate(RequestContext) bindAndValidate} to bind incoming request parameters to the form object and
 * validate the form object. </li>
 * <li> If there are binding or validation errors, the transition will not be allowed and the view state will
 * automatically be re-entered.
 * <li> If binding and validation are successful go to an action state called "processSubmit" (or any other appropriate
 * name). This will invoke an action method called "processSubmit" you must provide on a subclass to process form
 * submission, e.g. interacting with the business logic. </li>
 * <li> If business processing is ok, continue to a view state to display the success view. </li>
 * </ol>
 * <p>
 * Here is an example implementation of such a form flow:
 * 
 * <pre>
 *     &lt;view-state id=&quot;displayCriteria&quot;&gt;
 *         &lt;on-render&gt;
 *             &lt;evaluate expression=&quot;formAction.setupForm&quot;/&gt;
 *         &lt;/on-render&gt;
 *         &lt;transition on=&quot;search&quot; to=&quot;executeSearch&quot;&gt;
 *             &lt;evaluate expression=&quot;formAction.bindAndValidate&quot;/&gt;
 *         &lt;/transition&gt;
 *     &lt;/view-state&gt;
 * </pre>
 * 
 * <p>
 * When you need additional flexibility consider splitting the view state above acting as a single logical <i>form state</i>
 * into multiple states. For example, you could have one action state handle form setup, a view state trigger form
 * display, another action state handle data binding and validation, and another process form submission. This would be
 * a bit more verbose but would also give you more control over how you respond to specific results of fine-grained
 * actions that occur within the flow.
 * <p>
 * <b>Subclassing hooks:</b>
 * <ul>
 * <li>A important hook is {@link #createFormObject(RequestContext) createFormObject}. You may override this to
 * customize where the backing form object instance comes from (e.g instantiated transiently in memory or loaded from a
 * database).</li>
 * <li>An optional hook method provided by this class is {@link #initBinder(RequestContext, DataBinder) initBinder}.
 * This is called after a new data binder is created.
 * <li>Another optional hook method is {@link #registerPropertyEditors(PropertyEditorRegistry)}. By overriding it you
 * can register any required property editors for your form. Instead of overriding this method, consider setting an
 * explicit {@link org.springframework.beans.PropertyEditorRegistrar} strategy as a more reusable way to encapsulate
 * custom PropertyEditor installation logic.</li>
 * <li>Override {@link #validationEnabled(RequestContext)} to dynamically decide whether or not to do validation based
 * on data available in the request context.
 * </ul>
 * <p>
 * Note that this action does not provide a <i>referenceData()</i> hook method similar to that of Spring MVC's
 * <code>SimpleFormController</code>. If you wish to expose reference data to populate form drop downs you can define
 * a custom action method in your FormAction subclass that does just that. Simply invoke it as either a chained action
 * as part of the setupForm state, or as a fine grained state definition itself.
 * <p>
 * For example, you might create this method in your subclass:
 * 
 * <pre>
 * public Event setupReferenceData(RequestContext context) throws Exception {
 * 	MutableAttributeMap requestScope = context.getRequestScope();
 * 	requestScope.put(&quot;refData&quot;, lookupService.getSupportingFormData());
 * 	return success();
 * }
 * </pre>
 * 
 * ... and then invoke it like this:
 * 
 * <pre>
 *     &lt;view-state id=&quot;displayCriteria&quot;&gt;
 *         &lt;on-render&gt;
 *             &lt;evaluate expression=&quot;formAction.setupForm&quot;/&gt;
 *             &lt;evaluate expression=&quot;formAction.setupReferenceData&quot;/&gt;
 *         &lt;/on-render&gt;
 *         ...
 *     &lt;/view-state&gt;
 * </pre>
 * 
 * This style of calling multiple action methods in a chain (Chain of Responsibility) is preferred to overriding a
 * single action method. In general, action method overriding is discouraged.
 * <p>
 * When it comes to validating submitted input data using a registered {@link org.springframework.validation.Validator},
 * this class offers the following options:
 * <ul>
 * <li>If you don't want validation at all, just call {@link #bind(RequestContext)} instead of
 * {@link #bindAndValidate(RequestContext)} or don't register a validator.</li>
 * <li>If you want piecemeal validation, e.g. in a multi-page wizard, call {@link #bindAndValidate(RequestContext)} or
 * {@link #validate(RequestContext)} and specify a {@link #VALIDATOR_METHOD_ATTRIBUTE validatorMethod} action execution
 * attribute. This will invoke the identified custom validator method on the validator. The validator method signature
 * should follow the following pattern:
 * 
 * <pre>
 *     public void ${validateMethodName}(${formObjectClass}, Errors)
 * </pre>
 * 
 * For instance, having a action definition like this:
 * 
 * <pre>
 *     &lt;evaluate expression=&quot;formAction.bindAndValidate&quot;&gt;
 *         &lt;attribute name=&quot;validatorMethod&quot; value=&quot;validateSearchCriteria&quot;/&gt;
 *     &lt;/evaluate&gt;
 * </pre>
 * 
 * Would result in the <tt>public void validateSearchCriteria(SearchCriteria, Errors)</tt> method of the registered
 * validator being called if the form object class would be <code>SearchCriteria</code>.</li>
 * <li>If you want to do full validation using the
 * {@link org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) validate}
 * method of the registered validator, call {@link #bindAndValidate(RequestContext)} or
 * {@link #validate(RequestContext)} without specifying a "validatorMethod" action execution attribute.</li>
 * </ul>
 * 
 * <p>
 * <b>FormAction configurable properties</b><br>
 * <table border="1">
 * <tr>
 * <td><b>name</b></td>
 * <td><b>default</b></td>
 * <td><b>description</b></td>
 * </tr>
 * <tr>
 * <td>formObjectName</td>
 * <td>formObject</td>
 * <td>The name of the form object. The form object will be set in the configured scope using this name.</td>
 * </tr>
 * <tr>
 * <td>formObjectClass</td>
 * <td>null</td>
 * <td>The form object class for this action. An instance of this class will get populated and validated. Required when
 * using a validator.</td>
 * </tr>
 * <tr>
 * <td>formObjectScope</td>
 * <td>{@link org.springframework.webflow.execution.ScopeType#FLOW flow}</td>
 * <td>The scope in which the form object will be put. If put in flow scope the object will be cached and reused over
 * the life of the flow, preserving previous values. Request scope will cause a new fresh form object instance to be
 * created on each request into the flow execution.</td>
 * </tr>
 * <tr>
 * <td>formErrorsScope</td>
 * <td>{@link org.springframework.webflow.execution.ScopeType#FLASH flash}</td>
 * <td>The scope in which the form object errors instance will be put. If put in flash scope form errors will be cached
 * until the next user event is signaled. </td>
 * </tr>
 * <tr>
 * <td>propertyEditorRegistrar</td>
 * <td>null</td>
 * <td>The strategy used to register custom property editors with the data binder. This is an alternative to overriding
 * the {@link #registerPropertyEditors(PropertyEditorRegistry)} hook method. </td>
 * </tr>
 * <tr>
 * <td>validator</td>
 * <td>null</td>
 * <td>The validator for this action. The validator must support the specified form object class. </td>
 * </tr>
 * <tr>
 * <td>messageCodesResolver</td>
 * <td>null</td>
 * <td>Set the strategy to use for resolving errors into message codes. </td>
 * </tr>
 * </table>
 * 
 * @see org.springframework.beans.PropertyEditorRegistrar
 * @see org.springframework.validation.DataBinder
 * @see ScopeType
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class FormAction extends MultiAction implements InitializingBean {

	/**
	 * The default form object name ("formObject").
	 */
	public static final String DEFAULT_FORM_OBJECT_NAME = "formObject";

	/**
	 * Optional attribute that identifies the method that should be invoked on the configured validator instance, to
	 * support piecemeal wizard page validation ("validatorMethod").
	 */
	public static final String VALIDATOR_METHOD_ATTRIBUTE = "validatorMethod";

	/**
	 * The name the form object should be exposed under. Default is {@link #DEFAULT_FORM_OBJECT_NAME}.
	 */
	private String formObjectName = DEFAULT_FORM_OBJECT_NAME;

	/**
	 * The type of form object, typically an instantiable class. Required if {@link #createFormObject(RequestContext)}
	 * is not overridden or when a validator is used.
	 */
	private Class formObjectClass;

	/**
	 * The scope in which the form object should be exposed. Default is {@link ScopeType#FLOW}.
	 */
	private ScopeType formObjectScope = ScopeType.FLOW;

	/**
	 * The scope in which the form object errors holder should be exposed. Default is {@link ScopeType#FLASH}.
	 */
	private ScopeType formErrorsScope = ScopeType.FLASH;

	/**
	 * A centralized service for property editor registration, for applying type conversion during form object data
	 * binding. Can be used as an alternative to overriding {@link #registerPropertyEditors(PropertyEditorRegistry)}.
	 */
	private PropertyEditorRegistrar propertyEditorRegistrar;

	/**
	 * A validator for the form's form object.
	 */
	private Validator validator;

	/**
	 * Strategy for resolving error message codes.
	 */
	private MessageCodesResolver messageCodesResolver;

	/**
	 * A cache for dispatched validator methods.
	 */
	private DispatchMethodInvoker validateMethodInvoker;

	/**
	 * Bean-style default constructor; creates a initially unconfigured FormAction instance relying on default property
	 * values. Clients invoking this constructor directly must set the formObjectClass property or override
	 * {@link #createFormObject(RequestContext)}.
	 * @see #setFormObjectClass(Class)
	 */
	public FormAction() {
	}

	/**
	 * Creates a new form action that manages instance(s) of the specified form object class.
	 * @param formObjectClass the class of the form object (must be instantiable)
	 */
	public FormAction(Class formObjectClass) {
		setFormObjectClass(formObjectClass);
	}

	/**
	 * Return the name of the form object in the configured scope.
	 */
	public String getFormObjectName() {
		return formObjectName;
	}

	/**
	 * Set the name of the form object in the configured scope. The form object will be included in the configured scope
	 * under this name.
	 */
	public void setFormObjectName(String formObjectName) {
		this.formObjectName = formObjectName;
	}

	/**
	 * Return the form object class for this action.
	 */
	public Class getFormObjectClass() {
		return formObjectClass;
	}

	/**
	 * Set the form object class for this action. An instance of this class will get populated and validated. This is a
	 * required property if you register a validator with the form action ({@link #setValidator(Validator)})!
	 * <p>
	 * If no form object name is set at the moment this method is called, a form object name will be automatically
	 * generated based on the provided form object class using
	 * {@link ClassUtils#getShortNameAsProperty(java.lang.Class)}.
	 */
	public void setFormObjectClass(Class formObjectClass) {
		this.formObjectClass = formObjectClass;
		// generate a default form object name
		if ((getFormObjectName() == null || getFormObjectName() == DEFAULT_FORM_OBJECT_NAME) && formObjectClass != null) {
			setFormObjectName(ClassUtils.getShortNameAsProperty(formObjectClass));
		}
	}

	/**
	 * Get the scope in which the form object will be placed.
	 */
	public ScopeType getFormObjectScope() {
		return formObjectScope;
	}

	/**
	 * Set the scope in which the form object will be placed. The default if not set is
	 * {@link ScopeType#FLOW flow scope}.
	 */
	public void setFormObjectScope(ScopeType scopeType) {
		this.formObjectScope = scopeType;
	}

	/**
	 * Get the scope in which the Errors object will be placed.
	 */
	public ScopeType getFormErrorsScope() {
		return formErrorsScope;
	}

	/**
	 * Set the scope in which the Errors object will be placed. The default if not set is
	 * {@link ScopeType#FLASH flash scope}.
	 */
	public void setFormErrorsScope(ScopeType errorsScope) {
		this.formErrorsScope = errorsScope;
	}

	/**
	 * Get the property editor registration strategy for this action's data binders.
	 */
	public PropertyEditorRegistrar getPropertyEditorRegistrar() {
		return propertyEditorRegistrar;
	}

	/**
	 * Set a property editor registration strategy for this action's data binders. This is an alternative to overriding
	 * the {@link #registerPropertyEditors(PropertyEditorRegistry)} method.
	 */
	public void setPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar) {
		this.propertyEditorRegistrar = propertyEditorRegistrar;
	}

	/**
	 * Returns the validator for this action.
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * Set the validator for this action. When setting a validator, you must also set the
	 * {@link #setFormObjectClass(Class) form object class}. The validator must support the specified form object
	 * class.
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Return the strategy to use for resolving errors into message codes.
	 */
	public MessageCodesResolver getMessageCodesResolver() {
		return messageCodesResolver;
	}

	/**
	 * Set the strategy to use for resolving errors into message codes. Applies the given strategy to all data binders
	 * used by this action.
	 * <p>
	 * Default is null, i.e. using the default strategy of the data binder.
	 * @see #createBinder(RequestContext, Object)
	 * @see org.springframework.validation.DataBinder#setMessageCodesResolver(org.springframework.validation.MessageCodesResolver)
	 */
	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
	}

	protected void initAction() {
		if (getValidator() != null) {
			if (getFormObjectClass() != null && !getValidator().supports(getFormObjectClass())) {
				throw new IllegalArgumentException("Validator [" + getValidator()
						+ "] does not support form object class [" + getFormObjectClass() + "]");
			}
			// signature: public void ${validateMethodName}(${formObjectClass}, Errors)
			validateMethodInvoker = new DispatchMethodInvoker(getValidator(), new Class[] { getFormObjectClass(),
					Errors.class });
		}
	}

	// action methods

	/**
	 * Prepares a form object for display in a new form, creating it and caching it in the {@link #getFormObjectScope()}
	 * if necessary. Also installs custom property editors for formatting form object values in UI controls such as text
	 * fields.
	 * <p>
	 * A new form object instance will only be created (or more generally acquired) with a call to
	 * {@link #createFormObject(RequestContext)}, if the form object does not yet exist in the configured
	 * {@link #getFormObjectScope() scope}. If you want to reset the form handling machinery, including creation or
	 * loading of a fresh form object instance, call {@link #resetForm(RequestContext)} instead.
	 * <p>
	 * NOTE: This action method is not designed to be overridden and might become <code>final</code> in a future
	 * version of Spring Web Flow. If you need to execute custom form setup logic have your flow call this method along
	 * with your own custom methods as part of a single action chain.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return "success" when binding and validation is successful
	 * @throws Exception an <b>unrecoverable</b> exception occurs, either checked or unchecked
	 * @see #createFormObject(RequestContext)
	 */
	public Event setupForm(RequestContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing setupForm");
		}
		// retrieve the form object, creating it if necessary
		Object formObject = getFormObject(context);
		ensureFormErrorsExposed(context, formObject);
		return success();
	}

	/**
	 * Bind incoming request parameters to allowed fields of the form object and then validate the bound form object if
	 * a validator is configured.
	 * <p>
	 * NOTE: This action method is not designed to be overridden and might become <code>final</code> in a future
	 * version of Spring Web Flow. If you need to execute custom bind and validate logic have your flow call this method
	 * along with your own custom methods as part of a single action chain. Alternatively, override the
	 * {@link #doBind(RequestContext, DataBinder)} or {@link #doValidate(RequestContext, Object, Errors)} hooks.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return "success" when binding and validation is successful, "error" if there were binding and/or validation
	 * errors
	 * @throws Exception an <b>unrecoverable</b> exception occurred, either checked or unchecked
	 */
	public Event bindAndValidate(RequestContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing bind");
		}
		Object formObject = getFormObject(context);
		DataBinder binder = createBinder(context, formObject);
		doBind(context, binder);
		if (getValidator() != null && validationEnabled(context)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Executing validation");
			}
			doValidate(context, formObject, binder.getBindingResult());
		} else {
			if (logger.isDebugEnabled()) {
				if (getValidator() == null) {
					logger.debug("No validator is configured, no validation will occur after binding");
				} else {
					logger.debug("Validation was disabled for this bindAndValidate request");
				}
			}
		}
		putFormErrors(context, binder.getBindingResult());
		return binder.getBindingResult().hasErrors() ? error() : success();
	}

	/**
	 * Bind incoming request parameters to allowed fields of the form object.
	 * <p>
	 * NOTE: This action method is not designed to be overridden and might become <code>final</code> in a future
	 * version of Spring Web Flow. If you need to execute custom data binding logic have your flow call this method
	 * along with your own custom methods as part of a single action chain. Alternatively, override the
	 * {@link #doBind(RequestContext, DataBinder)} hook.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return "success" if there are no binding errors, "error" otherwise
	 * @throws Exception an <b>unrecoverable</b> exception occured, either checked or unchecked
	 */
	public Event bind(RequestContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing bind");
		}
		Object formObject = getFormObject(context);
		DataBinder binder = createBinder(context, formObject);
		doBind(context, binder);
		putFormErrors(context, binder.getBindingResult());
		return binder.getBindingResult().hasErrors() ? error() : success();
	}

	/**
	 * Validate the form object by invoking the validator if configured.
	 * <p>
	 * NOTE: This action method is not designed to be overridden and might become <code>final</code> in a future
	 * version of Spring Web Flow. If you need to execute custom validation logic have your flow call this method along
	 * with your own custom methods as part of a single action chain. Alternatively, override the
	 * {@link #doValidate(RequestContext, Object, Errors)} hook.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return "success" if there are no validation errors, "error" otherwise
	 * @throws Exception an <b>unrecoverable</b> exception occured, either checked or unchecked
	 * @see #getValidator()
	 */
	public Event validate(RequestContext context) throws Exception {
		if (getValidator() != null && validationEnabled(context)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Executing validation");
			}
			Object formObject = getFormObject(context);
			Errors errors = getFormErrors(context);
			doValidate(context, formObject, errors);
			return errors.hasErrors() ? error() : success();
		} else {
			if (logger.isDebugEnabled()) {
				if (getValidator() == null) {
					logger.debug("No validator is configured, no validation will occur");
				} else {
					logger.debug("Validation was disabled for this request");
				}
			}
			return success();
		}
	}

	/**
	 * Resets the form by clearing out the form object in the specified scope and recreating it.
	 * <p>
	 * NOTE: This action method is not designed to be overridden and might become <code>final</code> in a future
	 * version of Spring Web Flow. If you need to execute custom reset logic have your flow call this method along with
	 * your own custom methods as part of a single action chain.
	 * @param context the request context
	 * @return "success" if the reset action completed successfully
	 * @throws Exception if an exception occured
	 * @see #createFormObject(RequestContext)
	 */
	public Event resetForm(RequestContext context) throws Exception {
		Object formObject = initFormObject(context);
		initFormErrors(context, formObject);
		return success();
	}

	// internal helpers

	/**
	 * Create the new form object and put it in the configured {@link #getFormObjectScope() scope}.
	 * @param context the flow execution request context
	 * @return the new form object
	 * @throws Exception an exception occured creating the form object
	 */
	private Object initFormObject(RequestContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new form object with name '" + getFormObjectName() + "'");
		}
		Object formObject = createFormObject(context);
		putFormObject(context, formObject);
		return formObject;
	}

	/**
	 * Put given form object in the configured scope of given context.
	 */
	private void putFormObject(RequestContext context, Object formObject) {
		if (logger.isDebugEnabled()) {
			logger.debug("Putting form object of type [" + formObject.getClass() + "] in scope " + getFormObjectScope()
					+ " with name '" + getFormObjectName() + "'");
		}
		getFormObjectAccessor(context).putFormObject(formObject, getFormObjectName(), getFormObjectScope());
	}

	/**
	 * Initialize a new form object {@link Errors errors} instance in the configured {@link #getFormErrorsScope() scope}.
	 * This method also registers any {@link PropertiesEditor property editors} used to format form object property
	 * values.
	 * @param context the current flow execution request context
	 * @param formObject the form object for which errors will be tracked
	 */
	private Errors initFormErrors(RequestContext context, Object formObject) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new form errors for object with name '" + getFormObjectName() + "'");
		}
		Errors errors = createBinder(context, formObject).getBindingResult();
		putFormErrors(context, errors);
		return errors;
	}

	/**
	 * Put given errors instance in the configured scope of given context.
	 */
	private void putFormErrors(RequestContext context, Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("Putting form errors instance in scope " + getFormErrorsScope());
		}
		getFormObjectAccessor(context).putFormErrors(errors, getFormErrorsScope());
	}

	/**
	 * Make sure a <i>valid</i> Errors instance for given form object is exposed in given context.
	 */
	private void ensureFormErrorsExposed(RequestContext context, Object formObject) throws Exception {
		if (!formErrorsExposed(context)) {
			// initialize and expose a fresh errors instance to the flow with
			// editors applied
			initFormErrors(context, formObject);
		} else {
			// trying to reuse an existing errors instance
			if (formErrorsValid(context, formObject)) {
				// reapply property editors against the existing errors instance
				reinstallPropertyEditors(context);
			} else {
				// the existing errors instance seems to be invalid
				// initialize a new errors instance, but copy over error information
				if (logger.isInfoEnabled()) {
					logger.info("Fixing inconsistent Errors instance: initializing a new Errors instance "
							+ "wrapping from object '" + formObject + "' in scope '" + getFormErrorsScope()
							+ "' and copying over all existing error information.");
				}
				Errors invalidExistingErrors = getFormObjectAccessor(context).getFormErrors(getFormObjectName(),
						getFormErrorsScope());
				Errors newErrors = initFormErrors(context, formObject);
				newErrors.addAllErrors(invalidExistingErrors);
			}
		}
	}

	/**
	 * Check if there is an Errors instance available in given context for given form object.
	 */
	private boolean formErrorsExposed(RequestContext context) {
		return getFormObjectAccessor(context).getFormErrors(getFormObjectName(), getFormErrorsScope()) != null;
	}

	/**
	 * Check if the Errors instance available in given context is valid for given form object.
	 */
	private boolean formErrorsValid(RequestContext context, Object formObject) {
		Errors errors = getFormObjectAccessor(context).getFormErrors(getFormObjectName(), getFormErrorsScope());
		if (errors instanceof BindingResult) {
			BindingResult be = (BindingResult) errors;
			if (be.getTarget() != formObject) {
				if (logger.isInfoEnabled()) {
					logger.info("Inconsistency detected: the Errors instance in '" + getFormErrorsScope()
							+ "' does NOT wrap the current form object '" + formObject + "' of class "
							+ formObject.getClass()
							+ "; instead this Errors instance unexpectedly wraps the target object '" + be.getTarget()
							+ "' of class: " + be.getTarget().getClass() + ".");
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * Re-registers property editors against the current form errors instance.
	 * @param context the flow execution request context
	 */
	private void reinstallPropertyEditors(RequestContext context) {
		BindingResult errors = (BindingResult) getFormObjectAccessor(context).getFormErrors(getFormObjectName(),
				getFormErrorsScope());
		registerPropertyEditors(context, errors.getPropertyEditorRegistry());
	}

	/**
	 * Invoke specified validator method on the validator registered with this action. The validator method for
	 * piecemeal validation should have the following signature:
	 * 
	 * <pre>
	 *     public void ${validateMethodName}(${formObjectClass}, Errors)
	 * </pre>
	 * 
	 * @param validatorMethod the name of the validator method to invoke
	 * @param formObject the form object
	 * @param errors possible binding errors
	 * @throws Exception when an unrecoverable exception occurs
	 */
	private void invokeValidatorMethod(String validatorMethod, Object formObject, Errors errors) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking piecemeal validator method '" + validatorMethod + "(" + getFormObjectClass()
					+ ", Errors)'");
		}
		getValidateMethodInvoker().invoke(validatorMethod, new Object[] { formObject, errors });
	}

	// accessible helpers (subclasses could override if necessary)

	/**
	 * Convenience method that returns the form object for this form action. If not found in the configured scope, a new
	 * form object will be created by a call to {@link #createFormObject(RequestContext)} and exposed in the configured
	 * {@link #getFormObjectScope() scope}.
	 * <p>
	 * The returned form object will become the
	 * {@link FormObjectAccessor#setCurrentFormObject(Object, ScopeType) current} form object.
	 * @param context the flow execution request context
	 * @return the form object
	 * @throws Exception when an unrecoverable exception occurs
	 */
	protected Object getFormObject(RequestContext context) throws Exception {
		FormObjectAccessor accessor = getFormObjectAccessor(context);
		Object formObject = accessor.getFormObject(getFormObjectName(), getFormObjectScope());
		if (formObject == null) {
			formObject = initFormObject(context);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found existing form object with name '" + getFormObjectName() + "' of type ["
						+ formObject.getClass() + "] in scope " + getFormObjectScope());
			}
			accessor.setCurrentFormObject(formObject, getFormObjectScope());
		}
		return formObject;
	}

	/**
	 * Convenience method that returns the form object errors for this form action. If not found in the configured
	 * scope, a new form object errors will be created, initialized, and exposed in the confgured
	 * {@link #getFormErrorsScope() scope}.
	 * <p>
	 * Keep in mind that an Errors instance wraps a form object, so a form object will also be created if required (see
	 * {@link #getFormObject(RequestContext)}).
	 * @param context the flow request context
	 * @return the form errors
	 * @throws Exception when an unrecoverable exception occurs
	 */
	protected Errors getFormErrors(RequestContext context) throws Exception {
		Object formObject = getFormObject(context);
		ensureFormErrorsExposed(context, formObject);
		return getFormObjectAccessor(context).getFormErrors(getFormObjectName(), getFormErrorsScope());
	}

	/**
	 * Create a new binder instance for the given form object and request context. Can be overridden to plug in custom
	 * DataBinder subclasses.
	 * <p>
	 * Default implementation creates a standard WebDataBinder, and invokes
	 * {@link #initBinder(RequestContext, DataBinder)} and {@link #registerPropertyEditors(PropertyEditorRegistry)}.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @param formObject the form object to bind onto
	 * @return the new binder instance
	 * @throws Exception when an unrecoverable exception occurs
	 * @see WebDataBinder
	 * @see #initBinder(RequestContext, DataBinder)
	 * @see #setMessageCodesResolver(MessageCodesResolver)
	 */
	protected DataBinder createBinder(RequestContext context, Object formObject) throws Exception {
		DataBinder binder = new WebDataBinder(formObject, getFormObjectName());
		if (getMessageCodesResolver() != null) {
			binder.setMessageCodesResolver(getMessageCodesResolver());
		}
		initBinder(context, binder);
		registerPropertyEditors(context, binder);
		return binder;
	}

	/**
	 * Bind allowed parameters in the external context request parameter map to the form object using given binder.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @param binder the data binder to use
	 * @throws Exception when an unrecoverable exception occurs
	 */
	protected void doBind(RequestContext context, DataBinder binder) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Binding allowed request parameters in "
					+ StylerUtils.style(context.getExternalContext().getRequestParameterMap())
					+ " to form object with name '" + binder.getObjectName() + "', pre-bind formObject toString = "
					+ binder.getTarget());
			if (binder.getAllowedFields() != null && binder.getAllowedFields().length > 0) {
				logger.debug("(Allowed fields are " + StylerUtils.style(binder.getAllowedFields()) + ")");
			} else {
				logger.debug("(Any field is allowed)");
			}
		}
		binder.bind(new MutablePropertyValues(context.getRequestParameters().asMap()));
		if (logger.isDebugEnabled()) {
			logger.debug("Binding completed for form object with name '" + binder.getObjectName()
					+ "', post-bind formObject toString = " + binder.getTarget());
			logger.debug("There are [" + binder.getBindingResult().getErrorCount() + "] errors, details: "
					+ binder.getBindingResult().getAllErrors());
		}
	}

	/**
	 * Validate given form object using a registered validator. If a "validatorMethod" action property is specified for
	 * the currently executing action, the identified validator method will be invoked. When no such property is found,
	 * the defualt <code>validate()</code> method is invoked.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @param formObject the form object
	 * @param errors the errors instance to record validation errors in
	 * @throws Exception when an unrecoverable exception occurs
	 */
	protected void doValidate(RequestContext context, Object formObject, Errors errors) throws Exception {
		Assert.notNull(getValidator(), "The validator must not be null when attempting validation -- programmer error");
		String validatorMethodName = context.getAttributes().getString(VALIDATOR_METHOD_ATTRIBUTE);
		if (StringUtils.hasText(validatorMethodName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking validation method '" + validatorMethodName + "' on validator " + getValidator());
			}
			invokeValidatorMethod(validatorMethodName, formObject, errors);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking validator " + getValidator());
			}
			getValidator().validate(formObject, errors);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Validation completed for form object");
			logger.debug("There are [" + errors.getErrorCount() + "] errors, details: " + errors.getAllErrors());
		}
	}

	/**
	 * Returns a dispatcher to invoke validation methods. Subclasses could override this to return a custom dispatcher.
	 */
	protected DispatchMethodInvoker getValidateMethodInvoker() {
		return validateMethodInvoker;
	}

	/**
	 * Factory method that returns a new form object accessor for accessing form objects in the provided request
	 * context.
	 * @param context the flow request context
	 * @return the accessor
	 */
	protected FormObjectAccessor getFormObjectAccessor(RequestContext context) {
		return new FormObjectAccessor(context);
	}

	// common subclassing hook methods

	/**
	 * Create the backing form object instance that should be managed by this {@link FormAction form action}. By
	 * default, will attempt to instantiate a new form object instance of type {@link #getFormObjectClass()} transiently
	 * in memory.
	 * <p>
	 * Subclasses should override if they need to load the form object from a specific location or resource such as a
	 * database or filesystem.
	 * <p>
	 * Subclasses should override if they need to customize how a transient form object is assembled during creation.
	 * @param context the action execution context for accessing flow data
	 * @return the form object
	 * @throws IllegalStateException if the {@link #getFormObjectClass()} property is not set and this method has not
	 * been overridden
	 * @throws Exception when an unrecoverable exception occurs
	 */
	protected Object createFormObject(RequestContext context) throws Exception {
		if (getFormObjectClass() == null) {
			throw new IllegalStateException("Cannot create form object without formObjectClass property being set -- "
					+ "either set formObjectClass or override createFormObject");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new instance of form object class [" + getFormObjectClass() + "]");
		}
		return getFormObjectClass().newInstance();
	}

	/**
	 * Initialize a new binder instance. This hook allows customization of binder settings such as the
	 * {@link DataBinder#getAllowedFields() allowed fields}, {@link DataBinder#getRequiredFields() required fields} and
	 * {@link DataBinder#initDirectFieldAccess() direct field access}. Called by
	 * {@link #createBinder(RequestContext, Object)}.
	 * <p>
	 * Note that registration of custom property editors should be done in
	 * {@link #registerPropertyEditors(PropertyEditorRegistry)}, not here! This method will only be called when a
	 * <b>new</b> data binder is created.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @param binder new binder instance
	 * @see #createBinder(RequestContext, Object)
	 */
	protected void initBinder(RequestContext context, DataBinder binder) {
	}

	/**
	 * Register custom editors to perform type conversion on fields of your form object during data binding and form
	 * display. This method is called on form errors initialization and
	 * {@link #initBinder(RequestContext, DataBinder) data binder} initialization.
	 * <p>
	 * Property editors give you full control over how objects are transformed to and from a formatted String form for
	 * display on a user interface such as a HTML page.
	 * <p>
	 * This default implementation will call the {@link #registerPropertyEditors(PropertyEditorRegistry) simpler form}
	 * of the method not taking a <tt>RequestContext</tt> parameter.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @param registry the property editor registry to register editors in
	 * @see #registerPropertyEditors(PropertyEditorRegistry)
	 */
	protected void registerPropertyEditors(RequestContext context, PropertyEditorRegistry registry) {
		registerPropertyEditors(registry);
	}

	/**
	 * Register custom editors to perform type conversion on fields of your form object during data binding and form
	 * display. This method is called on form errors initialization and
	 * {@link #initBinder(RequestContext, DataBinder) data binder} initialization.
	 * <p>
	 * Property editors give you full control over how objects are transformed to and from a formatted String form for
	 * display on a user interface such as a HTML page.
	 * <p>
	 * This default implementation will simply call <tt>registerCustomEditors</tt> on the
	 * {@link #getPropertyEditorRegistrar() propertyEditorRegistrar} object that has been set for the action, if any.
	 * @param registry the property editor registry to register editors in
	 */
	protected void registerPropertyEditors(PropertyEditorRegistry registry) {
		if (getPropertyEditorRegistrar() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Registering custom property editors using configured registrar");
			}
			getPropertyEditorRegistrar().registerCustomEditors(registry);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No property editor registrar set, no custom editors to register");
			}
		}
	}

	/**
	 * Return whether validation should be performed given the state of the flow request context. Default implementation
	 * always returns true.
	 * @param context the request context, for accessing and setting data in "flow scope" or "request scope"
	 * @return whether or not validation is enabled
	 */
	protected boolean validationEnabled(RequestContext context) {
		return true;
	}

	public String toString() {
		return new ToStringCreator(this).append("formObjectName", formObjectName).append("formObjectClass",
				formObjectClass).append("formObjectScope", formObjectScope).toString();
	}
}