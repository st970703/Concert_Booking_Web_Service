//package nz.ac.auckland.concert.client.service;
//
//import nz.ac.auckland.concert.common.dto.*;
//import nz.ac.auckland.concert.common.message.Messages;
//import nz.ac.auckland.concert.common.types.PriceBand;
//import nz.ac.auckland.concert.common.types.SeatRow;
//import nz.ac.auckland.concert.common.util.TheatreLayout;
//import nz.ac.auckland.concert.service.services.ConcertApplication;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
//import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
//import org.junit.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
///**
// * Tests for a ConcertService implementation.
// *
// * Prior to running each test, an embedded servlet container is started up that
// * hosts a named Web service. Immediately after each test, the servlet
// * container is stopped. Testing is handled in this way so that the Web service
// * can reinitialise its database. Since an embedded H2 database can only have
// * one connection at a time, it's not possible for the @After method in this
// * Test class to connect to the H2 database to delete data - hence restarting
// * the Web service before each test allows the Web service to clear from the
// * database the effects of the tests.
// *
// * This Test class references property RESERVATION_EXPIRY_TIME_IN_SECONDS,
// * defined in the Web service Application subclass, ConcertApplication.
// * Darius Au's Tests
// */
//public class ConcertServiceTest2 {
//
//	private static Logger _logger = LoggerFactory
//			.getLogger(ConcertServiceTest2.class);
//
//	private static final int SERVER_PORT = 10000;
//	private static final String WEB_SERVICE_CLASS_NAME = ConcertApplication.class.getName();
//
//	private static Client _client;
//	private static Server _server;
//
//	private ConcertService _service;
//
//	@BeforeClass
//	public static void createClientAndServer() throws Exception {
//		// Use ClientBuilder to create a new client that can be used to create
//		// connections to the Web service.
//		_client = ClientBuilder.newClient();
//
//		// Start the embedded servlet container and host the Web service.
//		ServletHolder servletHolder = new ServletHolder(new HttpServletDispatcher());
//		servletHolder.setInitParameter("javax.ws.rs.Application", WEB_SERVICE_CLASS_NAME);
//		ServletContextHandler servletCtxHandler = new ServletContextHandler();
//		servletCtxHandler.setContextPath("/services");
//		servletCtxHandler.addServlet(servletHolder, "/");
//		_server = new Server(SERVER_PORT);
//		_server.setHandler(servletCtxHandler);
//	}
//
//	@AfterClass
//	public static void shutDown() {
//		_client.close();
//	}
//
//	@Before
//	public void startServer() throws Exception {
//		_server.start();
//		_service = new DefaultService();
//	}
//
//	@After
//	public void stopServer() throws Exception {
//		_server.stop();
//	}
//
//
//	@Test
//	public void testRetrieveConcerts() {
//		final int numberOfConcerts = 25;
//
//		Set<ConcertDTO> concerts = _service.getConcerts();
//		assertEquals(numberOfConcerts, concerts.size());
//	}
//
//	@Test
//	public void testRetrievePerformers() {
//		final int numberOfPerformers = 20;
//
//		Set<PerformerDTO> performers = _service.getPerformers();
//		assertEquals(numberOfPerformers, performers.size());
//	}
//
//	@Test
//	public void testGetPerformerImage(){
//		Set<PerformerDTO> performers = _service.getPerformers();
//		List<PerformerDTO> list = new ArrayList<PerformerDTO>(performers);
//		try{
//		_service.getImageForPerformer(list.get(0));
//		} catch(ServiceException e) {
//			assertEquals(Messages.SERVICE_COMMUNICATION_ERROR, e.getMessage());
//		}
//	}
//
////	@Test
////	public void testSubscription(){
////		try {
////			UserDTO userDTO = new UserDTO("Bulldog", "123", "Au", "Darius");
////			_service.createUser(userDTO);
////			Subscription subscription = new Subscription();
////			//subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////
////			assertEquals(0,subscription.getNewsItems().size());
////			_service.subscribeForNewsItems(subscription);
////
////			DefaultService _serverService = new DefaultService();
////			_serverService.addNewItem(new NewsItemDTO((long)3,LocalDateTime.now(),"This should be added to subscription"));
////
////			Thread.sleep((long)1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(1,subscription.getNewsItems().size());
////
////			_serverService.addNewItem(new NewsItemDTO((long)4,LocalDateTime.now(),"This should be added to subscription as well"));
////
////			Thread.sleep((long)1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(2,subscription.getNewsItems().size());
////
////
////			_serverService.addNewItem(new NewsItemDTO((long)5,LocalDateTime.now(),"This should be added AS WELL"));
////
////			Thread.sleep(1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(3,subscription.getNewsItems().size());
////
////			_service.cancelSubscription();
////
////		} catch (ServiceException | InterruptedException e){
////			fail();
////		}
////	}
////
////	@Test
////	public void testUnsubscribe(){
////		try {
////			UserDTO userDTO = new UserDTO("Bulldog", "123", "Au", "Darius");
////			_service.createUser(userDTO);
////			Subscription subscription = new Subscription();
////			//subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////
////			assertEquals(0,subscription.getNewsItems().size());
////			_service.subscribeForNewsItems(subscription);
////
////			DefaultService _serverService = new DefaultService();
////			_serverService.addNewItem(new NewsItemDTO((long)3,LocalDateTime.now(),"This should be added to subscription"));
////
////			Thread.sleep(1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(1,subscription.getNewsItems().size());
////
////			_service.cancelSubscription();
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(1,subscription.getNewsItems().size());
////
////		} catch (ServiceException | InterruptedException e){
////			fail();
////		}
////	}
////
////
////	@Test
////	public void testSubscriptionBlackout(){
////		try {
////			UserDTO userDTO = new UserDTO("Bulldog", "123", "Au", "Darius");
////			_service.createUser(userDTO);
////			Subscription subscription = new Subscription();
////			//subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////
////			assertEquals(0,subscription.getNewsItems().size());
////			_service.subscribeForNewsItems(subscription);
////
////			DefaultService _serverService = new DefaultService();
////			_serverService.addNewItem(new NewsItemDTO((long)3,LocalDateTime.now(),"This should be added to subscription"));
////
////			Thread.sleep((long)1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(1,subscription.getNewsItems().size());
////
////			_serverService.addNewItem(new NewsItemDTO((long)4,LocalDateTime.now(),"This should be added to subscription as well"));
////			Thread.sleep((long)1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(2,subscription.getNewsItems().size());
////
////			_service.cancelSubscription();
////
////			_serverService.addNewItem(new NewsItemDTO((long)5,LocalDateTime.now(),"This waas missed by blackout"));
////			Thread.sleep(1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(2,subscription.getNewsItems().size());
////
////			_serverService.addNewItem(new NewsItemDTO((long)7,LocalDateTime.now(),"This waas missed by blackout as well"));
////			Thread.sleep(1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(2,subscription.getNewsItems().size());
////
////			_service.subscribeForNewsItems(subscription);
////
////			_serverService.addNewItem(new NewsItemDTO((long)6,LocalDateTime.now(),"This should be added AS WELL"));
////			Thread.sleep(1000);
////
////			subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////			assertEquals(5,subscription.getNewsItems().size());
////
////
////		} catch (ServiceException | InterruptedException e){
////			fail();
////		}
////	}
//
//	@Test
//	public void testMakeReservationWithMultipleUsers() {
//		try {
//			final int numberOfSeatsToBook = 2;
//
//			UserDTO userDTO = new UserDTO("Bulldog", "123", "Churchill", "Winston");
//			_service.createUser(userDTO);
//
//			ConcertService service2 = new DefaultService();
//			UserDTO darius = new UserDTO("Bulldog2", "123", "Au", "Darius");
//			service2.createUser(darius);
//
//			LocalDateTime dateTime = LocalDateTime.of(2017, 2, 24, 17, 00);
//			ReservationRequestDTO request = new ReservationRequestDTO(numberOfSeatsToBook, PriceBand.PriceBandC, 1L, dateTime);
//			ReservationDTO reservation = _service.reserveSeats(request);
//
//			//Darius
//			LocalDateTime dateTime2 = LocalDateTime.of(2017, 2, 24, 17, 00);
//			ReservationRequestDTO request2 = new ReservationRequestDTO(numberOfSeatsToBook, PriceBand.PriceBandA, 1L, dateTime2);
//			ReservationDTO reservation2 = service2.reserveSeats(request2);
//
//			ReservationRequestDTO requestFromResponse = reservation.getReservationRequest();
//			assertEquals(request, requestFromResponse);
//			ReservationRequestDTO requestFromResponse2 = reservation2.getReservationRequest();
//			assertEquals(request2, requestFromResponse2);
//
//			Set<SeatDTO> reservedSeats = reservation.getSeats();
//			assertEquals(numberOfSeatsToBook, reservedSeats.size());
//			Set<SeatDTO> reservedSeats2 = reservation2.getSeats();
//			assertEquals(numberOfSeatsToBook, reservedSeats2.size());
//
//
//			// Check that the seats reserved are of the required type.
//			for(SeatDTO seat : reservedSeats) {
//				assertTrue(TheatreLayout.getRowsForPriceBand(PriceBand.PriceBandC).contains(seat.getRow()));
//			}
//			for(SeatDTO seat : reservedSeats2) {
//				assertTrue(TheatreLayout.getRowsForPriceBand(PriceBand.PriceBandA).contains(seat.getRow()));
//			}
//
//		} catch(ServiceException e) {
//			fail();
//		}
//	}
//
//
//		@Test
//		public void testMakeReservationClashWithMultipleUsers() {
//
//			ReservationRequestDTO request = null;
//			ReservationDTO reservation = null;
//
//			Set<SeatRow> rowsOfStandardSeats = TheatreLayout.getRowsForPriceBand(PriceBand.PriceBandB);
//			int totalNumberOfStandardSeats = 0;
//			for(SeatRow row : rowsOfStandardSeats) {
//				totalNumberOfStandardSeats += TheatreLayout.getNumberOfSeatsForRow(row);
//			}
//
//			try {
//
//
//				UserDTO userDTO = new UserDTO("Bulldog", "123", "Churchill", "Winston");
//				_service.createUser(userDTO);
//
//				ConcertService service2 = new DefaultService();
//				UserDTO darius = new UserDTO("Bulldog2", "123", "Au", "Darius");
//				service2.createUser(darius);
//
//				LocalDateTime dateTime = LocalDateTime.of(2017, 2, 24, 17, 00);
//				request = new ReservationRequestDTO(totalNumberOfStandardSeats-1, PriceBand.PriceBandB, 1L, dateTime);
//				reservation = _service.reserveSeats(request);
//
//				//Darius
//				LocalDateTime dateTime2 = LocalDateTime.of(2017, 2, 24, 17, 00);
//				ReservationRequestDTO request2 = new ReservationRequestDTO(2, PriceBand.PriceBandB, 1L, dateTime2);
//				ReservationDTO reservation2 = service2.reserveSeats(request2);
//
//
//				fail();
//			} catch(ServiceException e) {
//				ReservationRequestDTO requestFromResponse = reservation.getReservationRequest();
//				assertEquals(request, requestFromResponse);
//
//				Set<SeatDTO> reservedSeats = reservation.getSeats();
//				assertEquals(totalNumberOfStandardSeats-1, reservedSeats.size());
//
//				// Check that the seats reserved are of the required type.
//				for(SeatDTO seat : reservedSeats) {
//					assertTrue(TheatreLayout.getRowsForPriceBand(PriceBand.PriceBandB).contains(seat.getRow()));
//				}
//			}
//
//		}
//
////		@Test
////		public void testSubscription2(){
////			try {
////
////				DefaultService _serverService = new DefaultService();
////				_serverService.addNewItem(new NewsItemDTO((long)3,LocalDateTime.now(),"This should be added to subscription"));
////
////				UserDTO userDTO = new UserDTO("Bulldog", "123", "Au", "Darius");
////				_service.createUser(userDTO);
////				Subscription subscription = new Subscription();
////				//subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////
////				assertEquals(0,subscription.getNewsItems().size());
////				_service.subscribeForNewsItems(subscription);
////
////				Thread.sleep((long)1000);
////
////				subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////				assertEquals(1,subscription.getNewsItems().size());
////
////				/*_serverService.addNewItem(new NewsItemDTO((long)4,LocalDateTime.now(),"This should be added to subscription as well"));
////
////				Thread.sleep((long)1000);
////
////				subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////				assertEquals(2,subscription.getNewsItems().size());
////
////
////				_serverService.addNewItem(new NewsItemDTO((long)5,LocalDateTime.now(),"This should be added AS WELL"));
////
////				Thread.sleep(1000);
////
////				subscription.getNewsItems().forEach(item -> System.out.println(item.getContent()));
////				assertEquals(3,subscription.getNewsItems().size());*/
////
////				_service.cancelSubscription();
////
////			} catch (ServiceException | InterruptedException e){
////				fail();
////			}
////		}
//}
