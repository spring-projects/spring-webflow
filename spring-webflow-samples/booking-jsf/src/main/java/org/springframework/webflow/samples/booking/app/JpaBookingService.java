package org.springframework.webflow.samples.booking.app;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * A JPA-based implementation of the Booking Service. Delegates to a JPA entity manager to issue data access calls
 * against the backing repository. The EntityManager reference is provided by the managing container (Spring)
 * automatically.
 */
@Repository
public class JpaBookingService implements BookingService {

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Booking> findBookings(String username) {
	return em.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
		.setParameter("username", username).getResultList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Hotel> findHotels(String searchString, int pageSize, int page) {
	String pattern = !StringUtils.hasText(searchString) ? "'%'" : "'%"
		+ searchString.toLowerCase().replace('*', '%') + "%'";
	return em.createQuery(
		"select h from Hotel h where lower(h.name) like " + pattern + " or lower(h.city) like " + pattern
			+ " or lower(h.zip) like " + pattern + " or lower(h.address) like " + pattern).setMaxResults(
		pageSize).setFirstResult(page * pageSize).getResultList();
    }

    @Transactional(readOnly = true)
    public Hotel findHotelById(Long id) {
	return em.find(Hotel.class, id);
    }

    // this one is a read/write transaction
    @Transactional
    public void cancelBooking(Long id) {
	Booking booking = em.find(Booking.class, id);
	if (booking != null) {
	    em.remove(booking);
	}
    }
}