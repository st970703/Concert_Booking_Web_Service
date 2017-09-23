package nz.ac.auckland.concert.client.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.CreditCard;
import nz.ac.auckland.concert.service.domain.User;
import nz.ac.auckland.concert.service.services.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static nz.ac.auckland.concert.common.Config.CLIENT_COOKIE;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private static PersistenceManager pManager = PersistenceManager.instance();
	private static EntityManager eManager = pManager.createEntityManager();

	private NewCookie _storedCookie;

	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "";
	private static final String AWS_SECRET_ACCESS_KEY = "";

	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	private static final String USER_DIRECTORY = System
			.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
			+ FILE_SEPARATOR + "images";

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Client client = ClientBuilder.newClient();
		Set<ConcertDTO> cDtos = null;

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/concerts/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus();

		String errorMessage;
		switch (responseCode) {
			case 200:
				cDtos = new HashSet<>(
						response.readEntity(
								new GenericType<
										Set<nz.ac.auckland.concert.common.dto.ConcertDTO>>() {
								}));
				break;
			default:
				//Condition: there is a communication error.
				errorMessage = response.readEntity ( String.class );
				if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
				}
		}

		response.close();
		client.close();

		return cDtos;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Client client = ClientBuilder.newClient();
		Set<PerformerDTO> pDtos = null;

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/performers/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		String errorMessage;
		switch (responseCode) {
			case 200:
				pDtos = new HashSet<>(
						response.readEntity(
								new GenericType<
										Set<nz.ac.auckland.concert.common.dto.PerformerDTO>>() {
								}));
				break;
			default:
				//Condition: there is a communication error.
				errorMessage = response.readEntity ( String.class );
				if (errorMessage.equals(Messages.SERVICE_COMMUNICATION_ERROR)) {
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
				}
		}

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

		String errorMessage;

		switch (responseCode) {
			case 400:
				errorMessage = response.readEntity (String.class);
				throw new ServiceException(errorMessage);
			case 201:
				break;
			case 200:
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		processCookie(response);

		response.close();
		client.close();

		return newUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Response response = client
				.target(WEB_SERVICE_URI+"/resources/authenticate")
				.request()
				.post(Entity.xml(user));

		processCookie(response);

		// Get the response code from the Response object.
		int responseCode = response.getStatus ();

		UserDTO uDto;

		String errorMessage;
		switch (responseCode) {
			case 401:
				errorMessage = response.readEntity (String.class);

				throw new ServiceException(errorMessage);
			case 200:
				uDto = response.readEntity(UserDTO.class);
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		response.close();
		client.close();

		return uDto;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		String name = performer.getImageName();

		// Create download directory if it doesn't already exist.
		File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
		downloadDirectory.mkdir();

		// Create an AmazonS3 object that represents a connection with the
		// remote S3 service.
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
				AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(
						new AWSStaticCredentialsProvider(awsCredentials))
				.build();

		TransferManager tMgr = TransferManagerBuilder
				.standard()
				.withS3Client(s3)
				.build();

		File file = new File(
				DOWNLOAD_DIRECTORY + FILE_SEPARATOR + name);

		try {
			Download download = tMgr.download(AWS_BUCKET, name, file);
			download.waitForCompletion();
		} catch (AmazonServiceException e) {
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		} catch (AmazonClientException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		tMgr.shutdownNow();

		Image image;

		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		}

		return image;
	}
}

	@Override

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
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/resources/creditcard").request();
		addCookieToBuilder(builder);

		Response response = builder.post(Entity.xml(creditCard));

		processCookie(response);

		int responseCode = response.getStatus();

		switch (responseCode){
			case 401:
				String errorMessage = response.readEntity (String.class);
				throw new ServiceException(errorMessage);
			case 200:
				break;
			case 201:
				break;
			case 204:
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		response.close();
		client.close();

		return;
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

	/**
	 *  helper
	 */
	private void processCookie(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();

		boolean containsKey = cookies.containsKey(Config.CLIENT_COOKIE);

		if (containsKey){
			NewCookie getCookie = cookies.get(Config.CLIENT_COOKIE);
			_storedCookie = getCookie;
		}
	}

	/**
	 * helper
	 * @param builder
	 */
	private void addCookieToBuilder(Invocation.Builder builder) {
		if(_storedCookie != null) {
			builder.cookie(Config.CLIENT_COOKIE, _storedCookie.getValue());
		}
		return;
	}
}
