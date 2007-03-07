package org.springframework.webflow.jpa;

import org.springframework.webflow.execution.ExtendedFlowExecutionListenerInterceptor;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.ConversationLifecycleListener;
import org.springframework.webflow.execution.ConversationLifecycleListenerAdapter;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.EndState;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
/**
 * Intended for those wating a long session model within webflows. A entity
 * manager is created when the flow begins, and is disconnected and reconnected
 * as necessary throughout the lifecycle of the flow. In particular, it should
 * be thought of as both a
 * {@link org.springframework.webflow.execution.FlowExecutionListener} and
 * {@link WebRequestInterceptor}
 * that binds a
 * entity manager to the current thread for the activate span of a FlowSession.
 * This implementation shares the entity manager between a parent flow and any
 * subflows.
 * </p>
 * To facilitate the long session idea, objects within the flow will be
 * reassociated with the jpa persistence context when the flow is
 * re-activated. Deserialized jpa flow scope
 * objects lose their association to the persistence context, and thus result in
 * problems when you attempt to perform persistence operations (run into
 * non-unique object exceptions, null sessions in persistent collections, and a
 * host of other weird behaviors). There is a basic facility to apply a
 * flushMode to new entity managers that are created, but not as
 * sophisticated as that provided by a HibernateAccessor. Config example:
 *
 * <pre>
 *      	&lt;bean id=&quot;openEntityManagerFlowListener&quot;
 *       			class=&quot;org.springframework.webflow.jpa.OpenEntityManagerPerConversationFlowListener&quot;&gt;
 *       		&lt;constructor ref=&quot;_lifecycleController&quot;/&gt;
 *      	&lt;/bean&gt;
 * </pre>
 *
 * <p>
 * Adapted from Alex Wolfe's post at <a
 * href="http://forum.springframework.org/showthread.php?t=17633">
 * http://forum.springframework.org/showthread.php?t=17633</a>
 *
 * @author Maxim Petrashev
 */
public class OpenEntityManagerPerConversationFlowListener extends ConversationLifecycleListenerAdapter {
    protected final Log _logger = LogFactory.getLog( getClass() );

    private final EntityManagerLifecycleController _lifecycleController;

    /**
     * Attribute name for annotated state which mark end-state and application commit end-state.
     */
    public static final String APPLICATION_TRANSACTION_COMMIT_ATTR_NAME = "applicationTransactionCommit";
    /**
     * Attribute name under wich in conversation scope will be stored entity manager for conversation.
     */
    private static final String ENTITY_MANAGER_ATTR_NAME = OpenEntityManagerPerConversationFlowListener.class.getName() + ".ENTITY_MANAGER";

    protected OpenEntityManagerPerConversationFlowListener(EntityManagerLifecycleController aLifecycleController ) {
        _lifecycleController = aLifecycleController;
    }

    protected EntityManager getEntityManager(RequestContext aContext){
        return (EntityManager) aContext.getConversationScope().get( ENTITY_MANAGER_ATTR_NAME );
    }

    /**
     * Create entity manager for new conversation.
     * @param aNewFlow
     * @param aContext
     */
    public void startingFlow(FlowDefinition aNewFlow, RequestContext aContext) {
        _logger.debug("Creating entity manager for flow: " + aNewFlow.getId());
        EntityManager entityManager = _lifecycleController.create();
        _lifecycleController.activate( entityManager );
        aContext.getConversationScope().put(ENTITY_MANAGER_ATTR_NAME, entityManager);
    }

    /**
     * Try commit application transaction on application transaction commit end state.
     * Clean also all resources that were allocated for conversation entity manager.
     * @param aEndedSession
     * @param aContext
     */
    public void flowEnded(FlowSession aEndedSession, RequestContext aContext) {
        EntityManager entityManager = getEntityManager(aContext);
        try{
            if (isApplicationTransactionCommitState(aEndedSession.getState())) {
                _lifecycleController.flush(entityManager);
            }
        }finally{
            try{
                _lifecycleController.deactivate(entityManager);
            }finally{
                _lifecycleController.close( entityManager );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sessionActive(RequestContext aContext) {
        EntityManager entityManager = getEntityManager(aContext);
        _lifecycleController.activate( entityManager );
    }

    /**
     * Deactivate current entity manager on end of request handling process.
     */
    public void sessionDeactive(RequestContext aContext) {
        FlowExecutionContext flowExecutionContext = aContext.getFlowExecutionContext();
        if( flowExecutionContext.isActive() ) {//todo need CommandManager or request specific lifecycleController
            EntityManager entityManager = getEntityManager(aContext);
            _lifecycleController.deactivate( entityManager );
        } else {
            //entity manager already was closed in flowEnded method
        }
    }

    /**
     * Return is aState application commit state or not. Returns true if aState is EndState and aState is annotated
     * by {@link #APPLICATION_TRANSACTION_COMMIT_ATTR_NAME} attribute.
     */
    protected boolean isApplicationTransactionCommitState(StateDefinition aState) {
        boolean retVal = false;
        if (aState instanceof EndState) {
            retVal = aState.getAttributes().get(APPLICATION_TRANSACTION_COMMIT_ATTR_NAME, "false").equals("true");
        }
        return retVal;
    }

    public static final String CURRENT_ENTITY_MANAGER_KEY_ATTR_NAME = OpenEntityManagerPerConversationFlowListener.class.getName() + ".CURRENT_ENTITY_MANAGER_KEY";
    //todo review exceptionThrown
}
