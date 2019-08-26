package org.springframework.webflow.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
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
			public Event execute(RequestContext context) {
				assertSessionBound();
				Session session = (Session) context.getFlowScope().get("persistenceContext");
				TestBean bean = session.get(TestBean.class, 0L);
				bean.incrementCount();
				return new Event(this, "success");
			}
		};
	}

	@Override
	protected Object assertCountAction() {
		return new Object() {
			@SuppressWarnings("unused")
			public void execute(RequestContext context, int expected) {
				assertSessionBound();
				Session session = (Session) context.getFlowScope().get("persistenceContext");
				TestBean bean = session.get(TestBean.class, 0L);
				assertEquals(expected, bean.getCount());
			}
		};
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(sessionFactory));
	}

	/* private helper methods */

	private SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setMappingLocations(
				new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
				new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml"));
		factory.afterPropertiesSet();
		return factory.getObject();
	}

}
