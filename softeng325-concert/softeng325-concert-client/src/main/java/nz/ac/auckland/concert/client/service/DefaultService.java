package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.services.PersistenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Client client = ClientBuilder.newClient();

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/concerts/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
		case 500:
			//Condition: there is a communication error.
			errorMessage = response.readEntity ( String.class );
			if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		}

		Set<ConcertDTO> cDtos = new HashSet<>(
				response.readEntity(
						new GenericType<
						Set<nz.ac.auckland.concert.common.dto.ConcertDTO>>() {
						}));

		response.close();
		client.close();

		return cDtos;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Client client = ClientBuilder.newClient();

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/performers/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
		case 500:
			//Condition: there is a communication error.
			errorMessage = response.readEntity ( String.class );
			if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		}
		
		Set<PerformerDTO> pDtos = new HashSet<>(
				response.readEntity(
						new GenericType<
						Set<nz.ac.auckland.concert.common.dto.PerformerDTO>>() {
						}));

		response.close();
		client.close();

		return pDtos;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {

		Client client = ClientBuilder.newClient();

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/users/")
				.request()
				.post(Entity.xml(newUser));

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
		case 400: // BAD REQUEST
			//Condition: the expected UserDTO attributes are not set.
			errorMessage = response.readEntity ( String.class );
			throw new ServiceException(errorMessage);

		case 500:
			//Condition: there is a communication error.
			errorMessage = response.readEntity ( String.class );
			if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		}

		response.close();
		client.close();

		return newUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/authenticate/")
				.request()
				.post(Entity.xml(user));

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
		case 400:
		}

		response.close();
		client.close();

		return user;
	}

	@Override
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
