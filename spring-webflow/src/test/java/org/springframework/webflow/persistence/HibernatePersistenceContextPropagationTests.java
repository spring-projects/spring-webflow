package org.springframework.webflow.persistence;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.FlowExecutionListener;

public class HibernatePersistenceContextPropagationTests extends AbstractPersistenceContextPropagationTests {

	private SessionFactory sessionFactory;

	private HibernateTemplate hibernateTemplate;

	private HibernateFlowExecutionListener executionListener;

	private int rowCount;

	@Override
	protected void setUpResources(DataSource dataSource) throws Exception {
		sessionFactory = getSessionFactory(dataSource);
		hibernateTemplate = new HibernateTemplate(sessionFactory);
		hibernateTemplate.setCheckWriteOperations(false);
		HibernateTransactionManager tm = new HibernateTransactionManager(sessionFactory);
		executionListener = new HibernateFlowExecutionListener(sessionFactory, tm);
		rowCount = 1;
	}

	@Override
	protected FlowExecutionListener getListener() {
		return executionListener;
	}

	@Override
	protected void assertSessionNotBound() {
		assertNull(TransactionSynchronizationManager.getResource(sessionFactory));
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(sessionFactory));
	}

	@Override
	protected void assertCommitState(boolean insertRow, boolean isCommited) {
		if (insertRow) {
			hibernateTemplate.save(new TestBean(rowCount++, "Keith Donald"));
		}
		if (!isCommited) {
			assertEquals("Nothing should be committed yet", 1,
					getJdbcTemplate().queryForInt("select count(*) from T_BEAN"));
		} else {
			assertEquals("All rows should be committed", rowCount,
					getJdbcTemplate().queryForInt("select count(*) from T_BEAN"));
		}
	}

	@SuppressWarnings("cast")
	private SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setMappingLocations(new Resource[] {
				new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
				new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml") });
		factory.afterPropertiesSet();
		return (SessionFactory) factory.getObject();
	}

}
