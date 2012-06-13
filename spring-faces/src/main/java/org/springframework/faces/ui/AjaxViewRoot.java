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
package org.springframework.faces.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.View;

/**
 * Customizes the behavior of an existing UIViewRoot with Ajax-aware processing.
 * 
 * <p>
 * This component is the key to rendering partial subtrees of the JSF component tree. It makes use of JSF 1.2's
 * {@link UIComponent#invokeOnComponent(FacesContext, String, ContextCallback)} method to execute the various phases of
 * the {@link Lifecycle} on each subtree.
 * </p>
 * 
 * @author Jeremy Grelle
 * @author Nazaret Kazarian
 */
public class AjaxViewRoot extends DelegatingViewRoot {

	public static final String AJAX_SOURCE_PARAM = "ajaxSource";

	public static final String PROCESS_IDS_PARAM = "processIds";

	protected static final String FORM_RENDERED = "formRendered";

	protected static final String PROCESS_ALL = "*";

	private final List<FacesEvent> events = new ArrayList<FacesEvent>();

	private String[] processIds;

	private String[] renderIds;

	private static final String RENDER_IDS_EXPRESSION = "#{" + View.RENDER_FRAGMENTS_ATTRIBUTE + "}";

	private final ValueBinding renderIdsExpr;

	public AjaxViewRoot(UIViewRoot original) {
		super(original);
		this.renderIdsExpr = FacesContext.getCurrentInstance().getApplication().createValueBinding(RENDER_IDS_EXPRESSION);
		if (!StringUtils.hasText(original.getId())) {
			original.setId(createUniqueId());
		}
		swapChildren(original, this);
	}

	// implementing view root

	public String getId() {
		return getOriginalViewRoot().getId() + "_ajax";
	}

	public void queueEvent(FacesEvent event) {
		Assert.notNull(event, "Cannot queue a null event.");
		this.events.add(event);
	}

