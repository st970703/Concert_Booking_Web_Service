package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.services.PersistenceManager;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

//import nz.ac.auckland.concert.service.services.ConcertResource;

@Path("/concerts")
@Produces({APPLICATION_XML})
public class DefaultService implements ConcertService {

//	private static Logger _logger = LoggerFactory
//			.getLogger(ConcertResource.class);

	// Declare necessary instance variables.
	private AtomicLong _idCounter = new AtomicLong();

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	@Override
	@GET
	@Path("{id}")
	@Produces({APPLICATION_XML})
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response.ResponseBuilder builder = new ResponseBuilderImpl();

		eManager.getTransaction().begin();
		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c where c.id = :id", Concert.class);
		java.util.Set<Concert> concerts = new HashSet(concertQuery.getResultList());
		eManager.getTransaction().commit();

if () {

}

		if (clientId == null) {
			NewCookie newCookie = makeCookie(clientId);
			builder.cookie(newCookie);
			builder.status(Response.Status.NOT_FOUND);
		} else {
			builder.status(Response.Status.OK);
		}

		GenericEntity<java.util.List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
		builder = Response.ok(entity);

		Response response = (Response) builder.build();

		return response;

		return null;
	}

	@Override
	@GET
	@Path("{id}")
	@Produces({APPLICATION_XML})
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@POST
	@Consumes({APPLICATION_XML})
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("{id}")
	@Produces({APPLICATION_XML})
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@POST
	@Consumes({APPLICATION_XML})
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@POST
	@Consumes({APPLICATION_XML})
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	@POST
	@Consumes({APPLICATION_XML})
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	@GET
	@Path("{id}")
	@Produces({APPLICATION_XML})
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@POST
	@Consumes({APPLICATION_XML})
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();

	}

	@Override
	@DELETE
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}


	//helper
	private NewCookie makeCookie(@CookieParam("clientId") Cookie clientId){
		NewCookie newCookie = null;

		if(clientId == null) {
			newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
			_logger.info("Generated cookie: " + newCookie.getValue());
		}

		return newCookie;
	}
}
