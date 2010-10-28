package org.springframework.webflow.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
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

public class HibernateFlowManagedPersistenceIntegrationTests extends TestCase {

	private SessionFactory sessionFactory;

	private HibernateTemplate hibernateTemplate;

	private HibernateFlowExecutionListener hibernateListener;

	private FlowExecution flowExecution;

	protected void setUp() throws Exception {
		DataSource dataSource = getDataSource();
		populateDataBase(dataSource);
		sessionFactory = getSessionFactory(dataSource);
		hibernateTemplate = new HibernateTemplate(sessionFactory);
		hibernateTemplate.setCheckWriteOperations(false);
		HibernateTransactionManager tm = new HibernateTransactionManager(sessionFactory);
		hibernateListener = new HibernateFlowExecutionListener(sessionFactory, tm);

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
				Session session = (Session) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) session.get(TestBean.class, new Long(0));
				assertNotNull(bean);
				context.getFlowScope().put("testBean", bean);
				return new Event(this, "success");
			}
		});
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(hibernateListener));
		flowExecution = factory.createFlowExecution(flow);
	}

	public void testFlowWithSubflow() {
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

	private SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setMappingLocations(new Resource[] {
				new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
				new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml") });
		factory.afterPropertiesSet();
		return (SessionFactory) factory.getObject();
	}

	private void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(sessionFactory));
	}
}
