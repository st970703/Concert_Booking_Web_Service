package nz.ac.auckland.concert.service.services;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * Class to implement a simple REST Web service for managing Concerts.
 *
 */
@Produces({APPLICATION_XML})
@Consumes({APPLICATION_XML})
@Path("/resources")
public class ConcertResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	@GET
	@Path("/concerts")
	@Produces({ APPLICATION_XML })
	public Response retrieveConcerts() {
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		_logger.info("Retrieving all concerts");
		ResponseBuilder builder = new ResponseBuilderImpl();

		eManager.getTransaction().begin();

		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();

		eManager.getTransaction().commit();

		if (concerts == null) {
			builder.status(Response.Status.NOT_FOUND);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder.status(Response.Status.OK);
			_logger.info("Found concerts");
		}

		//convert
		Set<ConcertDTO> cDtos = new HashSet<ConcertDTO>(); 
		for(Concert c: concerts) {
			cDtos.add(ConcertMapper.toDto(c));
		}

		GenericEntity<Set<ConcertDTO>> entity = new GenericEntity<
				Set<ConcertDTO>>(cDtos) {};
				builder = Response.ok(entity);

				Response response = (Response) builder.build();

				eManager.close( );

				return response;
	}

	@GET
	@Path("/performers")
	@Produces({ APPLICATION_XML })
	public Response retrievePerformers() {
		_logger.info("Retrieving all performers");
		ResponseBuilder builder = new ResponseBuilderImpl();

		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();
		eManager.getTransaction().begin();

		TypedQuery<Performer> performertQuery = eManager.createQuery("select p from Performer p", Performer.class);
		List<Performer> performers = performertQuery.getResultList();

		eManager.getTransaction().commit();

		if (performers == null) {
			builder.status(Response.Status.NOT_FOUND);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder.status(Response.Status.OK);
			_logger.info("Found concerts");
		}

		//convert
		Set<PerformerDTO> pDtos = new HashSet<PerformerDTO>(); 
		for(Performer p: performers) {
			pDtos.add(PerformerMapper.toDto(p));
		}

		GenericEntity<Set<PerformerDTO>> entity = new GenericEntity<
				Set<PerformerDTO>>(pDtos) {};
				builder = Response.ok(entity);

				Response response = (Response) builder.build();

				eManager.close( );
				return response;
	}

	@POST
	@Path("/users")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response createUser(UserDTO uDto) {
		_logger.info("Creating User");
		ResponseBuilder builder = new ResponseBuilderImpl();

		User user = UserMapper.toDomainModel(uDto);

		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		TypedQuery<User> userQuery = eManager
				.createQuery("select u from User u where u._username = :uName", User.class)
				.setParameter("uName", uDto.getUsername());;
				List<User> uQuery = userQuery.getResultList();

				//Condition: the supplied username is already taken.
				if (uQuery.size() != 0) {
					throw new BadRequestException(
							Response
							.status (Status.BAD_REQUEST)
							.entity (Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
							.build ());
				}

				if (uDto.getFirstname() == null
						|| uDto.getLastname() == null
						|| uDto.getPassword() == null
						|| uDto.getUsername() == null
						) {
					throw new BadRequestException(
							Response
							.status (Status.BAD_REQUEST)
							.entity (Messages.CREATE_USER_WITH_MISSING_FIELDS)
							.build ());
				}

				eManager.getTransaction().begin();
				eManager.persist(user);
				eManager.getTransaction().commit();

				builder.status(201);

				Response response = Response
						.created(URI.create("/resources/users/" + user.getId()))
						.build();

				builder = Response.ok(uDto);

				response = (Response) builder.build();

				eManager.close( );
				return response;
	}

	@POST
	@Path("/authenticate")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response authenticateUser(UserDTO uDto) {
		_logger.info("Creating User");
		ResponseBuilder builder = new ResponseBuilderImpl();

		User user = UserMapper.toDomainModel(uDto);

		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		TypedQuery<User> userQuery = eManager
				.createQuery("select u from User u where u._username = :uName", User.class)
				.setParameter("uName", uDto.getUsername());;
				List<User> uQuery = userQuery.getResultList();

				/*Condition: the UserDTO parameter doesn't have values for username and/or
				password.*/
				if (uDto.getPassword() == null
						|| uDto.getUsername() == null
						) {
					throw new NotAuthorizedException(
							Response
							.status (Status.UNAUTHORIZED)
							.entity (Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS)
							.build ());
				}

				/*Condition: the remote service doesn't have a record of a user with the
				specified username.*/
				if (uQuery.size() == 0) {
					throw new NotAuthorizedException(
							Response
							.status (Status.UNAUTHORIZED)
							.entity (Messages.AUTHENTICATE_NON_EXISTENT_USER)
							.build ());
				}

				return null;
	}

	/*
	 * helper
	 */	
	private NewCookie makeCookie(@CookieParam("clientId") Cookie clientId){
		NewCookie newCookie = null;

		if(clientId == null) {
			newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
			_logger.info("Generated cookie: " + newCookie.getValue());
		}

		return newCookie;
	}
	//	 * Condition: the UserDTO parameter doesn't have values for username and/or
	//	 * password.
	//	 * Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS
	//	 * 
	//	 * Condition: the remote service doesn't have a record of a user with the
	//	 * specified username.
	//	 * Messages.AUTHENTICATE_NON_EXISTENT_USER
	//	 * 
	//	 * Condition: the given user can't be authenticated because their password
	//	 * doesn't match what's stored in the remote service.
	//	 * Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD
	//	 * 
	//	 * Condition: there is a communication error.
	//	 * Messages.SERVICE_COMMUNICATION_ERROR
}
