package org.springframework.webflow.samples.booking;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Hotel> findHotels(SearchCriteria criteria) {
	String pattern = getSearchPattern(criteria);
	return em.createQuery(
		"select h from Hotel h where lower(h.name) like " + pattern + " or lower(h.city) like " + pattern
			+ " or lower(h.zip) like " + pattern + " or lower(h.address) like " + pattern).setMaxResults(
		criteria.getPageSize()).setFirstResult(criteria.getPage() * criteria.getPageSize()).getResultList();
    }

    @Transactional(readOnly = true)
    public Hotel findHotelById(Long id) {
	return em.find(Hotel.class, id);
    }

    // read-write transactional methods

    @Transactional
    public Booking createBooking(Hotel hotel, User user) {
	Booking booking = new Booking(hotel, user);
	em.persist(booking);
	return booking;
    }

    @Transactional
    public void cancelBooking(Long id) {
	Booking booking = em.find(Booking.class, id);
	if (booking != null) {
	    em.remove(booking);
	}
    }

    // helpers

    private String getSearchPattern(SearchCriteria criteria) {
	if (criteria.getSearchString().length() > 0) {
	    return "'%'" + criteria.getSearchString().toLowerCase().replace('*', '%') + "%'";
	} else {
	    return "'%";
	}
    }

}