	public void encodeAll(FacesContext context) throws IOException {
		for (int i = 0; i < getRenderIds().length; i++) {
			String renderId = getRenderIds()[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					try {
						target.encodeAll(context);
						if (target instanceof UIForm) {
							context.getViewRoot().getAttributes().put(FORM_RENDERED, FORM_RENDERED);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			invokeOnComponent(context, renderId, callback);
		}
		swapChildren(this, getOriginalViewRoot());
		context.setViewRoot(getOriginalViewRoot());
		if (!getAttributes().containsKey(FORM_RENDERED)) {
			context.getApplication().getViewHandler().writeState(context);
			updateFormAction(context);
		}
		broadCastEvents(context, PhaseId.APPLY_REQUEST_VALUES);
	}

	public void processDecodes(FacesContext context) {
		for (int i = 0; i < getProcessIds().length; i++) {
			String processId = getProcessIds()[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processDecodes(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.APPLY_REQUEST_VALUES);
	}

	public void processUpdates(FacesContext context) {
		for (int i = 0; i < getProcessIds().length; i++) {
			String processId = getProcessIds()[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processUpdates(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.UPDATE_MODEL_VALUES);
	}

	public void processValidators(FacesContext context) {
		for (int i = 0; i < getProcessIds().length; i++) {
			String processId = getProcessIds()[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processValidators(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.PROCESS_VALIDATIONS);
	}

	public void processApplication(FacesContext context) {
		broadCastEvents(context, PhaseId.INVOKE_APPLICATION);
	}

	// subclassing hooks

	protected String[] getProcessIds() {
		if (this.processIds == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			String processIdsParam = context.getExternalContext().getRequestParameterMap().get(PROCESS_IDS_PARAM);
			if (StringUtils.hasText(processIdsParam) && processIdsParam.indexOf(PROCESS_ALL) != -1) {
				this.processIds = new String[] { getOriginalViewRoot().getClientId(context) };
			} else {
				this.processIds = StringUtils.delimitedListToStringArray(processIdsParam, ",", " ");
				this.processIds = removeNestedChildren(context, this.processIds);
			}
		}
		return this.processIds;
	}

	protected String[] getRenderIds() {
		if (this.renderIds == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			this.renderIds = (String[]) this.renderIdsExpr.getValue(context);
			if (this.renderIds == null || this.renderIds.length == 0) {
				this.renderIds = getProcessIds();
			} else {
				this.renderIds = removeNestedChildren(context, this.renderIds);
			}
		}
		return this.renderIds;
	}

	// internal helpers

	private void swapChildren(UIViewRoot source, UIViewRoot target) {
		target.getChildren().addAll(source.getChildren());
		// Create a new list because the children of ViewRoot can change while we're iterating.
		// For example:
		// 1. child is an outputScript component with target="head"
		// 2. child.setParent() fires PostAddToViewEvent
		// 3. MyFaces HtmlScriptRenderer processes the event
		// 3.1. creates javax.faces.Panel for "head"
		// 3.2. adds outputScript to it
		// 3.3. the parent of outputScript is changed from ViewRoot to "head" Panel component
		// 4. outputScript is therefore no longer a child of ViewRoot
		List<UIComponent> children = new ArrayList<UIComponent>(target.getChildren());
		for (int i = 0; i < children.size(); i++) {
			UIComponent child = children.get(i);
			child.setParent(target);
		}
	}

	private void updateFormAction(FacesContext context) {
		ResponseWriter writer = context.getResponseWriter();
		try {
			String formId = findContainingFormId(context);
			if (StringUtils.hasLength(formId)) {
				String script = "dojo.byId('" + formId + "').action = '"
						+ context.getApplication().getViewHandler().getActionURL(context, getViewId()) + "'";
				ResourceHelper.beginScriptBlock(context);
				writer.writeText(script, null);
				ResourceHelper.endScriptBlock(context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String findContainingFormId(FacesContext context) {
		for (int i = 0; i < getRenderIds().length; i++) {
			UIComponent component = context.getViewRoot().findComponent(getRenderIds()[i]);
			Assert.notNull(component, "Component to be rendered with id '" + getRenderIds()[i]
					+ "' could not be found.");
			while (!(component instanceof UIViewRoot)) {
				component = component.getParent();
				if (component instanceof UIForm) {
					return component.getClientId(context);
				}
			}
		}
		return null;
	}

	private String[] removeNestedChildren(FacesContext context, String[] ids) {
		List<String> idList = Arrays.asList(ids);
		final List<String> trimmedIds = new ArrayList<String>(idList);
		for (final ListIterator<String> i = trimmedIds.listIterator(); i.hasNext();) {
			String id = i.next();
			invokeOnComponent(context, id, new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent component) {
					while (!(component.getParent() instanceof UIViewRoot)) {
						component = component.getParent();
						if (trimmedIds.contains(component.getClientId(context))) {
							i.remove();
						}
					}
				}
			});
		}
		return trimmedIds.toArray(new String[trimmedIds.size()]);
	}

	private void broadCastEvents(FacesContext context, PhaseId phaseId) {
		List<FacesEvent> processedEvents = new ArrayList<FacesEvent>();
		if (this.events.size() == 0) {
			return;
		}
		boolean abort = false;
		int phaseIdOrdinal = phaseId.getOrdinal();
		for (FacesEvent event : this.events) {
			int ordinal = event.getPhaseId().getOrdinal();
			if (ordinal == PhaseId.ANY_PHASE.getOrdinal() || ordinal == phaseIdOrdinal) {
				UIComponent source = event.getComponent();
				try {
					processedEvents.add(event);
					source.broadcast(event);
				} catch (AbortProcessingException e) {
					abort = true;
					break;
				}
			}
		}
		if (abort) {
			this.events.clear();
		} else {
			this.events.removeAll(processedEvents);
		}
	}

}
