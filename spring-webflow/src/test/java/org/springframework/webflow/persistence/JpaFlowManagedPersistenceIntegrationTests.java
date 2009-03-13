package org.springframework.webflow.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.model.FlowModelFlowBuilder;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class JpaFlowManagedPersistenceIntegrationTests extends TestCase {

	private EntityManagerFactory entityManagerFactory;

	private JpaFlowExecutionListener jpaListener;

	private FlowExecution flowExecution;

	protected void setUp() throws Exception {
		DataSource dataSource = getDataSource();
		populateDataBase(dataSource);
		entityManagerFactory = getEntityManagerFactory(dataSource);
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		jpaListener = new JpaFlowExecutionListener(entityManagerFactory, tm);

		ClassPathResource res = new ClassPathResource("flow-managed-persistence.xml", getClass());
		DefaultFlowModelHolder holder = new DefaultFlowModelHolder(new XmlFlowModelBuilder(res));
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder);
		MockFlowBuilderContext context = new MockFlowBuilderContext("foo");
		FlowAssembler assembler = new FlowAssembler(builder, context);
		Flow flow = assembler.assembleFlow();
		context.registerSubflow(flow);
		Flow notManaged = new Flow("notmanaged");
		new EndState(notManaged, "finish");
		context.registerSubflow(notManaged);
		context.registerBean("loadTestBean", new Action() {
			public Event execute(RequestContext context) throws Exception {
				assertSessionBound();
				EntityManager em = (EntityManager) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) em.getReference(TestBean.class, new Integer(0));
				assertNotNull(bean);
				context.getFlowScope().put("testBean", bean);
				return new Event(this, "success");
			}
		});
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(jpaListener));
		flowExecution = factory.createFlowExecution(flow);
	}

	public void testManagedFlowWithManagedSubflow() {
		MockExternalContext context = new MockExternalContext();
		flowExecution.start(null, context);
		context.setEventId("subflow");
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

	private DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:jpa");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	private void populateDataBase(DataSource dataSource) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.createStatement().execute("drop table T_ADDRESS if exists;");
			connection.createStatement().execute("drop table T_BEAN if exists;");
			connection.createStatement().execute(
					"create table T_BEAN (ID integer primary key, NAME varchar(50) not null);");
			connection.createStatement().execute(
					"create table T_ADDRESS (ID integer primary key, BEAN_ID integer, VALUE varchar(50) not null);");
			connection
					.createStatement()
					.execute(
							"alter table T_ADDRESS add constraint FK_BEAN_ADDRESS foreign key (BEAN_ID) references T_BEAN(ID) on delete cascade");
			connection.createStatement().execute("insert into T_BEAN (ID, NAME) values (0, 'Ben Hale');");
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

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) throws Exception {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/persistence/persistence.xml");
		OpenJpaVendorAdapter openJpa = new OpenJpaVendorAdapter();
		factory.setJpaVendorAdapter(openJpa);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	private void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}
}
