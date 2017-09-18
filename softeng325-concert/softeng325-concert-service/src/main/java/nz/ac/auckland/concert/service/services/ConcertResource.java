package nz.ac.auckland.concert.service.services;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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

	// Declare necessary instance variables.
	private AtomicLong _idCounter = new AtomicLong();

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	//	@GET
	//	@Path("{id}")
	//	@Produces({ APPLICATION_XML })
	//    public Response retrieveConcert(@PathParam("id")long id) {
	//		 _logger.info("Retrieving concert with id: " + id);
	//	        ResponseBuilder builder = new ResponseBuilderImpl();
	//
	//	        eManager.getTransaction().begin();
	//	        Object responseObj = eManager.find(Concert.class ,id);
	//	        eManager.getTransaction().commit();
	//
	//	        if (responseObj == null) {
	//	        	builder.status(Response.Status.NOT_FOUND);
	//	            throw new WebApplicationException(Response.Status.NOT_FOUND);
	//	        } else {
	//	        	builder.status(Response.Status.OK);
	//	        	_logger.info("Found concert with id: " + id);
	//	        }
	//
	//	        builder.entity((Concert) responseObj);
	//	        Response response = (Response) builder.build();
	//
	//	        return response;
	//	}

	@GET
	@Path("/concerts")
	@Produces({ APPLICATION_XML })
	public Response retrieveConcerts() {
		_logger.info("Retrieving all concerts");
		ResponseBuilder builder = new ResponseBuilderImpl();

		eManager.getTransaction().begin();

		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();

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

		return response;
	}
	
	@GET
	@Path("/performers")
	@Produces({ APPLICATION_XML })
	public Response retrievePerformers() {
		_logger.info("Retrieving all performers");
		ResponseBuilder builder = new ResponseBuilderImpl();

		eManager.getTransaction().begin();

		TypedQuery<Performer> performertQuery = eManager.createQuery("select p from Performer p", Performer.class);
		List<Performer> performers = performertQuery.getResultList();

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

		return response;
	}
}
