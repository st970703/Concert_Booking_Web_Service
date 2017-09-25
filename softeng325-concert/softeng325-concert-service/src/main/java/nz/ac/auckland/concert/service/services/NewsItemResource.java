package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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

	ArrayList<AsyncResponse> _responses = new ArrayList<>();

	@GET
	@Path("")
	public synchronized void subscribe(final @Suspended AsyncResponse response) {
		_responses.add(response);
	}

	@POST
	@Path("")
	@Consumes(APPLICATION_XML)
	public synchronized void send(NewsItemDTO nDto) {
		NewsItem nItem = NewsItemMapper.toDomainModel(nDto);

		executor.execute(() -> {
			EntityManager eManager = PersistenceManager.instance().createEntityManager();
			;

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

		// to notify scuscribers
		for (AsyncResponse aResponse : _responses) {
			aResponse.resume(response);
		}

		_responses.clear();
	}

	@GET
	@Path("/unsubscribe")
	public synchronized void unsubscribe() {
		_responses.clear();
	}
}
