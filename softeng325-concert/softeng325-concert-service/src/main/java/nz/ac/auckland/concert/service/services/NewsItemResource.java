package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.NewsItemDTO;
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

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	private Executor executor =
			Executors.newSingleThreadExecutor();

	HashMap<String, AsyncResponse> _responses = new HashMap<>();

	@GET
	@Path("")
	public synchronized void subscribe(final @Suspended AsyncResponse response, @CookieParam("clientId") Cookie clientId) {
		boolean authenticated = authenticateSubscriber(clientId);

		if (authenticated) {
			_responses.put(clientId.getValue(),response);
		}
	}

	@POST
	@Path("")
	@Consumes(APPLICATION_XML)
	public synchronized void send(NewsItemDTO nDto) {
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

		_responses.clear();
	}

	@GET
	@Path("/unsubscribe")
	public synchronized void unsubscribe(@CookieParam("clientId") Cookie clientId) {
		boolean authenticated = authenticateSubscriber(clientId);

		if (authenticated) {
			_responses.remove(clientId.getValue());
		}
	}

	private boolean authenticateSubscriber(Cookie cookie) {
		if (cookie == null) {
			return false;
		}

		try {
			PersistenceManager pManager = PersistenceManager.instance();
			EntityManager eManager = pManager.createEntityManager();

			eManager.getTransaction().begin();

			TypedQuery<User> userQuery = eManager
					.createQuery("select u from User u where u._tokenKey = :token", User.class)
					.setParameter("token", cookie.getValue());
			User findUser = userQuery.getSingleResult();

			eManager.getTransaction().commit();

			String tokenKey = findUser.getToken();

			if (!cookie.getName().equals(Config.CLIENT_COOKIE)
					|| tokenKey == null) {
				return false;
			}
		} catch (NoResultException e) {
			_logger.debug(e.getMessage());
			return false;
		}

		return true;
	}
}
