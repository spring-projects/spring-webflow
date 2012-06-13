/*
 * Copyright 2004-2012 the original author or authors.
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
		target.processEvent(this.event);
		return VisitResult.ACCEPT;
	}
}
