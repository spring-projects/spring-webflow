package org.springframework.webflow.execution;

import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.core.collection.MutableAttributeMap;

/**
 *
 * @author Maxim Petrashev
 */
public abstract class ConversationLifecycleListenerAdapter implements ConversationLifecycleListener {

    public void startingFlow(FlowDefinition aNewFlow, RequestContext aContext) {
    }

    public void flowStarted(RequestContext aContext, MutableAttributeMap aInput) {
    }

    public void flowEnded(FlowSession aEndedSession, RequestContext aContext) {
    }

    public void endingFlow(RequestContext aContext, MutableAttributeMap aOutput) {
    }

    public void startingSubflow(FlowSession aParentSession, FlowDefinition aChild, RequestContext aContext, MutableAttributeMap aInput) {
    }

    public void subflowStarted(RequestContext aContext, MutableAttributeMap aInput) {
    }

    public void subflowStarted(FlowSession aParentSession, FlowDefinition aChild, RequestContext aContext, MutableAttributeMap aInput) {
    }

    public void endingSubflow(RequestContext aContext, MutableAttributeMap aOutput) {
    }

    public void subflowEnded(FlowSession aParentSession, FlowDefinition aChild, RequestContext aContext, MutableAttributeMap aOutput) {
    }

    public void sessionActive(RequestContext aContext) {
    }

    public void sessionDeactive(RequestContext aContext) {
    }
}
