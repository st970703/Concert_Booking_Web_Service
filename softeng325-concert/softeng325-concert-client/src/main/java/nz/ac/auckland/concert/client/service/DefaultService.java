package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.services.PersistenceManager;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.identitymanagement.model.User;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.annotation.XmlElement;

import java.awt.*;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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

		/*
		 * Process the response. If the response code is 400/401, expect the
		 * Response's entity to be a string error message. For other
		response codes, the entity might be a DTO that could be retrieved .
		 */
		String errorMessage;
		switch (responseCode) {
		case 400: // BAD REQUEST
			//Condition: the expected UserDTO attributes are not set.
			errorMessage = response.readEntity ( String.class );
			if (errorMessage.equals(Messages.CREATE_USER_WITH_MISSING_FIELDS)) {
				throw new ServiceException(Messages.CREATE_USER_WITH_MISSING_FIELDS);
			}
			
			if (errorMessage.equals(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)) {
				throw new ServiceException(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);	
			}

		default:
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
