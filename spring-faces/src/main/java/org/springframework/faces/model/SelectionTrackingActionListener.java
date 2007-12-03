package org.springframework.faces.model;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.faces.webflow.FlowActionListener;

/**
 * Custom {@link ActionListener} that inspects the {@link UIComponent} that signaled the current {@link ActionEvent} to
 * determine whether it is a child of a {@link UIData} component that uses a {@link SelectionAware} data model
 * implementation. If a containing SelectionAware model is found, the row containing the event-signaling component
 * instance will be selected. This enables convenient access to the selected model state at any time through EL
 * expressions such as #{model.selectedRow.id} without having to rely on the whether or not the current row index is
 * pointing to the desired row as it would need to be to use an expression such as #{model.rowData.id}
 * 
 * @author Jeremy Grelle
 */
public class SelectionTrackingActionListener implements ActionListener {

	private static final Log logger = LogFactory.getLog(FlowActionListener.class);

	private ActionListener delegate;

	public SelectionTrackingActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent event) throws AbortProcessingException {
		trackSelection(event.getComponent());
		delegate.processAction(event);
	}

	private void trackSelection(UIComponent component) {
		// Find parent UIData instance if it exists
		UIData table = null;
		UIComponent currentComponent = component;
		while (!(currentComponent.getParent() instanceof UIViewRoot)) {
			if (currentComponent.getParent() instanceof UIData) {
				table = (UIData) currentComponent.getParent();
				break;
			}
			currentComponent = currentComponent.getParent();
		}
		if (table != null && table.getValue() instanceof SelectionAware) {
			SelectionAware selectableModel = (SelectionAware) table.getValue();
			selectableModel.setSelected(true);
			if (logger.isDebugEnabled()) {
				logger.debug("Row selection has been set on the current SelectionAware data model.");
			}
		}
	}

}
