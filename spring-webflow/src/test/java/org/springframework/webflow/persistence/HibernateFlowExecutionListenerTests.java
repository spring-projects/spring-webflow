/*
 * Copyright 2004-2014 the original author or authors.
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

import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.persistence.HibernateHandler.SessionCallback;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

import junit.framework.TestCase;

/**
 * Tests for {@link HibernateFlowExecutionListener}
 *
 * @author Ben Hale
 */
public class HibernateFlowExecutionListenerTests extends TestCase {

	private HibernateHandler hibernate;

	private JdbcTemplate jdbcTemplate;

	private HibernateFlowExecutionListener hibernateListener;

	protected void setUp() throws Exception {
		DataSource dataSource = getDataSource();
		populateDataBase(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
		hibernate = HibernateHandlerFactory.create(dataSource);
		hibernateListener = new HibernateFlowExecutionListener(hibernate.getSessionFactory(), hibernate.getTransactionManager());
	}

	public void testSameSession() {
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		// Session created and bound to conversation
		final Session hibSession = (Session) flowSession.getScope().get("persistenceContext");
		assertNotNull("Should have been populated", hibSession);
		hibernateListener.paused(context);
		assertSessionNotBound();

		// Session bound to thread local variable
		hibernateListener.resuming(context);
		assertSessionBound();

		hibernate.templateExecuteWithNativeSession(session -> assertSame("Should have been original instance", hibSession, session));
		hibernateListener.paused(context);
		assertSessionNotBound();
	}

	public void testFlowNotAPersistenceContext() {
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		hibernateListener.sessionStarting(context, flowSession, null);
		assertSessionNotBound();
	}

	public void testFlowCommitsInSingleRequest() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean("Keith Donald");
		hibernate.templateSave(bean);
		assertEquals("Table should still only have one row", 1, getCount());

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		hibernateListener.sessionEnding(context, flowSession, "success", null);
		hibernateListener.sessionEnded(context, flowSession, "success", null);
		assertEquals("Table should only have two rows", 2, getCount());
		assertSessionNotBound();
	}

	@SuppressWarnings("ConstantConditions")
	private int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class);
	}

	public void testFlowCommitsAfterMultipleRequests() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean1 = new TestBean("Keith Donald");
		hibernate.templateSave(bean1);
		assertEquals("Table should still only have one row", 1, getCount());
		hibernateListener.paused(context);
		assertSessionNotBound();

		hibernateListener.resuming(context);
		TestBean bean2 = new TestBean("Keith Donald");
		hibernate.templateSave(bean2);
		assertEquals("Table should still only have one row", 1, getCount());
		assertSessionBound();

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		hibernateListener.sessionEnding(context, flowSession, "success", null);
		hibernateListener.sessionEnded(context, flowSession, "success", null);
		assertEquals("Table should only have three rows", 3, getCount());

		assertSessionNotBound();
	}

	public void testCancelEndState() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean("Keith Donald");
		hibernate.templateSave(bean);
		assertEquals("Table should still only have one row", 1, getCount());

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "cancel");
		endState.getAttributes().put("commit", false);
		flowSession.setState(endState);
		hibernateListener.sessionEnding(context, flowSession, "success", null);
		hibernateListener.sessionEnded(context, flowSession, "cancel", null);
		assertEquals("Table should only have two rows", 1, getCount());
		assertSessionNotBound();
	}

	public void testNoCommitAttributeSetOnEndState() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "cancel");
		flowSession.setState(endState);

		hibernateListener.sessionEnding(context, flowSession, "success", null);
		hibernateListener.sessionEnded(context, flowSession, "cancel", null);
		assertEquals("Table should only have three rows", 1, getCount());

		assertSessionNotBound();
	}

	public void testExceptionThrown() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean1 = new TestBean("Keith Donald");
		hibernate.templateSave(bean1);
		assertEquals("Table should still only have one row", 1, getCount());
		hibernateListener.exceptionThrown(context, new FlowExecutionException("bla", "bla", "bla"));
		assertEquals("Table should still only have one row", 1, getCount());
		assertSessionNotBound();

	}

	public void testExceptionThrownWithNothingBound() {
		assertEquals("Table should only have one row", 1, getCount());
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		assertSessionNotBound();
		hibernateListener.exceptionThrown(context, new FlowExecutionException("foo", "bar", "test"));
		assertSessionNotBound();
	}

	public void testLazyInitializedCollection() {
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		hibernateListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = hibernate.templateGet(TestBean.class, 0L);
		assertFalse("addresses should not be initialized", Hibernate.isInitialized(bean.getAddresses()));
		hibernateListener.paused(context);
		assertFalse("addresses should not be initialized", Hibernate.isInitialized(bean.getAddresses()));
		Hibernate.initialize(bean.getAddresses());
		assertTrue("addresses should be initialized", Hibernate.isInitialized(bean.getAddresses()));
	}

	private DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:hspcl");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	private void populateDataBase(DataSource dataSource) {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("test-data.sql", getClass()));
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);
		initializer.afterPropertiesSet();
	}

	private void assertSessionNotBound() {
		assertNull(TransactionSynchronizationManager.getResource(hibernate.getSessionFactory()));
	}

	private void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(hibernate.getSessionFactory()));
	}

}
