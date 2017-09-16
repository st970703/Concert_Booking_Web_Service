package nz.ac.auckland.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.atomic.AtomicLong;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * Class to implement a simple REST Web service for managing Concerts.
 *
 */
@Path("/concerts")
@Produces({APPLICATION_XML})
public class ConcertResource {

    private static Logger _logger = LoggerFactory
            .getLogger(ConcertResource.class);

    // Declare necessary instance variables.
    private AtomicLong _idCounter = new AtomicLong();

    private static PersistenceManager pManager = PersistenceManager.instance();
    private static EntityManager eManager = pManager.createEntityManager();

}
