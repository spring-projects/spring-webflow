package org.springframework.faces.webflow;

import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.springframework.util.StringUtils;

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
	}

}
