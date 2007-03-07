package org.springframework.webflow.execution;

import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.core.collection.MutableAttributeMap;

/**
 * Listener interface for callback notification about phases in during conversation
 *
 * @author Maxim Petrashev
 */
public interface ConversationLifecycleListener {
    /**
     * Invoked when a flow is launched. The launching flow is not active.
     *
     * @param aNewFlow The launching flow
     * @param aContext The request context
     */

    void startingFlow(FlowDefinition aNewFlow, RequestContext aContext);

    void flowStarted(RequestContext aContext, MutableAttributeMap aInput);
    /**
     * Invoked when the root flow session has ended.
     *
     * @param aEndedSession The ended session
     * @param aContext      The request context
     */
    void flowEnded(FlowSession aEndedSession, RequestContext aContext);

    void endingFlow(RequestContext aContext, MutableAttributeMap aOutput);
    /**
     * Invoked in parent flow before spawning in subflow but after input mapping has been happened.
     * The child flow session is not available to implementations of this method because the flow session for
     * the child flow has not yet started. <p/> If you need to add items to the
     * subflow scope, put them in aInput.
     *
     * @param aParentSession The active parent flow session
     * @param aChild         The child flow
     * @param aContext       The request context
     * @param aInput         The input map
     */
    void startingSubflow(FlowSession aParentSession, FlowDefinition aChild, RequestContext aContext
            , MutableAttributeMap aInput);
    /**
     * Invoked when a subflow is launched. The child flow session is not
     * available to implementations of this method because the flow session for
     * the child flow has not yet started. <p/> If you need to add items to the
     * subflow scope, put them in aInput.
     *
     * @param aContext       The request context
     * @param aInput         The input map
     */
    void subflowStarted(RequestContext aContext , MutableAttributeMap aInput);
    /**
     * Invoked in subflow flow before spawning back to parent flot but after output mapping has been happened.
     * The parent flow session is not available to implementations of this method because the flow session for
     * the parent flow has not yet continued. <p/> If you need to add items to the
     * parent flow scope, put them in aOutput.
     *
     * @param aContext       The request context
     * @param aOutput         The input map
     */
    void endingSubflow(RequestContext aContext , MutableAttributeMap aOutput);
    /**
     * Invoked when a parent flow is launched back. The child flow session is not
     * available to implementations of this method because the flow session for
     * the child flow has not yet started. <p/> If you need to add items to the
     * subflow scope, put them in aInput.
     *
     * @param aParentSession The active parent flow session
     * @param aChild         The child flow
     * @param aContext       The request context
     * @param aOutput         The input map
     */
    void subflowEnded(FlowSession aParentSession, FlowDefinition aChild, RequestContext aContext
            , MutableAttributeMap aOutput);
    /**
     * The currently executing flow session is active. This occurs after the
     * first event for the executing request has been signaled or resume event has been happended. Invoked once per
     * request and provides access to the active flow session prior to any
     * actions being performed.
     *
     * @param aContext The request context
     */
    void sessionActive(RequestContext aContext);
    /**
     * The currently executing flow session is active. This occurs after the
     * first event for the executing request has been signaled or resume event has been happended. Invoked once per
     * request and provides access to the active flow session prior to any
     * actions being performed.
     *
     * @param aContext The request context
     */
    void sessionDeactive(RequestContext aContext);

}
