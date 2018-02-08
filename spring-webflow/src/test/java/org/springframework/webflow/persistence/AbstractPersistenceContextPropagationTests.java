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
package org.springframework.webflow.persistence;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public abstract class AbstractPersistenceContextPropagationTests extends TestCase {

	private MockRequestContext requestContext;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	protected final void setUp() throws Exception {
		requestContext = new MockRequestContext();
		DataSource dataSource = createDataSource();
		jdbcTemplate = new JdbcTemplate(dataSource);
		populateDataBase(dataSource);
		setUpResources(dataSource);
	}

	public void testSessionStarting_NoPc_ParentPc() {
		MockFlowSession parentSession = newFlowSession(true, null);
		MockFlowSession childSession = newFlowSession(false, parentSession);

		getListener().sessionStarting(new MockRequestContext(), parentSession, null);
		assertSessionBound();
		assertSessionInScope(parentSession);

		getListener().sessionStarting(new MockRequestContext(), childSession, null);
		assertSessionNotBound();
		assertSessionNotInScope(childSession);
	}

	public void testSessionStarting_Pc_ParentPc() {
		MockFlowSession parentSession = newFlowSession(true, null);
		MockFlowSession childSession = newFlowSession(true, parentSession);

		getListener().sessionStarting(new MockRequestContext(), parentSession, null);
		assertSessionBound();
		assertSessionInScope(parentSession);

		getListener().sessionStarting(new MockRequestContext(), childSession, null);
		assertSessionBound();
		assertSessionInScope(childSession);
		assertSame("Parent PersistenceContext should be re-used", parentSession.getScope().get("persistenceContext"),
				childSession.getScope().get("persistenceContext"));
	}

	public void testSessionEnd_Pc_NoParentPc() {
		MockFlowSession parentSession = newFlowSession(false, null);
		MockFlowSession childSession = newFlowSession(true, parentSession);

		getListener().sessionStarting(requestContext, parentSession, null);
		getListener().sessionStarting(requestContext, childSession, null);

		assertCommitState(true, false);

		requestContext.setActiveSession(childSession);

		// Session ending commits, unbinds/closes PersistenceContext
		getListener().sessionEnding(requestContext, childSession, "success", null);
		assertSessionNotBound();

		// sessionEnded has no effect
		getListener().sessionEnded(requestContext, childSession, "success", null);
		assertSessionNotBound();
		assertCommitState(false, true);
	}

	public void testSessionEnd_Pc_ParentPc() {
		MockFlowSession parentSession = newFlowSession(true, null);
		MockFlowSession childSession = newFlowSession(true, parentSession);

		getListener().sessionStarting(requestContext, parentSession, null);
		getListener().sessionStarting(requestContext, childSession, null);

		assertCommitState(true, false);

		requestContext.setActiveSession(childSession);

		// sessionEnding is a no-op
		getListener().sessionEnding(requestContext, childSession, "success", null);
		assertSessionBound();
		assertCommitState(true, false);

		// sessionEnded binds Parent PersistenceContext
		getListener().sessionEnded(requestContext, childSession, "success", null);
		assertSessionBound();
	}

	private MockFlowSession newFlowSession(boolean persistenceContext, FlowSession parent) {
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.setParent(parent);
		if (persistenceContext) {
			flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		}
		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);
		return flowSession;
	}

	private DataSource createDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:hspcl");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	private void populateDataBase(DataSource dataSource) {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("test-data.sql", this.getClass()));
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);
		initializer.afterPropertiesSet();
	}

	/* methods for subclasses */

	protected abstract void setUpResources(DataSource dataSource) throws Exception;

	protected abstract FlowExecutionListener getListener();

	protected abstract void assertSessionBound();

	protected abstract void assertSessionNotBound();

	protected abstract void assertCommitState(boolean b, boolean c);

	/* private helper methods */

	private void assertSessionInScope(FlowSession session) {
		assertTrue(session.getScope().contains("persistenceContext"));
	}

	private void assertSessionNotInScope(FlowSession session) {
		assertFalse(session.getScope().contains("persistenceContext"));
	}

}
