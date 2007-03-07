package org.springframework.webflow.jpa.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.EntityMode;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.classic.Session;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.ejb.HibernateEntityManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

/**
 * Utils class for common hibernate JPA routines.
 *
 * @author Maxim Petrashev
 */
public abstract class HibernateUtils {
    public static SessionFactory getSessionFactory(EntityManagerFactory aEntityManagerFactory) {
        HibernateEntityManagerFactory hibernateEntityManagerFactory
                = (HibernateEntityManagerFactory) aEntityManagerFactory;
        return hibernateEntityManagerFactory.getSessionFactory();
    }
    public static Session getSession(EntityManager aEntityManager) {
        return (Session) ((HibernateEntityManager)aEntityManager).getSession();
    }
    @Deprecated public static List<Class> getEntityClasses( EntityManager aEntityManager ) {
        SessionFactory sessionFactory = getSession(aEntityManager).getSessionFactory();
        @SuppressWarnings("unchecked")
        Map<String, ClassMetadata> metadataMap = sessionFactory.getAllClassMetadata();
        List<Class> retVal = new LinkedList<Class>();
        for (ClassMetadata classMetadata : metadataMap.values()) {
            Class type = classMetadata.getMappedClass(EntityMode.POJO);
            retVal.add( type );
        }
        return retVal;
    }
    public static Object getIdentifier(SessionFactory aSessionFactory, Object aEntity) {
        return aSessionFactory.getCurrentSession().getIdentifier( aEntity );
    }
}
