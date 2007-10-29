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
package org.springframework.faces.webflow;

import javax.faces.application.NavigationHandler;
import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.springframework.util.StringUtils;

/**
 * The default {@link ActionListener} implementation to be used with Web Flow.
 * <p>
 * This implementation bypasses the JSF {@link NavigationHandler} mechanism to instead let the event be handled directly
 * by Web Flow.
 * 
 * @author Jeremy Grelle
 */
public class FlowActionListener implements ActionListener {

	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {

		FacesContext context = FacesContext.getCurrentInstance();
		ActionSource source = (ActionSource) actionEvent.getSource();
		String result = null;

		if (source.getAction() != null) {
			result = (String) source.getAction().invoke(context, null);
		}

		if (StringUtils.hasText(result)) {
			context.getExternalContext().getRequestMap().put(JsfView.EVENT_KEY, result);
		} else {
			context.getExternalContext().getRequestMap().remove(JsfView.EVENT_KEY);
		}
		context.renderResponse();
	}

}
