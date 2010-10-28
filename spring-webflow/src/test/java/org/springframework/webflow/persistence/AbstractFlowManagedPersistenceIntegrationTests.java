package org.springframework.webflow.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.model.FlowModelFlowBuilder;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

public abstract class AbstractFlowManagedPersistenceIntegrationTests extends TestCase {

	private FlowExecutionListener persistenceListener;

	private FlowExecution flowExecution;

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	protected void setUp() throws Exception {
		initDataSource();
		populateDataBase();
		persistenceListener = createFlowExecutionListener();

		ClassPathResource resource = new ClassPathResource("managed-root-flow.xml", getClass());
		DefaultFlowModelHolder holder = new DefaultFlowModelHolder(new XmlFlowModelBuilder(resource));
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder);
		MockFlowBuilderContext context = new MockFlowBuilderContext("managed-root-flow");
		FlowAssembler assembler = new FlowAssembler(builder, context);
		Flow rootFlow = assembler.assembleFlow();

		ClassPathResource childFlowResource = new ClassPathResource("managed-child-flow.xml", getClass());
		DefaultFlowModelHolder childFlowHolder = new DefaultFlowModelHolder(new XmlFlowModelBuilder(childFlowResource));
		FlowModelFlowBuilder childFlowBuilder = new FlowModelFlowBuilder(childFlowHolder);
		MockFlowBuilderContext childFlowContext = new MockFlowBuilderContext("managed-child-flow");
		FlowAssembler childFlowAssembler = new FlowAssembler(childFlowBuilder, childFlowContext);
		Flow childFlow = childFlowAssembler.assembleFlow();

		Flow notManaged = new Flow("notmanaged-child-flow");
		new EndState(notManaged, "finish");

		context.registerSubflow(childFlow);
		context.registerSubflow(notManaged);

		Action incrementCountAction = incrementCountAction();
		context.registerBean("incrementCountAction", incrementCountAction);
		childFlowContext.registerBean("incrementCountAction", incrementCountAction);

		Object assertCountAction = assertCountAction();
		context.registerBean("assertCountAction", assertCountAction);
		childFlowContext.registerBean("assertCountAction", assertCountAction);

		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(persistenceListener));
		flowExecution = factory.createFlowExecution(rootFlow);
	}

	public void testFlowWithSubflow() {
		MockExternalContext context = new MockExternalContext();
		flowExecution.start(null, context);
		context.setEventId("managed");
		flowExecution.resume(context);
		context.setEventId("finish");
		flowExecution.resume(context);
	}

	public void testManagedFlowWithUnmanagedSubflow() {
		MockExternalContext context = new MockExternalContext();
		flowExecution.start(null, context);
		context.setEventId("notmanaged");
		flowExecution.resume(context);
	}

	/* Methods for subclasses */

	protected abstract FlowExecutionListener createFlowExecutionListener() throws Exception;

	protected abstract Action incrementCountAction();

	protected abstract Object assertCountAction();

	protected abstract void assertSessionBound();

	/* private helper methods */

	private void initDataSource() {
		DriverManagerDataSource dmds = new DriverManagerDataSource();
		dmds.setDriverClassName("org.hsqldb.jdbcDriver");
		dmds.setUrl("jdbc:hsqldb:mem:jpa");
		dmds.setUsername("sa");
		dmds.setPassword("");
		dataSource = dmds;
	}

	private void populateDataBase() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.createStatement().execute("drop table T_ADDRESS if exists;");
			connection.createStatement().execute("drop table T_BEAN if exists;");
			connection.createStatement().execute(
					"create table T_BEAN (ID integer primary key, NAME varchar(50) not null, COUNTER integer);");
			connection.createStatement().execute(
					"create table T_ADDRESS (ID integer primary key, BEAN_ID integer, VALUE varchar(50) not null);");
			connection
					.createStatement()
					.execute(
							"alter table T_ADDRESS add constraint FK_BEAN_ADDRESS foreign key (BEAN_ID) references T_BEAN(ID) on delete cascade");
			connection.createStatement().execute("insert into T_BEAN (ID, NAME, COUNTER) values (0, 'Ben Hale',0);");
			connection.createStatement().execute(
					"insert into T_ADDRESS (ID, BEAN_ID, VALUE) values (0, 0, 'Melbourne')");
		} catch (SQLException e) {
			throw new RuntimeException("SQL exception occurred acquiring connection", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}
