package org.springframework.webflow.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import junit.framework.TestCase;

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

@SuppressWarnings("deprecation")
public class JpaFlowExecutionListenerTests extends TestCase {

	private EntityManagerFactory entityManagerFactory;

	private JpaFlowExecutionListener jpaListener;

	private JdbcTemplate jdbcTemplate;

	public void testTemp() {

	}

	protected void setUp() throws Exception {
		DataSource dataSource = getDataSource();
		populateDataBase(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
		entityManagerFactory = getEntityManagerFactory(dataSource);
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		jpaListener = new JpaFlowExecutionListener(entityManagerFactory, tm);
	}

	public void testFlowNotAPersistenceContext() {
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		jpaListener.sessionStarting(context, flowSession, null);
		assertSessionNotBound();
	}

	public void testFlowCommitsInSingleRequest() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		jpaListener.sessionEnding(context, flowSession, "success", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals("Table should only have two rows", 2, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		assertSessionNotBound();
	}

	public void testFlowCommitsAfterMultipleRequests() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean1 = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean1);
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		jpaListener.paused(context);
		assertSessionNotBound();

		jpaListener.resuming(context);
		TestBean bean2 = new TestBean(2, "Keith Donald");
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean2);
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		assertSessionBound();

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
		endState.getAttributes().put("commit", true);
		flowSession.setState(endState);

		jpaListener.sessionEnding(context, flowSession, "success", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals("Table should only have three rows", 3, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));

		assertSessionNotBound();
	}

	public void testCancelEndState() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));

		EndState endState = new EndState(flowSession.getDefinitionInternal(), "cancel");
		endState.getAttributes().put("commit", false);
		flowSession.setState(endState);
		jpaListener.sessionEnding(context, flowSession, "cancel", null);
		jpaListener.sessionEnded(context, flowSession, "success", null);
		assertEquals("Table should only have two rows", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		assertSessionNotBound();
	}

	public void testNoCommitAttributeSetOnEndState() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
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
		assertEquals("Table should only have three rows", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));

		assertSessionNotBound();
	}

	public void testExceptionThrown() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		MockRequestContext context = new MockRequestContext();
		MockFlowSession flowSession = new MockFlowSession();
		flowSession.getDefinition().getAttributes().put("persistenceContext", "true");
		jpaListener.sessionStarting(context, flowSession, null);
		context.setActiveSession(flowSession);
		assertSessionBound();

		TestBean bean = new TestBean(1, "Keith Donald");
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
		em.persist(bean);
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		jpaListener.exceptionThrown(context, new FlowExecutionException("bla", "bla", "bla"));
		assertEquals("Table should still only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
		assertSessionNotBound();

	}

	public void testExceptionThrownWithNothingBound() {
		assertEquals("Table should only have one row", 1, (int)jdbcTemplate.queryForObject("select count(*) from T_BEAN", Integer.class));
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

	private void populateDataBase(DataSource dataSource) throws Exception {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("test-data.sql", this.getClass()));
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);
		initializer.afterPropertiesSet();
	}

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) throws Exception {
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
