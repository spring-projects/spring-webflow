package org.springframework.webflow.persistence;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;

public class HibernateFlowManagedPersistenceIntegrationTests extends AbstractFlowManagedPersistenceIntegrationTests {

	private SessionFactory sessionFactory;

	@Override
	protected FlowExecutionListener createFlowExecutionListener() throws Exception {
		sessionFactory = getSessionFactory(getDataSource());
		HibernateTransactionManager tm = new HibernateTransactionManager(sessionFactory);
		return new HibernateFlowExecutionListener(sessionFactory, tm);
	}

	@Override
	protected Action incrementCountAction() {
		return new Action() {
			public Event execute(RequestContext context) throws Exception {
				assertSessionBound();
				Session session = (Session) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) session.get(TestBean.class, new Long(0));
				bean.incrementCount();
				return new Event(this, "success");
			}
		};
	}

	@Override
	protected Object assertCountAction() {
		return new Object() {
			@SuppressWarnings("unused")
			public void execute(RequestContext context, int expected) throws Exception {
				assertSessionBound();
				Session session = (Session) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) session.get(TestBean.class, new Long(0));
				assertEquals(expected, bean.getCount());
			}
		};
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(sessionFactory));
	}

	/* private helper methods */

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
