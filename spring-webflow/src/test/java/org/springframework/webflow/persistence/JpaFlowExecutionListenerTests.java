package org.springframework.webflow.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public class JpaFlowExecutionListenerTests {

	private EntityManagerFactory entityManagerFactory;

	private JpaFlowExecutionListener jpaListener;

	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setUp() throws Exception {
		DataSource dataSource = getDataSource();
		populateDataBase(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
		entityManagerFactory = getEntityManagerFactory(dataSource);
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		jpaListener = new JpaFlowExecutionListener(entityManagerFactory, tm);
	}

	@Test
	public void testFlowNotAPersistenceContext() {
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		jpaListener.sessionStarting(context, flowSession, null);
		assertSessionNotBound();
	}

	@Test
	public void testFlowCommitsInSingleRequest() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals(1, getCount(), "Table should still only have one row");

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		jpaListener.sessionEnding(context, flowSession, "success", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals(2, getCount(), "Table should only have two rows");
		assertSessionNotBound();
	}

	@SuppressWarnings("ConstantConditions")
	private int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class);
	}

	@Test
	public void testFlowCommitsAfterMultipleRequests() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean1 = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean1);
		assertEquals(1, getCount(), "Table should still only have one row");
		jpaListener.paused(context);
		assertSessionNotBound();

		jpaListener.resuming(context);
		TestBean bean2 = new TestBean(2, "Keith Donald");
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean2);
		assertEquals(1, getCount(), "Table should still only have one row");
		assertSessionBound();

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		jpaListener.sessionEnding(context, flowSession, "success", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals(3, getCount(), "Table should only have three rows");

		assertSessionNotBound();
	}

	@Test
	public void testCancelEndState() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals(1, getCount(), "Table should still only have one row");

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "cancel");
		endState.getAttributes().put("commit", false);
		flowSession.setState(endState);
		jpaListener.sessionEnding(context, flowSession, "cancel", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals(1, getCount(), "Table should only have two rows");
		assertSessionNotBound();
	}

	@Test
	public void testNoCommitAttributeSetOnEndState() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "cancel");
		flowSession.setState(endState);

		jpaListener.sessionEnding(context, flowSession, "cancel", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals(1, getCount(), "Table should only have three rows");

		assertSessionNotBound();
	}

	@Test
	public void testExceptionThrown() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals(1, getCount(), "Table should still only have one row");
		jpaListener.exceptionThrown(context, new FlowExecutionException("bla", "bla", "bla"));
		assertEquals(1, getCount(), "Table should still only have one row");
		assertSessionNotBound();

	}

	@Test
	public void testExceptionThrownWithNothingBound() {
		assertEquals(1, getCount(), "Table should only have one row");
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		assertSessionNotBound();
		jpaListener.exceptionThrown(context, new FlowExecutionException("foo", "bar", "test"));
		assertSessionNotBound();
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
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("test-data.sql", this.getClass()));
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);
		initializer.afterPropertiesSet();
	}

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/persistence/persistence.xml");
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	private void assertSessionNotBound() {
		assertNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

	private void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

}
