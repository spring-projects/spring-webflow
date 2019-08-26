package org.springframework.webflow.persistence;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
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

public abstract class AbstractFlowManagedPersistenceIntegrationTests {

	private FlowExecutionListener persistenceListener;

	private FlowExecution flowExecution;

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	@BeforeEach
	public void setUp() throws Exception {
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

	@Test
	public void testFlowWithSubflow() {
		MockExternalContext context = new MockExternalContext();
		flowExecution.start(null, context);
		context.setEventId("managed");
		flowExecution.resume(context);
		context.setEventId("finish");
		flowExecution.resume(context);
	}

	@Test
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
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("test-data.sql", this.getClass()));
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(getDataSource());
		initializer.setDatabasePopulator(databasePopulator);
		initializer.afterPropertiesSet();
	}

}
