package org.springframework.faces.webflow;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.event.PostRestoreStateEvent;

/**
 * A VisitCallback used to deliver a PostRestoreStataEvent similar to
 * {@code RestoreViewPhase.deliverPostRestoreStateEvent(..)} in Sun's JSF.
 * 
 * @since 2.3.1
 */
class PostRestoreStateEventVisitCallback implements VisitCallback {

	private PostRestoreStateEvent event;

	public VisitResult visit(VisitContext context, UIComponent target) {
		if (this.event == null) {
			this.event = new PostRestoreStateEvent(target);
		} else {
			this.event.setComponent(target);
		}
		target.processEvent(event);
		return VisitResult.ACCEPT;
	}
}
