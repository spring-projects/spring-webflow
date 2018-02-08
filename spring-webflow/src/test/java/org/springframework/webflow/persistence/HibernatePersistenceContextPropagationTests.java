package org.springframework.webflow.persistence;

import javax.sql.DataSource;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.FlowExecutionListener;

public class HibernatePersistenceContextPropagationTests extends AbstractPersistenceContextPropagationTests {

	private HibernateHandler hibernate;

	private HibernateFlowExecutionListener executionListener;

	private int rowCount;

	@Override
	protected void setUpResources(DataSource dataSource) throws Exception {
		hibernate = HibernateHandlerFactory.create(dataSource);
		executionListener = new HibernateFlowExecutionListener(hibernate.getSessionFactory(), hibernate.getTransactionManager());
		rowCount = 1;
	}

	@Override
	protected FlowExecutionListener getListener() {
		return executionListener;
	}

	@Override
	protected void assertSessionNotBound() {
		assertNull(TransactionSynchronizationManager.getResource(hibernate.getSessionFactory()));
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(hibernate.getSessionFactory()));
	}

	@Override
	protected void assertCommitState(boolean insertRow, boolean isCommited) {
		if (insertRow) {
			hibernate.templateSave(new TestBean(rowCount++, "Keith Donald"));
		}
		if (!isCommited) {
			assertEquals("Nothing should be committed yet", 1, getCount());
		} else {
			assertEquals("All rows should be committed", rowCount, getCount());
		}
	}

	@SuppressWarnings("ConstantConditions")
	private int getCount() {
		return getJdbcTemplate().queryForObject("select count(*) from T_BEAN", Integer.class);
	}

}
