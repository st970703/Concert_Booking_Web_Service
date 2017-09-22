package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.services.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Client client = ClientBuilder.newClient();
		Set<ConcertDTO> cDtos = null;

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/concerts/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus();

		String errorMessage;
		switch (responseCode) {
			case 200:
				cDtos = new HashSet<>(
						response.readEntity(
								new GenericType<
										Set<nz.ac.auckland.concert.common.dto.ConcertDTO>>() {
								}));
				break;
			default:
				//Condition: there is a communication error.
				errorMessage = response.readEntity ( String.class );
				if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
				}
		}

		response.close();
		client.close();

		return cDtos;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Client client = ClientBuilder.newClient();
		Set<PerformerDTO> pDtos = null;

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/performers/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
			case 200:
				pDtos = new HashSet<>(
						response.readEntity(
								new GenericType<
										Set<nz.ac.auckland.concert.common.dto.PerformerDTO>>() {
								}));
				break;
			default:
				//Condition: there is a communication error.
				errorMessage = response.readEntity ( String.class );
				if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
				}
		}

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

		boolean attrNotSet = false;

		switch (responseCode) {
			case 400:
				errorMessage = response.readEntity (String.class);
				throw new ServiceException(errorMessage);
			case 201:
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
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
				break;
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

	private void processCookie(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();

		boolean containsKey = cookies.containsKey(Config.CLIENT_COOKIE);

		if (containsKey){
			String cookieValue = cookies.get(Config.CLIENT_COOKIE).getValue();

			//_cookieValues = cookieValue;
		}
	}
}
