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
package org.springframework.webflow.support.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * A {@link FlowExecutionListener} that implements the Session-per-Conversation using the standard Java Persistence API
 * (JPA).
 * <p>
 * This implementation uses standard JPA APIs. The general pattern is as follows:
 * <ul>
 * <li>When a flow execution starts, create a new JPA persistence context and bind it to conversation scope.
 * <li>Before processing a flow execution request, expose the conversationally-bound persistence context as the
 * "current" persistence context for the current thread.
 * <li>When an existing flow pauses, unbind the persistence context from the current thread.
 * <li>When an existing flow ends, unbind persistence context and and close it.
 * </ul>
 * 
 * The general data access pattern implemented here is:
 * <ul>
 * <li> Create a new persistence context when a new conversation (e.g. edit session) starts
 * <li> Load some objects non-transactionally using this persistence context
 * <li> Perform edits to those objects over a series of conversational requests
 * <li> On successful conversation completion, commit and flush those edits to the database
 * </ul>
 * 
 * Note: care should be taken to ensure at the service-layer that all data access in a conversation occurs
 * non-transactionally until the final "commit" request to ensure isolation of intermediate object changes made during
 * the course of the conversation. This care should be taken because, by default, JPA will always flush upon transaction
 * commit, resulting in changes in the object model being synchronized with the database at that time. You would
 * generally not want such intermediate flushing to happen, as the nature of a conversation implies a transient resource
 * that can be canceled.
 * 
 * @author Keith Donald
 * @since 1.1
 */
public class JpaSessionPerConversationListener extends FlowExecutionListenerAdapter {

    private static final String ENTITY_MANAGER_ATTRIBUTE = "jpa.entityManager";

    private EntityManagerFactory entityManagerFactory;

    /**
     * Create a new Session-per-Conversation listener using given JPA Entity Manager factory.
     * @param entityManagerFactory the entity manager factory to use
     */
    public JpaSessionPerConversationListener(EntityManagerFactory entityManagerFactory) {
	this.entityManagerFactory = entityManagerFactory;
    }

    public void sessionCreated(RequestContext context, FlowSession session) {
	if (session.isRoot()) {
	    EntityManager em = entityManagerFactory.createEntityManager();
	    context.getConversationScope().put(ENTITY_MANAGER_ATTRIBUTE, em);
	    bind(em);
	}
    }

    public void resumed(RequestContext context) {
	bind(getEntityManager(context));
    }

    public void paused(RequestContext context, ViewSelection selectedView) {
	unbind(getEntityManager(context));
    }

    public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
	if (session.isRoot()) {
	    EntityManager em = (EntityManager) context.getConversationScope().remove(ENTITY_MANAGER_ATTRIBUTE);
	    unbind(em);
	    em.close();
	}
    }

    public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
	unbind(getEntityManager(context));
    }

    // internal helpers

    private EntityManager getEntityManager(RequestContext context) {
	return (EntityManager) context.getConversationScope().get(ENTITY_MANAGER_ATTRIBUTE);
    }

    private void bind(EntityManager em) {
	TransactionSynchronizationManager.bindResource(em, new EntityManagerHolder(em));
    }

    private void unbind(EntityManager em) {
	TransactionSynchronizationManager.unbindResource(em);
    }
}
