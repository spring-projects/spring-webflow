package org.springframework.webflow.jpa;

import javax.persistence.EntityManager;

/**
 * Lifycycle controller that hide vendor specific routines for entity manager like:
 * <ul>
 * <li>Application transaction commit implementation. See, for example,
 *  {@link org.hibernate.annotations.FlushModeType.MANUAL}</li>
 * <li>Binding/Unbinding persistence context resources for current thread</li>
 * </ul>
 *
 * @author Maxim Petrashev
 */
public interface EntityManagerLifecycleController {
    /**
     * Create new entity manager and return wrapper for it with aId id.
     */
    EntityManager create();

    /**
     * Reconnect entity manager and bind to current thread.
     * @param aEntityManager
     */
    void activate(EntityManager aEntityManager);
    /**
     * Disconnect current session and unbind from current thread.
     * @param aEntityManager
     */
    void deactivate(EntityManager aEntityManager);

    /**
     * Commit application transaction.
     * @param aEntityManager
     */
    void flush(EntityManager aEntityManager);
    /**
     * Close opened entity manager.
     * @param aEntityManager
     */
    void close(EntityManager aEntityManager);

}
