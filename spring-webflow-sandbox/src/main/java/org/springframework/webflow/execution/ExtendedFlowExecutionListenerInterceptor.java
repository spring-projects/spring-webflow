package org.springframework.webflow.execution;

import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A listener/interceptor whose purpose is to adapt a HandlerInterceptor and
 * FlowExecutionListener to provide expanded coverage at the beginning and
 * ending of a request coming into a flow controller. The idea is to signal when
 * a flow starts/activates, then signal when it has deactivated or ended. In
 * addition, it should be signaled when a subflow starts/activates and
 * ends/deactivates.
 * </p>
 * Adapted from Alex Wolfe's post at <a
 * href="http://forum.springframework.org/showthread.php?t=17633">
 * http://forum.springframework.org/showthread.php?t=17633</a>
 *
 *
 * @author Andrew Ebaugh
 * @author Maxim Petrashev
 */
public class ExtendedFlowExecutionListenerInterceptor extends FlowExecutionListenerAdapter {
    protected final Log _logger = LogFactory.getLog(getClass());

    private static String FIRST_EVENT_SIGNALED = ExtendedFlowExecutionListenerInterceptor.class.getName() + ".FIRST_EVENT_SIGNALED";
    private static String CURRENT_SESSION_ENDED = ExtendedFlowExecutionListenerInterceptor.class.getName() + ".CURRENT_SESSION_ENDED";


    public ExtendedFlowExecutionListenerInterceptor(ConversationLifecycleListener aConversationLifecycleListener) {
        _conversationLifecycleListener = aConversationLifecycleListener;
    }

    private ConversationLifecycleListener _conversationLifecycleListener;

    /**
     * Called when any client request is submitted to manipulate this flow
     * execution. Sets a flag in the request scope that is activated when the
     * first state is entered during this request. This flag is required in
     * order to trigger execution of the {@link ConversationLifecycleListener#sessionActive(RequestContext)}
     * method.
     *
     * @param aContext The request aContext
     */
    public final void requestSubmitted(RequestContext aContext) {
        aContext.getRequestScope().put(FIRST_EVENT_SIGNALED, Boolean.FALSE);
        aContext.getRequestScope().put(CURRENT_SESSION_ENDED, Boolean.FALSE);
    }

    public final void eventSignaled(RequestContext aContext, Event aEvent) {
        signalAction(aContext);
    }

    public final void stateEntered(RequestContext aContext, StateDefinition aPreviousState, StateDefinition aState) {
        signalAction(aContext);
    }

    /**
     * Called when an event is signaled, or a state is entered, but before any
     * potential transition or actions occurs. If the action signaled is the
     * first for the request, then the {@link ConversationLifecycleListener#sessionActive(RequestContext)}
     * method is invoked. It is necessary that this be called prior to any state
     * transitions to provide listeners coverage of any state
     * exit/transition/entry actions.
     *
     * @param aContext The request aContext
     */
    private void signalAction(RequestContext aContext) {
        if (!firstEventSignaled(aContext)) {
            aContext.getRequestScope().put(FIRST_EVENT_SIGNALED, Boolean.TRUE);
            _conversationLifecycleListener.sessionActive(aContext);
        }
    }

