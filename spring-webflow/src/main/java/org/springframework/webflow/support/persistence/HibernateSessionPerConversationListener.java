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

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * A {@link FlowExecutionListener} that implements the Hibernate
 * Session-per-Conversation pattern as described in Java Persistence with
 * Hibernate (chapter 11).
 * <p>
 * This implementation uses raw Hibernate APIs and binds the current session to
 * the thread-local location identified by Spring's
 * <code>HibernateTransactionManager</code>.
 * <p>
 * This listener assumes that you are accessing Hibernate via Spring support
 * such as HibernateTemplate or the LocalSessionFactoryBean. If not, Hibernate
 * data access code will not participate in the proper transaction.
 * <p>
 * Note that when accessing service layer methods with Spring managed
 * transactions, those transaction should have {@link Propagation#REQUIRED}
 * semantics.  Anything else defeats the purpose of holding the transaction open
 * until the end of the session.
 * 
 * @author Ben Hale
 * @since 1.1
 */
public class HibernateSessionPerConversationListener extends FlowExecutionListenerAdapter {

	private static final String HIBERNATE_SESSION = "hibernate.session";

	private SessionFactory sessionFactory;

	/**
	 * Create a new Session-per-Conversation listener using giving Hibernate session
	 * factory.
	 * @param sessionFactory the session factory to use
	 */
	public HibernateSessionPerConversationListener(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * When a {@link FlowSession} is created a Hibernate <code>Session</code>
	 * is opened and put into MANUAL flush mode. From there it is bound to the
	 * conversation scope of this {@link FlowSession}. Since
	 * {@link #resumed(RequestContext)} will not be called on start of the
	 * session, this method also binds the session to the thread-local location.
	 * This behavior will only be exhibited on the root flow in a {@link FlowExecution}.
	 */
	public void sessionCreated(RequestContext context, FlowSession session) {
		if (session.isRoot()) {
			Session hibSession = createSession(context);
			bindSession(hibSession);
		}
	}

	/**
	 * When a {@link FlowExecution} is resumed, the conversationally scoped
	 * <code>Session</code> is bound to the thread-local location and a
	 * transaction is opened.
	 */
	public void resumed(RequestContext context) {
		Session hibSession = getHibernateSession(context);
		bindSession(hibSession);
	}

	/**
	 * When a {@link FlowExecution} is paused the conversationally scoped
	 * <code>Session</code> is unbound from the thread-local location and the
	 * transaction is committed. The transaction that is closed is a Hibernate
	 * transaction and with a FlushMode of MANUAL will not flush anything to the
	 * database.
	 */
	public void paused(RequestContext context, ViewSelection selectedView) {
		Session hibSession = getHibernateSession(context);
		unBindSession(hibSession);
	}

	/**
	 * When a {@link FlowSession} is destroyed all changes are flushed to the
	 * database, the current transaction committed, and the Hibernate
	 * <code>Session</code> is closed. Since
	 * {@link #paused(RequestContext, ViewSelection)} will not be called on the
	 * ending of this session, this method also unbinds the session from the
	 * thread-local location. This behavior will only be exhibited on the root
	 * flow in a {@link FlowExecution}.
	 */
	public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
		if (session.isRoot()) {
			Session hibSession = (Session) context.getConversationScope().remove(HIBERNATE_SESSION);
			hibSession.flush();
			unBindSession(hibSession);
			destroySession(hibSession);
		}
	}
	
	/**
	 * When an exception is thrown from a {@link FlowExecution}, the
	 * conversationally scoped <code>Session</code> is unbound from the
	 * thread-local location and the transaction is committed. The
	 * transaction that is closed is a Hibernate transaction and with a
	 * FlushMode of MANUAL will not flush anything to the database.
	 */
	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
		Session hibSession = getHibernateSession(context);
		unBindSession(hibSession);
	}
	
	// internal helpers

	private Session createSession(RequestContext context) {
		Session hibSession = sessionFactory.openSession();
		hibSession.setFlushMode(FlushMode.MANUAL);
		context.getConversationScope().put(HIBERNATE_SESSION, hibSession);
		return hibSession;
	}

	private void destroySession(Session hibSession) {
		hibSession.close();
	}

	private Session getHibernateSession(RequestContext context) {
		return (Session) context.getConversationScope().get(HIBERNATE_SESSION);
	}

	private void bindSession(Session hibSession) {
		Transaction hibTx = hibSession.beginTransaction();
		SessionHolder sessionHolder = new SessionHolder(hibSession);
		sessionHolder.setTransaction(hibTx);
		TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
	}

	private void unBindSession(Session hibSession) {
		hibSession.getTransaction().commit();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
	}
}
