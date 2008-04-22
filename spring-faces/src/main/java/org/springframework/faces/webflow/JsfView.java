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
package org.springframework.faces.webflow;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.definition.TransitionableStateDefinition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.mvc.view.MessageContextErrors;

/**
 * JSF-specific {@link View} implementation.
 * 
 * @author Jeremy Grelle
 */
public class JsfView implements View {

	private static final Log logger = LogFactory.getLog(JsfView.class);

	public static final String EVENT_KEY = "org.springframework.webflow.FacesEvent";

	private static final String MESSAGES_ID = "messages";

	/**
	 * The root of the JSF component tree managed by this view
	 */
	private UIViewRoot viewRoot;

	private Lifecycle facesLifecycle;

	private RequestContext requestContext;

	private String viewId;

	private boolean restored = false;

	private boolean viewErrors = false;

	public JsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle, RequestContext context) {
		this.viewRoot = viewRoot;
		this.viewId = viewRoot.getViewId();
		this.facesLifecycle = facesLifecycle;
		this.requestContext = context;
	}

	/**
	 * This implementation performs the standard duties of the JSF RENDER_RESPONSE phase.
	 */
	public void render() throws IOException {
		FacesContext facesContext = FlowFacesContext.newInstance(requestContext, facesLifecycle);
		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
		try {
			JsfUtils.notifyBeforeListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
			logger.debug("Asking view handler to render view");
			facesContext.getApplication().getViewHandler().renderView(facesContext, viewRoot);
			JsfUtils.notifyAfterListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
		} catch (IOException e) {
			throw new FacesException("An I/O error occurred during view rendering", e);
		} finally {
			logger.debug("View rendering complete");
			facesContext.responseComplete();
			facesContext.release();
		}
	}

	public void processUserEvent() {
		FacesContext facesContext = FlowFacesContext.newInstance(requestContext, facesLifecycle);
		facesContext.setViewRoot(viewRoot);
		try {
			if (restored && !facesContext.getResponseComplete() && !facesContext.getRenderResponse()) {
				facesLifecycle.execute(facesContext);
				// TODO move this and renderResponse behavior into lifecycle
				validateModel(facesContext);
			}
		} finally {
			facesContext.release();
		}
	}

	public boolean hasFlowEvent() {
		return requestContext.getExternalContext().getRequestMap().contains(EVENT_KEY) && !viewErrors;
	}

	public Event getFlowEvent() {
		return new Event(this, getEventId());
	}

	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	public String toString() {
		return "[JSFView = '" + viewId + "']";
	}

	public void setRestored(boolean restored) {
		this.restored = restored;
	}

	private void validateModel(FacesContext facesContext) {
		Object model = getModelObject();
		if (shouldValidate(model)) {
			validate(model);
			if (requestContext.getMessageContext().hasErrorMessages()) {
				viewErrors = true;
				if (requestContext.getExternalContext().isAjaxRequest()) {
					List fragments = new ArrayList();
					String formId = getModelExpression().getExpressionString();
					if (viewRoot.findComponent(formId) != null) {
						fragments.add(formId);
					}
					if (viewRoot.findComponent(MESSAGES_ID) != null) {
						fragments.add(MESSAGES_ID);
					}
					if (fragments.size() > 0) {
						String[] fragmentsArray = new String[fragments.size()];
						for (int i = 0; i < fragments.size(); i++) {
							fragmentsArray[i] = (String) fragments.get(i);
						}
						requestContext.getFlashScope().put(View.RENDER_FRAGMENTS_ATTRIBUTE, fragmentsArray);
					}
				}
			}
		}
	}

	private Object getModelObject() {
		Expression model = getModelExpression();
		if (model != null) {
			return model.getValue(requestContext);
		} else {
			return null;
		}
	}

	private Expression getModelExpression() {
		return (Expression) requestContext.getCurrentState().getAttributes().get("model");
	}

	private boolean shouldValidate(Object model) {
		if (model == null) {
			return false;
		}
		TransitionableStateDefinition currentState = (TransitionableStateDefinition) requestContext.getCurrentState();
		TransitionDefinition transition = currentState.getTransition(getEventId());
		if (transition != null) {
			if (transition.getAttributes().contains("bind")) {
				return transition.getAttributes().getBoolean("bind").booleanValue();
			}
		}
		return true;
	}

	private void validate(Object model) {
		String validateMethodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), validateMethodName,
				new Class[] { MessageContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { requestContext.getMessageContext() });
		}
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		if (beanFactory != null) {
			String validatorName = getModelExpression().getExpressionString() + "Validator";
			if (beanFactory.containsBean(validatorName)) {
				Object validator = beanFactory.getBean(validatorName);
				validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
						model.getClass(), MessageContext.class });
				if (validateMethod != null) {
					ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
							requestContext.getMessageContext() });
				} else {
					validateMethod = ReflectionUtils.findMethod(validator.getClass(), validateMethodName, new Class[] {
							model.getClass(), Errors.class });
					if (validateMethod != null) {
						ReflectionUtils.invokeMethod(validateMethod, validator, new Object[] { model,
								new MessageContextErrors(requestContext.getMessageContext()) });
					}
				}
			}
		}
	}

	private String getEventId() {
		return (String) requestContext.getExternalContext().getRequestMap().get(EVENT_KEY);
	}
}