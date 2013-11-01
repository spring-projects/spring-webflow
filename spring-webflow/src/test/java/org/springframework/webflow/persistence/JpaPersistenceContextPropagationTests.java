package org.springframework.webflow.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.FlowExecutionListener;

@SuppressWarnings("deprecation")
public class JpaPersistenceContextPropagationTests extends AbstractPersistenceContextPropagationTests {

	private EntityManagerFactory entityManagerFactory;

	private JpaFlowExecutionListener executionListener;

	private int rowCount;

	@Override
	protected void setUpResources(DataSource dataSource) throws Exception {
		entityManagerFactory = getEntityManagerFactory(dataSource);
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		executionListener = new JpaFlowExecutionListener(entityManagerFactory, tm);
		rowCount = 1;
	}

	@Override
	protected FlowExecutionListener getListener() {
		return executionListener;
	}

	@Override
	protected void assertSessionNotBound() {
		assertNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

	@Override
	protected void assertCommitState(boolean insertRow, boolean isCommited) {
		if (insertRow) {
			EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
			em.persist(new TestBean(rowCount++, "Keith Donald"));
		}
		if (!isCommited) {
			assertEquals("Nothing should be committed yet", 1,
					getJdbcTemplate().queryForInt("select count(*) from T_BEAN"));
		} else {
			assertEquals("All rows should be committed", rowCount,
					getJdbcTemplate().queryForInt("select count(*) from T_BEAN"));
		}
	}

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) throws Exception {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/persistence/persistence.xml");
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

}
