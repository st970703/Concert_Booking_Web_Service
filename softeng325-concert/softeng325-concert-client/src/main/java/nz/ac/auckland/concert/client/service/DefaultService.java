package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.service.services.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private static Client _client = ClientBuilder.newClient();
	private AtomicLong _idCounter = new AtomicLong();

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response response = _client
				.target(WEB_SERVICE_URI+"/resources/concerts/")
				.request()
				.get();

		Set<ConcertDTO> cDtos = new HashSet<>(
				response.readEntity(
						new GenericType<
						Set<nz.ac.auckland.concert.common.dto.ConcertDTO>>() {
				}));

		response.close();

		return cDtos;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Response response = _client
				.target(WEB_SERVICE_URI+"/resources/performers/")
				.request()
				.get();

		Set<PerformerDTO> pDtos = new HashSet<>(
				response.readEntity(
						new GenericType<
						Set<nz.ac.auckland.concert.common.dto.PerformerDTO>>() {
				}));

		response.close();

		return pDtos;
	}

	@Override
//	@POST
//	@Consumes({ APPLICATION_XML })
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		Response response = _client.target(WEB_SERVICE_URI).request().post(Entity.xml(newUser));

		response.close();

		return newUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
//	@GET
//	@Path("{id}")
//	@Produces({ APPLICATION_XML })
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
//	@POST
//	@Consumes({ APPLICATION_XML })
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
//	@POST
//	@Consumes({ APPLICATION_XML })
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
//	@POST
//	@Consumes({ APPLICATION_XML })
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
//	@GET
//	@Path("{id}")
//	@Produces({ APPLICATION_XML })
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
//	@POST
//	@Consumes({ APPLICATION_XML })
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();

	}

	@Override
//	@DELETE
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}
}
