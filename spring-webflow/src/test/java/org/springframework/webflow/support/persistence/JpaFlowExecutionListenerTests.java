package org.springframework.webflow.support.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.TopLinkJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public class JpaFlowExecutionListenerTests extends TestCase {

    private EntityManagerFactory entityManagerFactory;

    private JpaFlowExecutionListener jpaListener;

    private JdbcTemplate jdbcTemplate;

    private JpaTemplate jpaTemplate;

    protected void setUp() throws Exception {
	DataSource dataSource = getDataSource();
	populateDataBase(dataSource);
	jdbcTemplate = new JdbcTemplate(dataSource);
	entityManagerFactory = getEntityManagerFactory(dataSource);
	JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
	jpaListener = new JpaFlowExecutionListener(entityManagerFactory, tm);
	jpaTemplate = new JpaTemplate(entityManagerFactory);
    }

    public void testFlowNotAPersistenceContext() {
	MockRequestContext context = new MockRequestContext();
	MockFlowSession flowSession = new MockFlowSession();
	jpaListener.sessionCreated(context, flowSession);
	assertSessionNotBound();
    }

    public void testFlowCommitsInSingleRequest() {
	assertEquals("Table should only have one row", 1, jdbcTemplate.queryForInt("select count(*) from T_BEAN"));
	MockRequestContext context = new MockRequestContext();
	MockFlowSession flowSession = new MockFlowSession();
	flowSession.getDefinitionInternal().getAttributeMap().put("persistenceContext", "true");
	jpaListener.sessionCreated(context, flowSession);
	assertSessionBound();

	TestBean bean = new TestBean(1, "Keith Donald");
	jpaTemplate.persist(bean);
	assertEquals("Table should still only have one row", 1, jdbcTemplate.queryForInt("select count(*) from T_BEAN"));

	EndState endState = new EndState(flowSession.getDefinitionInternal(), "success");
	endState.getAttributeMap().put("commit", Boolean.TRUE);
	flowSession.setState(endState);

	jpaListener.sessionEnded(context, flowSession, null);
	assertEquals("Table should only have two rows", 2, jdbcTemplate.queryForInt("select count(*) from T_BEAN"));
	assertSessionNotBound();
	assertFalse(flowSession.getScope().contains("hibernate.session"));
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
	    connection.createStatement().execute("drop table T_BEAN if exists;");
	    connection.createStatement().execute(
		    "create table T_BEAN (ID integer primary key, NAME varchar(50) not null);");
	    connection.createStatement().execute("insert into T_BEAN (ID, NAME) values (0, 'Ben Hale');");
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
	factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/support/persistence/persistence.xml");
	TopLinkJpaVendorAdapter toplink = new TopLinkJpaVendorAdapter();
	factory.setJpaVendorAdapter(toplink);
	factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
	factory.afterPropertiesSet();
	return (EntityManagerFactory) factory.getObject();
    }

    private void assertSessionNotBound() {
	assertNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
    }

    private void assertSessionBound() {
	assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
    }
}
