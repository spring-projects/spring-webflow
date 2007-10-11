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
package org.springframework.webflow.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * A {@link FlowExecutionListener} that implements the Flow Managed Persistence Context (FMPC) pattern using the
 * standard Java Persistence API (JPA).
 * <p>
 * This implementation uses standard JPA APIs. The general pattern is as follows:
 * <ul>
 * <li>When a flow execution starts, create a new JPA persistence context and bind it to flow scope.
 * <li>Before processing a flow execution request, expose the flow-scoped persistence context as the "current"
 * persistence context for the current thread.
 * <li>When an existing flow pauses, unbind the persistence context from the current thread.
 * <li>When an existing flow ends, commit the changes made to the persistence context in a transaction if the ending
 * state is a commit state. Then, unbind the context and close it.
 * </ul>
 * 
 * The general data access pattern implemented here is:
 * <ul>
 * <li> Create a new persistence context when a new flow execution with the 'persistenceContext' attribute starts
 * <li> Load some objects into this persistence context
 * <li> Perform edits to those objects over a series of requests into the flow
 * <li> On successful conversation completion, commit and flush those edits to the database, applying a version check if
 * necessary.
 * </ul>
 * 
 * <p>
 * Note: All data access except for the final commit will, by default, be non-transactional. However, a flow may call
 * into a transactional service layer to fetch objects during the conversation in the context of a read-only system
 * transaction if the underlying JPA Transaction Manager supports this. Spring's JPA TransactionManager does support
 * this when working with a Hibernate JPA provider, for example. In that case, Spring will handle setting the FlushMode
 * to MANUAL to ensure any in-progress changes to managed persistent entities are not flushed, while reads of new
 * objects occur transactionally.
 * <p>
 * Care should be taken to prevent premature commits of flow data while the flow is in progress. You would generally not
 * want intermediate flushing to happen, as the nature of a flow implies a transient, isolated resource that can be
 * canceled before it ends. Generally, the only time a read-write transaction should be started is upon successful
 * completion of the flow, triggered by reaching a 'commit' end state.
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.1
 */
public class JpaFlowExecutionListener extends FlowExecutionListenerAdapter {

	private static final String PERSISTENCE_CONTEXT_ATTRIBUTE = "persistenceContext";

	private static final String ENTITY_MANAGER_ATTRIBUTE = "entityManager";

	private EntityManagerFactory entityManagerFactory;

	private TransactionTemplate transactionTemplate;

	/**
	 * Create a new JPA flow execution listener using given JPA Entity Manager factory.
	 * @param entityManagerFactory the entity manager factory to use
	 */
	public JpaFlowExecutionListener(EntityManagerFactory entityManagerFactory,
			PlatformTransactionManager transactionManager) {
		this.entityManagerFactory = entityManagerFactory;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}

	public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input) {
		if (isPersistenceContext(session.getDefinition())) {
			EntityManager em = entityManagerFactory.createEntityManager();
			session.getScope().put(ENTITY_MANAGER_ATTRIBUTE, em);
			bind(em);
		}
	}

	public void paused(RequestContext context) {
		if (isPersistenceContext(context.getActiveFlow())) {
			unbind(getEntityManager(context));
		}
	}

	public void resuming(RequestContext context) {
		if (isPersistenceContext(context.getActiveFlow())) {
			bind(getEntityManager(context));
		}
	}

	public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
		if (isPersistenceContext(session.getDefinition())) {
			final EntityManager em = (EntityManager) session.getScope().remove(ENTITY_MANAGER_ATTRIBUTE);
			Boolean commitStatus = session.getState().getAttributes().getBoolean("commit");
			if (Boolean.TRUE.equals(commitStatus)) {
				// this is a commit end state - start a new transaction that quickly commits
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						// necessary for JTA to enlist the entity manager in the transaction
						try {
							em.joinTransaction();
						} catch (IllegalStateException e) {
							// won't be necessary once Spring 2.0.7 is released
						}
					}
				});
			}
			unbind(em);
			em.close();
		}
	}

	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
		if (isPersistenceContext(context.getActiveFlow())) {
			unbind(getEntityManager(context));
		}
	}

	// internal helpers

	private boolean isPersistenceContext(FlowDefinition flow) {
		return flow.getAttributes().contains(PERSISTENCE_CONTEXT_ATTRIBUTE);
	}

	private EntityManager getEntityManager(RequestContext context) {
		return (EntityManager) context.getFlowScope().get(ENTITY_MANAGER_ATTRIBUTE);
	}

	private void bind(EntityManager em) {
		TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
	}

	private void unbind(EntityManager em) {
		if (TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
			TransactionSynchronizationManager.unbindResource(entityManagerFactory);
		}
	}

}
