package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.services.ConcertResource;
import nz.ac.auckland.concert.service.services.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/concerts")
@Produces({APPLICATION_XML})
public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	// Declare necessary instance variables.
	private AtomicLong _idCounter = new AtomicLong();

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	@Override
	@GET
	@Path("{id}")
	@Produces({APPLICATION_XML})
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		// TODO Auto-generated method stub
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

}
