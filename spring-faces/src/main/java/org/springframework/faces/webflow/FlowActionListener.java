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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.NavigationHandler;
import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageContextErrors;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * The default {@link ActionListener} implementation to be used with Web Flow.
 * 
 * <p>
 * This implementation bypasses the JSF {@link NavigationHandler} mechanism to instead let the event be handled directly
 * by Web Flow.
 * </p>
 * 
 * <p>
 * Web Flow's model-level validation will be invoked here after an event has been detected if the event is not an
 * immediate event.
 * </p>
 * 
 * @author Jeremy Grelle
 */
public class FlowActionListener implements ActionListener {

	private static final Log logger = LogFactory.getLog(FlowActionListener.class);

	private static final String MESSAGES_ID = "messages";

	private ActionListener delegate;

	public FlowActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		if (!JsfUtils.isFlowRequest()) {
			delegate.processAction(actionEvent);
			return;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		ActionSource source = (ActionSource) actionEvent.getSource();
		String eventId = null;
		if (source.getAction() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking action " + source.getAction());
			}
			eventId = (String) source.getAction().invoke(context, null);
		}
		if (StringUtils.hasText(eventId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Event '" + eventId + "' detected");
			}
			if (source.isImmediate() || validateModel(context, eventId)) {
				context.getExternalContext().getRequestMap().put(JsfView.EVENT_KEY, eventId);
			}
		} else {
			logger.debug("No action event detected");
			context.getExternalContext().getRequestMap().remove(JsfView.EVENT_KEY);
		}
		// tells JSF lifecycle that rendering should now happen and any subsequent phases should be skipped
		// required in the case of this action listener firing immediately (immediate=true) before validation
		context.renderResponse();
	}

	// internal helpers

	private boolean validateModel(FacesContext facesContext, String eventId) {
		boolean isValid = true;
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Object model = getModelObject(requestContext);
		if (shouldValidate(requestContext, model, eventId)) {
			validate(requestContext, model);
			if (requestContext.getMessageContext().hasErrorMessages()) {
				isValid = false;
				if (requestContext.getExternalContext().isAjaxRequest()) {
					List fragments = new ArrayList();
					String formId = getModelExpression(requestContext).getExpressionString();
					if (facesContext.getViewRoot().findComponent(formId) != null) {
						fragments.add(formId);
					}
					if (facesContext.getViewRoot().findComponent(MESSAGES_ID) != null) {
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
		return isValid;
	}

	private Object getModelObject(RequestContext requestContext) {
		Expression model = getModelExpression(requestContext);
		if (model != null) {
			return model.getValue(requestContext);
		} else {
			return null;
		}
	}

	private Expression getModelExpression(RequestContext requestContext) {
		return (Expression) requestContext.getCurrentState().getAttributes().get("model");
	}

	private boolean shouldValidate(RequestContext requestContext, Object model, String eventId) {
		if (model == null) {
			return false;
		}
		TransitionDefinition transition = requestContext.getMatchingTransition(eventId);
		if (transition != null) {
			if (transition.getAttributes().contains("validate")) {
				return transition.getAttributes().getBoolean("validate").booleanValue();
			}
		}
		return true;
	}

	private void validate(RequestContext requestContext, Object model) {
		String validateMethodName = "validate" + StringUtils.capitalize(requestContext.getCurrentState().getId());
		Method validateMethod = ReflectionUtils.findMethod(model.getClass(), validateMethodName,
				new Class[] { MessageContext.class });
		if (validateMethod != null) {
			ReflectionUtils.invokeMethod(validateMethod, model, new Object[] { requestContext.getMessageContext() });
		}
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		if (beanFactory != null) {
			String validatorName = getModelExpression(requestContext).getExpressionString() + "Validator";
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
}