    /**
     * A aFlow session is starting. This method invokes
     * {@link ConversationLifecycleListener#startingFlow(FlowDefinition, RequestContext)} if the launching aFlow
     * session is the root aFlow. Otherwise, the
     * {@link ConversationLifecycleListener#startingSubflow(FlowSession, FlowDefinition, RequestContext,MutableAttributeMap)} method is
     * invoked.
     *
     * @param aContext The request aContext
     * @param aInput
     * @throws EnterStateVetoException
     *          The start state transition was not allowed
     * @param aFlowDefinition
     */
    public final void sessionStarting(RequestContext aContext, FlowDefinition aFlowDefinition, MutableAttributeMap aInput) {
        aContext.getRequestScope().put(FIRST_EVENT_SIGNALED, Boolean.TRUE);
        aContext.getRequestScope().put(CURRENT_SESSION_ENDED, Boolean.FALSE);
        FlowExecutionContext executionContext = aContext.getFlowExecutionContext();
        FlowSession activeSession = executionContext.isActive() ? executionContext .getActiveSession() : null;
        // when starting in default subflow start state?
        if (activeSession != null
                && SubflowState.class.isInstance(activeSession.getState())) {
            aFlowDefinition = ((SubflowState) activeSession.getState()).getSubflow();
        }

        if (activeSession == null) {
            _conversationLifecycleListener.startingFlow(aFlowDefinition, aContext);
        } else {
            _conversationLifecycleListener.startingSubflow(activeSession, aFlowDefinition, aContext, null);
        }
    }

    public void sessionStarted(RequestContext aContext, FlowSession aSession) {
        if( aSession.isRoot() ) {
            _conversationLifecycleListener.flowStarted(aContext, null );
        } else {
            _conversationLifecycleListener.subflowStarted(aContext,null);
        }
    }

    public void sessionEnding(RequestContext aContext, FlowSession aSession, MutableAttributeMap aOutput) {
        if( aSession.isRoot() ) {
            _conversationLifecycleListener.endingFlow(aContext, null );
        } else {
            _conversationLifecycleListener.endingSubflow(aContext,null);
        }
    }

    public void requestProcessed(RequestContext aContext) {
        FlowExecutionContext executionContext = aContext.getFlowExecutionContext();
        if( executionContext.isActive() ) {
            _conversationLifecycleListener.sessionDeactive(aContext );
        }
    }

    /**
     * Called when a flow execution session ends. If the ended session was the
     * root session of the flow execution, the
     * {@link ConversationLifecycleListener#flowEnded(FlowSession, RequestContext)} method is
     * invoked. <p/> If the ended session was not the root session, then the
     * {@link ConversationLifecycleListener#subflowEnded(FlowSession, FlowDefinition, RequestContext, MutableAttributeMap)} }
     * method is invoked. Prior to this, and in either case, the
     * {@link ConversationLifecycleListener#sessionDeactive(RequestContext)} method is invoked.
     *
     * @param aContext      The source of the event
     * @param aEndedSession The ended FlowSession
     */
    public final void sessionEnded(RequestContext aContext, FlowSession aEndedSession, AttributeMap aSessionOutput) {
        FlowExecutionContext exeCtx = aContext.getFlowExecutionContext();
        FlowSession newSession = exeCtx.isActive() ? exeCtx.getActiveSession() : null;
        if (aEndedSession != null) {
            try{
                if (aEndedSession.isRoot()) {
                    _conversationLifecycleListener.flowEnded(aEndedSession, aContext);
                } else {
                    _conversationLifecycleListener.subflowEnded(newSession, aEndedSession.getDefinition(), aContext, null);
                }
            } catch( RuntimeException e ){  //todo to think which base exception need wrap
                String message = "Can't end session";
                if( _logger.isWarnEnabled() ) {
                    _logger.warn(message,e);
                }
                throw new FlowExecutionException( exeCtx.getDefinition().getId()
                        , newSession != null ? newSession.getState().getId() : aEndedSession.getState().getId()
                        , message, e);
            }
        }
        aContext.getRequestScope().put(CURRENT_SESSION_ENDED, Boolean.TRUE);
    }

    /**
     * Determine whether the current request has handled an event.
     *
     * @param aContext The request context
     * @return <code>true</code> if an event has already been signaled during
     *         the current request, otherwise <code>false</code>
     */
    private boolean firstEventSignaled(RequestContext aContext) {
        return aContext.getRequestScope().get(FIRST_EVENT_SIGNALED).equals(Boolean.TRUE);
    }
    public void resumed(RequestContext context) {
        signalAction(context);
    }
}
