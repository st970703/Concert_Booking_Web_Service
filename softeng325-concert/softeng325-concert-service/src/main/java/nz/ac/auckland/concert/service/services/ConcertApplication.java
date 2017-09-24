package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.service.domain.Booking;
import nz.ac.auckland.concert.service.domain.Reservation;
import nz.ac.auckland.concert.service.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 *
 *
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	// Constructor called by JAX−RS.
	public ConcertApplication () {
		_classes.add(ConcertResource.class);

		EntityManager em = null;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			// Clear DB content
			TypedQuery<Booking> bookingQuery = em.createQuery("select b from " + Booking.class.getName() +  " b", Booking.class);
			List<Booking> bookings = bookingQuery.getResultList();

			for(Booking booking : bookings){
				em.remove(booking);
			}

			TypedQuery<User> userQuery = em.createQuery("select u from " + User.class.getName() +  " u", User.class);
			List<User> users = userQuery.getResultList();

			for(User user : users){
				em.remove(user);
			}

			TypedQuery<Reservation> reservationQuery = em.createQuery("select r from " + Reservation.class.getName() +  " r", Reservation.class);
			List<Reservation> reservations = reservationQuery.getResultList();

			for(Reservation reservation : reservations){
				em.remove(reservation);
			}

			em.flush();
			em.clear();
			em.getTransaction (). commit();
		} catch(Exception e) {
			// Process and log the exception.
			_logger.debug(e.getMessage());
		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}
	}

	private Set<Object> _singletons = new HashSet<>();

	private Set<Class<?>> _classes = new HashSet<>();

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}

	@Override
	public Set<Object> getSingletons()
	{
		_singletons.add(PersistenceManager.instance());

		// Return a Set containing an instance of ParoleeResource that will be
		// used to process all incoming requests on Parolee resources.
		return _singletons;
	}
}
