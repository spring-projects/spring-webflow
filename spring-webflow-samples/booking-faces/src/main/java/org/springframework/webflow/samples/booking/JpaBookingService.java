package org.springframework.webflow.samples.booking;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * A JPA-based implementation of the Booking Service. Delegates to a JPA entity manager to issue data access calls
 * against the backing repository. The EntityManager reference is provided by the managing container (Spring)
 * automatically.
 */
@Service("bookingService")
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
	if (username != null) {
	    return em.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
		    .setParameter("username", username).getResultList();
	} else {
	    return null;
	}
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Hotel> findHotels(SearchCriteria criteria) {
	String pattern = !StringUtils.hasText(criteria.getSearchString()) ? "'%'" : "'%"
		+ criteria.getSearchString().toLowerCase().replace('*', '%') + "%'";
	return em.createQuery(
		"select h from Hotel h where lower(h.name) like " + pattern + " or lower(h.city) like " + pattern
			+ " or lower(h.zip) like " + pattern + " or lower(h.address) like " + pattern).setMaxResults(
		criteria.getPageSize()).setFirstResult(criteria.getPage() * criteria.getPageSize()).getResultList();
    }

    @Transactional(readOnly = true)
    public Hotel findHotelById(Long id) {
	return em.find(Hotel.class, id);
    }

    @Transactional(readOnly = true)
    public User findUser(String username) {
	return (User) em.createQuery("select u from User u where u.username = :username").setParameter("username",
		username).getSingleResult();
    }

    // read-write transactional methods
    @Transactional
    public void cancelBooking(Booking booking) {
	booking = em.find(Booking.class, booking.getId());
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