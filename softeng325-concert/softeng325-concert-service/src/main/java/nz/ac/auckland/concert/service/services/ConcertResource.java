package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.domain.*;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.*;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static nz.ac.auckland.concert.common.Config.CLIENT_COOKIE;

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
		Response response;
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		try {
			ResponseBuilder builder = new ResponseBuilderImpl();

			_logger.info("Retrieving all concerts");

			eManager.getTransaction().begin();

			TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
			List<Concert> concerts = concertQuery.getResultList();

			eManager.getTransaction().commit();

			_logger.debug("concerts == "+concerts);
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

			response = (Response) builder.build();
		} finally {
			if (eManager != null && eManager.isOpen()) {
				eManager.close ();
			}
		}
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
		Response response;

		try {
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

			response = (Response) builder.build();

		} finally {
			if (eManager != null && eManager.isOpen()) {
				eManager.close ();
			}
		}
		return response;
	}

	@POST
	@Path("/users")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response createUser(UserDTO uDto) {
		_logger.info("Creating User");
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		Response response;

		try {
			if (uDto.getFirstname() == null
					|| uDto.getLastname() == null
					|| uDto.getPassword() == null
					|| uDto.getUsername() == null
					) {
				throw new BadRequestException(
						Response.status (Status.BAD_REQUEST)
								.entity (Messages.CREATE_USER_WITH_MISSING_FIELDS)
								.build ());
			}

			User newUser = UserMapper.toDomainModel(uDto);

			eManager.getTransaction().begin();
			User findUser = eManager.find(User.class, uDto.getUsername());
			eManager.getTransaction().commit();

			//Condition: the supplied username is already taken.
			if (findUser != null) {
				throw new BadRequestException(
						Response.status (Status.BAD_REQUEST)
								.entity (Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
								.build ());
			}

			NewCookie cookie = makeCookie(null);
			newUser.setToken(cookie.getValue());

			eManager.getTransaction().begin();
			eManager.persist(newUser);
			eManager.getTransaction().commit();

			ResponseBuilder rBuilder = Response
					.created(URI.create("/resources/users/" + newUser.getId()));

			rBuilder.cookie(cookie);

			response = (Response) rBuilder.build();

			response.status(Status.CREATED);
		} finally {
			if (eManager != null && eManager.isOpen()) {
				eManager.close ();
			}
		}
		return response;
	}

	@POST
	@Path("/authenticate")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response authenticateUser(UserDTO uDto) {
		_logger.info("Creating User");

		/*Condition: the UserDTO parameter doesn't have values for username and/or
		password.*/
		if (uDto.getPassword() == null
				|| uDto.getUsername() == null
				) {
			throw new NotAuthorizedException(
					Response.status (Status.UNAUTHORIZED)
							.entity (Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS)
							.build ());
		}

		Response response;
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		try {
			ResponseBuilder builder = new ResponseBuilderImpl();

			TypedQuery<User> userQuery = eManager
					.createQuery("select u from User u where u._username = :uName", User.class)
					.setParameter("uName", uDto.getUsername());

			eManager.getTransaction().begin();
			User uQuery = userQuery.getSingleResult();
			eManager.getTransaction().commit();

			//condition: wrong password
			boolean wrongPassword = !uQuery.getPassword().equals(uDto.getPassword());
			if(wrongPassword){
				throw new NotAuthorizedException(Response
						.status (Status.UNAUTHORIZED)
						.entity (Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
						.build());
			}

			ResponseBuilder rBuilder = Response.ok(uDto);

			UserDTO new_uDto = UserMapper.toDto(uQuery);

			rBuilder.entity(new_uDto);
			rBuilder.cookie(makeCookie(new_uDto.getUsername()));

			_logger.info("Authentication token value:" + uQuery.getToken());

			response = rBuilder.build();
		} catch (NoResultException nre) {
			throw new NotAuthorizedException(
					Response.status (Status.UNAUTHORIZED)
							.entity (Messages.AUTHENTICATE_NON_EXISTENT_USER)
							.build ());
		} finally {
			if (eManager != null && eManager.isOpen()) {
				eManager.close ();
			}
		}
		return response;
	}

	@POST
	@Path("/reservation")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response makeReservation(ReservationRequestDTO dtoReservationRequest,
									@CookieParam("clientId") Cookie clientId) {
		NewCookie cookie = authenticateCookie(clientId);

		EntityManager em = null;
		ResponseBuilder response;
		try {
			em = PersistenceManager.instance().createEntityManager();

			if(dtoReservationRequest.getConcertId() == null
					|| dtoReservationRequest.getDate() == null
					|| dtoReservationRequest.getNumberOfSeats() <= 0
					|| dtoReservationRequest.getSeatType() == null){
				throw new BadRequestException(
						Response.status (Status.BAD_REQUEST)
								.entity (Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS)
								.build());
			}

			Set<Seat> bookedSeats = new HashSet<>();
			Set<Seat> availableSeats = new HashSet<>();
			Set<Seat> reservedSeats = new HashSet<>();

			em.getTransaction().begin();

			Concert concert = em.find(Concert.class, dtoReservationRequest.getConcertId());

			TypedQuery<Booking> bookingQuery = em.createQuery("select b from "
							+ Booking.class.getName()
							+ " b where b.CONCERT = (:cId)"
							+ " and b.DATE = (:date)"
							+" and b.PRICEBAND = (:pBand)",
					Booking.class);

			bookingQuery.setParameter("cId",
					dtoReservationRequest.getConcertId());
			bookingQuery.setParameter("date",
					dtoReservationRequest.getDate());
			bookingQuery.setParameter("pBand",
					dtoReservationRequest.getSeatType().toString());
			List<Booking> bookings = bookingQuery.getResultList();

			// get user associated with cookie
			PersistenceManager pManager = PersistenceManager.instance();
			EntityManager eManager = pManager.createEntityManager();
			TypedQuery<User> userQuery = eManager
					.createQuery("select u from User u where u._tokenKey = :token", User.class)
					.setParameter("token", cookie.getValue());
			User findUser = userQuery.getSingleResult();

			em.getTransaction().commit();

			boolean wrongDate = !concert.getDates().contains(dtoReservationRequest.getDate());
			if(wrongDate){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
						.build());
			}

			for(Booking booking : bookings){
				for(Seat bookedSeat : booking.getSeats()){
					bookedSeats.add(bookedSeat);
				}
			}

			Set<SeatRow> seatRows = TheatreLayout.getRowsForPriceBand(
					dtoReservationRequest.getSeatType());

			for(SeatRow row : seatRows){
				int number_of_rows = TheatreLayout.getNumberOfSeatsForRow(row);

				for(int i = 1; i < number_of_rows + 1; i++){
					Seat seat = new Seat(row, new SeatNumber(i));

					if(!bookedSeats.contains(seat)){
						availableSeats.add(seat);
					}
				}
			}

			boolean insufficientSeats = availableSeats.size() < dtoReservationRequest.getNumberOfSeats();
			if(insufficientSeats){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
						.build());
			}

			int seatsToReserve = dtoReservationRequest.getNumberOfSeats();
			for(Seat seat : availableSeats){
				reservedSeats.add(seat);
				seatsToReserve--;

				if(seatsToReserve == 0){
					break;
				}
			}

			Booking newBooking = new Booking(concert,
					dtoReservationRequest.getDate(),
					reservedSeats,
					dtoReservationRequest.getSeatType());

			em.getTransaction().begin();

			em.persist(newBooking);

			Reservation reservation = new Reservation(dtoReservationRequest.getSeatType(),
					concert,
					dtoReservationRequest.getDate(),
					reservedSeats,
					newBooking.getId());

			em.persist(reservation);

			findUser.addReservation(reservation);

			em.merge(findUser);

			em.getTransaction().commit();

			ReservationDTO dtoReservation = ReservationMapper.toDto(
					reservation,
					dtoReservationRequest);

			response = Response
					.ok()
					.entity(dtoReservation);

			deleteExpiredReservation(
					reservation.getId(),
					newBooking.getId(),
					findUser.getUsername());

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	@POST
	@Path("/creditcard")
	@Consumes({APPLICATION_XML})
	@Produces({ APPLICATION_XML })
	public Response registerCreditCard(
			nz.ac.auckland.concert.common.dto.CreditCardDTO creditCardDTO,
			@CookieParam("clientId") Cookie clientId) {

		Cookie storedCookie = authenticateCookie(clientId);

		EntityManager em = null;
		ResponseBuilder response;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			String tokenKey = clientId.getValue();

			TypedQuery<User> usertQuery = em.createQuery("select u from User u where u._tokenKey = :tKey", User.class)
					.setParameter("tKey", tokenKey);
			User user = usertQuery.getSingleResult();
			_logger.debug("Found user with username " + user.getUsername());

			user.setCreditCard(CreditCardMapper.toDomainModel(creditCardDTO));
			em.persist(user);
			em.getTransaction().commit();

			response = Response.noContent();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	@GET
	@Path("bookings")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response getBookings(@CookieParam("clientId") Cookie clientId) {
		Cookie cookie = authenticateCookie(clientId);

		EntityManager em = null;
		ResponseBuilder response;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();
			TypedQuery<User> userQuery = em
					.createQuery(
							"select u from User u where u._tokenKey = :token", User.class)
					.setParameter("token", cookie.getValue());
			User findUser = userQuery.getSingleResult();
			Set<Reservation> reservations = findUser.getReservations();

			em.getTransaction().commit();

			Set<BookingDTO> dtoBookings = new HashSet<>();

			for(Reservation reservation : reservations){

				Set<SeatDTO> sDtos = new HashSet<>();

				for(Seat seat : reservation.getSeats()){
					sDtos.add(
							SeatMapper.toDto(seat));
				}

				BookingDTO BookingDTO = new BookingDTO(
						reservation.getConcert().getId(),
						reservation.getConcert().getTitle(),
						reservation.getDate(),
						sDtos,
						reservation.getSeatType());
				dtoBookings.add(BookingDTO);
			}

			GenericEntity<Set<BookingDTO>> entity =
					new GenericEntity<Set<BookingDTO>>(dtoBookings) {};

			response = Response.ok().entity(entity);
		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	/**
	 * helper
	 */
	private NewCookie makeCookie(String clientId){
		NewCookie newCookie;

		if(clientId == null) {
			newCookie = new NewCookie(CLIENT_COOKIE, UUID.randomUUID().toString());
			_logger.info("Generated new cookie: " + newCookie.getValue());
		} else {
			newCookie = new NewCookie(CLIENT_COOKIE, clientId);
			_logger.info("Generated same cookie: " + newCookie.getValue());
		}

		return newCookie;
	}

	/**
	 * helper
	 */
	private NewCookie authenticateCookie(Cookie cookie){
		if(cookie == null){
			_logger.debug("Cookie is null");

			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.UNAUTHENTICATED_REQUEST)
					.build());
		}

		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();

		eManager.getTransaction().begin();

		TypedQuery<User> userQuery = eManager
				.createQuery("select u from User u where u._tokenKey = :token", User.class)
				.setParameter("token", cookie.getValue());
		User findUser = userQuery.getSingleResult();

		eManager.getTransaction().commit();

		String tokenKey = findUser.getToken();

		if(!cookie.getName().equals(Config.CLIENT_COOKIE)
				|| tokenKey == null){
			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.BAD_AUTHENTICATON_TOKEN)
					.build());
		}

		return makeCookie(findUser.getId());
	}

	/**
	 * helper
	 * @param reservationID
	 * @param bookingID
	 * @param username
	 */
	private void deleteExpiredReservation(Long reservationID, Long bookingID, String username){

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				_logger.debug("Checking reservation is confirmed!");

				deleteReservation(reservationID, bookingID, username);

			}
		}, ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS * 1000);

	}

	/**
	 * helper
	 * @param reservationID
	 * @param bookingID
	 * @param username
	 */
	private void deleteReservation(Long reservationID,
								   Long bookingID,
								   String username){
		EntityManager em = null;
		try {
			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			Booking booking = em.find(Booking.class, bookingID);

			Reservation storedReservation = em.find(Reservation.class, reservationID);

			em.getTransaction().commit();

			if(storedReservation != null){
				if(!storedReservation.getStatus()){

					em.getTransaction().begin();

					em.remove(storedReservation);

					em.remove(booking);

					User user = em.find(User.class, username);

					user.removeReservation(storedReservation);

					em.persist(user);

					_logger.debug("Deleted reservation with Id = " + reservationID);

					em.getTransaction().commit();
				}
			}
		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}
	}
}