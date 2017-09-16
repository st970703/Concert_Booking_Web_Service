package nz.ac.auckland.concert.service.services;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 *
 *
 *
 */
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	// Constructor called by JAX−RS.
	public ConcertApplication () {
		_classes.add(ConcertResource.class);

		EntityManager em = null;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			// Delete all existing entities of some type, e. g. MyEntity.
			em.createQuery("delete  from MyEntity").executeUpdate();

			// Make many entities of some type.
//			for (...) {
//				for (...) {
//					Object e = new MyEntity(...);
//					em. persist (e);
//				}
//				// Periodically flush and clear the persistence context.
				em.flush ();
				em.clear ();
//			}
			em.getTransaction (). commit();

		} catch(Exception e) {
			// Process and log the exception .
		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}
	}

	private Set<Object> _singletons = new HashSet<Object>();

	private Set<Class<?>> _classes = new HashSet<Class<?>>();

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
