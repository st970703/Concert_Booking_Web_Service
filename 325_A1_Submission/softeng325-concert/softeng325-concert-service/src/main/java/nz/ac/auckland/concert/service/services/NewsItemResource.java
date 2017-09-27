package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.NewsItem;
import nz.ac.auckland.concert.service.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Produces({APPLICATION_XML})
@Consumes({APPLICATION_XML})
@Path("/newsitem")
public class NewsItemResource {

	private static  NewsItemResource _instance = null;

	public static NewsItemResource instance() {
		if (_instance == null) {
			_instance = new NewsItemResource();
		}
		return _instance;
	}

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	private Executor executor =
			Executors.newSingleThreadExecutor();

	HashMap<String, AsyncResponse> _responses = new HashMap<>();

	@GET
	@Path("/subscribe")
	@Consumes(APPLICATION_XML)
	@Produces(APPLICATION_XML)
	public void subscribe(@Suspended AsyncResponse response, @CookieParam("clientId") Cookie clientId) {
		_logger.debug("void subscribe( "+response +", "+clientId.getValue());

		_responses.put(clientId.getValue(),response);
		_logger.debug("_responses.toString() "+_responses.toString());
	}

	@POST
	@Path("")
	@Consumes(APPLICATION_XML)
	@Produces(APPLICATION_XML)
	public /*synchronized*/ void send(NewsItemDTO nDto) {
		_logger.debug("void send( "+nDto.getContent());

		NewsItem nItem = NewsItemMapper.toDomainModel(nDto);

		executor.execute(() -> {
			EntityManager eManager = PersistenceManager
					.instance()
					.createEntityManager();

			try {
				eManager.getTransaction().begin();
				eManager.persist(nItem);
				eManager.getTransaction().commit();
			} finally {
				if (eManager != null && eManager.isOpen()) {
					eManager.close();
				}
			}
		});

		Response.ResponseBuilder builder = Response.ok(nItem);
		Response response = builder.cookie().build();

		// to notify sbuscribers
		Set<String> keySet = _responses.keySet();

		for (String key : keySet) {
			AsyncResponse aResponse = _responses.get(key);

			aResponse.resume(response);
		}

		return;
	}

	@GET
	@Path("/unsubscribe")
	@Consumes(APPLICATION_XML)
	@Produces(APPLICATION_XML)
	public /*synchronized*/ void unsubscribe(@CookieParam("clientId") Cookie clientId) {
		EntityManager eManager = PersistenceManager.instance().createEntityManager();

		TypedQuery<User> userCookieQuery = eManager.createQuery(
				"select u from User u where u._tokenKey = t", User.class)
				.setParameter("t",clientId.getValue());

		User foundUser = null;
		try {
			foundUser = userCookieQuery.getSingleResult();
		} catch (NoResultException e){
			throw new NotAllowedException(
					Response.status(Response.Status.METHOD_NOT_ALLOWED)
							.entity(Messages.BAD_AUTHENTICATON_TOKEN)
							.build());
		}

		eManager.getTransaction().begin();
		eManager.merge(foundUser);
		eManager.getTransaction().commit();

		eManager.close();

		_responses.remove(clientId.getValue());
	}
}
