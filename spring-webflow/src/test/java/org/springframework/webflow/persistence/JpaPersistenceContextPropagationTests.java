package org.springframework.webflow.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.FlowExecutionListener;

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
			assertEquals(1, getCount(), "Nothing should be committed yet");
		} else {
			assertEquals(rowCount, getCount(), "All rows should be committed");
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

	@SuppressWarnings("ConstantConditions")
	private int getCount() {
		return getJdbcTemplate().queryForObject("select count(*) from T_BEAN", Integer.class);
	}

}
