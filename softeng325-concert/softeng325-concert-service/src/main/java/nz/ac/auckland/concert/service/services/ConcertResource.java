//package nz.ac.auckland.concert.service.services;
//
//import nz.ac.auckland.concert.common.Config;
//import nz.ac.auckland.concert.domain.Concert;
//import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import javax.ws.rs.*;
//import javax.ws.rs.core.Cookie;
//import javax.ws.rs.core.GenericEntity;
//import javax.ws.rs.core.NewCookie;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.ResponseBuilder;
//import java.net.URI;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicLong;
//
//import static javax.ws.rs.core.MediaType.APPLICATION_XML;
//
///**
// * Class to implement a simple REST Web service for managing Concerts.
// *
// */
//@Path("/concerts")
//@Produces({APPLICATION_XML})
//public class ConcertResource {
//
//    private static Logger _logger = LoggerFactory
//            .getLogger(ConcertResource.class);
//
//    // Declare necessary instance variables.
//    private AtomicLong _idCounter = new AtomicLong();
//
//    private static PersistenceManager pManager = PersistenceManager.instance();
//    private static EntityManager eManager = pManager.createEntityManager();
//
//    /**
//     * Retrieves a Concert based on its unique id. The HTTP response message
//     * has a status code of either 200 or 404, depending on whether the
//     * specified Concert is found.
//     *
//     * When clientId is null, the HTTP request message doesn't contain a cookie
//     * named clientId (Config.CLIENT_COOKIE), this method generates a new
//     * cookie, whose value is a randomly generated UUID. This method returns
//     * the new cookie as part of the HTTP response message.
//     *
//     * This method maps to the URI pattern <base-uri>/concerts/{id}.
//     *
//     * @param id the unique ID of the Concert.
//     *
//     * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent
//     * by the client.
//     *
//     * @return a Response object containing the required Concert.
//     */
//    @GET
//    @Path("{id}")
//    @Produces({APPLICATION_XML})
//    public Response retrieveConcert(@PathParam("id")long id, @CookieParam("clientId") Cookie clientId) {
//
//        _logger.info("Retrieving concert with id: " + id);
//        ResponseBuilder builder = new ResponseBuilderImpl();
//
//        eManager.getTransaction().begin();
//        Object responseObj = eManager.find(Concert.class ,id);
//        eManager.getTransaction().commit();
//
//        if (responseObj == null) {
//            throw new WebApplicationException(Response.Status.NOT_FOUND);
//        } else {
//            _logger.info("Found concert with id: " + id);
//        }
//
//        if (clientId == null) {
//            NewCookie newCookieookie = makeCookie(clientId);
//            builder.cookie(newCookieookie);
//
//            builder.status(Response.Status.NOT_FOUND);
//        } else {
//            builder.status(Response.Status.OK);
//        }
//
//        builder.entity((Concert) responseObj);
//        Response response = (Response) builder.build();
//
//        return response;
//    }
//
//    /**
//     * Retrieves a collection of Concerts, where the "start" query parameter
//     * identifies an index position, and "size" represents the maximum number
//     * of successive Concerts to return. The HTTP response message returns 200.
//     *
//     * When clientId is null, the HTTP request message doesn't contain a cookie
//     * named clientId (Config.CLIENT_COOKIE), this method generates a new
//     * cookie, whose value is a randomly generated UUID. This method returns
//     * the new cookie as part of the HTTP response message.
//     *
//     * This method maps to the URI pattern <base-uri>/concerts?start&size.
//     *
//     * @param start the ID of a Concert from which to start retrieving
//     * Concerts.
//     *
//     * @param size the maximum number of Concerts to retrieve.
//     *
//     * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent
//     * by the client.
//     *
//     * @return a Response object containing a List of Concerts. The List may be
//     * empty.
//     */
//    @GET
//    @Produces({APPLICATION_XML})
//    public Response retrieveConcerts(@CookieParam("clientId") Cookie clientId) {
//        // The Response object should store an ArrayList<Concert> entity. The
//        // ArrayList can be empty depending on the start and size arguments,
//        // and Concerts stored.
//        //
//        // Because of type erasure with Java Generics, any generically typed
//        // entity needs to be wrapped by a javax.ws.rs.core.GenericEntity that
//        // stores the generic type information. Hence to add an ArrayList as a
//        // Response object's entity, you should use the following code:
//        //
//
////		List<Concert> concerts = new ArrayList<Concert>();
////		GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
////		ResponseBuilder builder = Response.ok(entity);
//
//        ResponseBuilder builder = new ResponseBuilderImpl();
//
//        eManager.getTransaction().begin();
//        TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
//        List<Concert> concerts = concertQuery.getResultList();
//        eManager.getTransaction().commit();
//
//        if (clientId == null) {
//            NewCookie newCookie = makeCookie(clientId);
//            builder.cookie(newCookie);
//            builder.status(Response.Status.NOT_FOUND);
//        } else {
//            builder.status(Response.Status.OK);
//        }
//
//        GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
//        builder = Response.ok(entity);
//
//        Response response = (Response) builder.build();
//
//        return response;
//    }
//
//
//    /**
//     * Creates a new Concert. This method assigns an ID to the new Concert and
//     * stores it in memory. The HTTP Response message returns a Location header
//     * with the URI of the new Concert and a status code of 201.
//     *
//     * When clientId is null, the HTTP request message doesn't contain a cookie
//     * named clientId (Config.CLIENT_COOKIE), this method generates a new
//     * cookie, whose value is a randomly generated UUID. This method returns
//     * the new cookie as part of the HTTP response message.
//     *
//     * This method maps to the URI pattern <base-uri>/concerts.
//     *
//     * @param concert the new Concert to create.
//     *
//     * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent
//     * by the client.
//     *
//     * @return a Response object containing the status code 201 and a Location
//     * header.
//     */
//    @POST
//    @Consumes({APPLICATION_XML})
//    public Response createConcert(Concert concert, @CookieParam("clientId") Cookie clientId) {
//
//        eManager.getTransaction().begin();
//        eManager.persist(concert);
//        eManager.getTransaction().commit();
//
//        NewCookie newCookie = makeCookie(clientId);
//
//        ResponseBuilder builder = new ResponseBuilderImpl();
//        if (clientId == null) {
//            builder.cookie(newCookie);
//        }
//
//        builder.entity(concert);
//        builder.status(201);
//        builder.location(URI.create("/concerts/"+concert.getId()));
//
//        return (Response) builder.build();
//    }
//
//
//    /**
//     * Deletes all Concerts, returning a status code of 204.
//     *
//     * When clientId is null, the HTTP request message doesn't contain a cookie
//     * named clientId (Config.CLIENT_COOKIE), this method generates a new
//     * cookie, whose value is a randomly generated UUID. This method returns
//     * the new cookie as part of the HTTP response message.
//     *
//     * This method maps to the URI pattern <base-uri>/concerts.
//     *
//     * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent
//     * by the client.
//     *
//     * @return a Response object containing the status code 204.
//     */
//    @DELETE
//    public Response deleteAllConcerts(@CookieParam("clientId") Cookie clientId) {
//        eManager.getTransaction().begin();
//        TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
//        List<Concert> concerts = concertQuery.getResultList();
//
//        if (concerts != null) {
//            for(Concert concert : concerts) {
//                eManager.remove(concert);
//            }
//        }
//
//        eManager.getTransaction().commit();
//
//        ResponseBuilder builder = new ResponseBuilderImpl();
//        _idCounter = new AtomicLong();
//
//        //builder.cookie(makeCookie(clientId));
//        builder.status(204);
//        return (Response) builder.build();
//    }
//
//    @DELETE
//    @Path("{id}")
//    public Response deleteConcert(@PathParam("id")long id, @CookieParam("clientId") Cookie clientId) {
//        eManager.getTransaction().begin();
//        TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c", Concert.class);
//        List<Concert> concerts = concertQuery.getResultList();
//
//        if (concerts != null) {
//            for (Concert concert : concerts) {
//                if (id == concert.getId()) {
//                    eManager.remove(concert);
//                }
//            }
//        }
//
//        eManager.getTransaction().commit();
//
//        ResponseBuilder builder = new ResponseBuilderImpl();
//        _idCounter = new AtomicLong();
//
//        //builder.cookie(makeCookie(clientId));
//        builder.status(204);
//        return (Response) builder.build();
//    }
//
//    @PUT
//    @Path("{id}")
//    @Produces({APPLICATION_XML})
//    public Response updateConcert(Concert concert, @CookieParam("clientId") Cookie clientId) {
//        eManager.getTransaction().begin();
//
//        Concert c = eManager.find(Concert.class, concert.getId());
//
//        if (c == null) {
//            throw new WebApplicationException(Response.Status.NO_CONTENT);
//        } else {
//
//            eManager.merge(concert);
//            eManager.getTransaction().commit();
//        }
//
//        ResponseBuilder builder = Response.noContent();
//
//
//        if (clientId == null) {
//            builder.cookie(makeCookie(clientId));
//        }
//
//        return builder.entity(concert).build();
//
//    }
//
//
//    /**
//     * Helper method that can be called from every service method to generate a
//     * NewCookie instance, if necessary, based on the clientId parameter.
//     *
//     * @param userId the Cookie whose name is Config.CLIENT_COOKIE, extracted
//     * from a HTTP request message. This can be null if there was no cookie
//     * named Config.CLIENT_COOKIE present in the HTTP request message.
//     *
//     * @return a NewCookie object, with a generated UUID value, if the clientId
//     * parameter is null. If the clientId parameter is non-null (i.e. the HTTP
//     * request message contained a cookie named Config.CLIENT_COOKIE), this
//     * method returns null as there's no need to return a NewCookie in the HTTP
//     * response message.
//     */
//    private NewCookie makeCookie(@CookieParam("clientId") Cookie clientId){
//        NewCookie newCookie = null;
//
//        if(clientId == null) {
//            newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
//            _logger.info("Generated cookie: " + newCookie.getValue());
//        }
//
//        return newCookie;
//    }
//}
