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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultService implements ConcertService {

	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
	private static final String AWS_SECRET_ACCESS_KEY = "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";
	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";
	private static final String FILE_SEPARATOR =
			System.getProperty("file.separator");
	private static final String USER_DIRECTORY = System.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY =
			USER_DIRECTORY + FILE_SEPARATOR + "images";
	private static Logger _logger = LoggerFactory.getLogger(ConcertService.class);
	private static String WEB_SERVICE_URI = "http://localhost:10000/services";
	private NewCookie _storedCookie;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Client client = ClientBuilder.newClient();
		Set<ConcertDTO> cDtos = null;

		Response response = client
				.target(WEB_SERVICE_URI + "/resources/concerts/")
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
				errorMessage = response.readEntity(String.class);
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
				.target(WEB_SERVICE_URI + "/resources/performers/")
				.request()
				.get();

		// Get the response code from the Response object.
		int responseCode = response.getStatus();

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
				errorMessage = response.readEntity(String.class);
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
				.target(WEB_SERVICE_URI + "/resources/users/")
				.request()
				.post(Entity.xml(newUser));

		// Get the response code from the Response object.
		int responseCode = response.getStatus();

		String errorMessage;

		switch (responseCode) {
			case 400:
				errorMessage = response.readEntity(String.class);
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
				.target(WEB_SERVICE_URI + "/resources/authenticate")
				.request()
				.post(Entity.xml(user));

		processCookie(response);

		// Get the response code from the Response object.
		int responseCode = response.getStatus();

		UserDTO uDto;

		String errorMessage;
		switch (responseCode) {
			case 401:
				errorMessage = response.readEntity(String.class);
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

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Invocation.Builder builder = client.target(
				WEB_SERVICE_URI + "/resources/reservation").request();
		addCookieToBuilder(builder);

		Response response = builder.post(
				Entity.xml(reservationRequest));

		ReservationDTO rDto = new ReservationDTO();

		String errorMessage;
		int responseCode = response.getStatus();
		_logger.debug("reserveSeats() responseCode =" + responseCode);
		switch (responseCode) {
			case 400:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 401:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 200:
				rDto = response.readEntity(ReservationDTO.class);
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		response.close();
		client.close();

		return rDto;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Invocation.Builder builder = client.target(
				WEB_SERVICE_URI + "/resources/confirm").request();
		addCookieToBuilder(builder);

		Response response = builder.post(
				Entity.xml(reservation));

		String errorMessage;
		int responseCode = response.getStatus();
		_logger.debug("confirmReservation() responseCode = " + responseCode);

		switch (responseCode) {
			case 400:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 401:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 404:
				errorMessage = response.readEntity(String.class);
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
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/resources/creditcard").request();
		addCookieToBuilder(builder);

		Response response = builder.post(Entity.xml(creditCard));

		processCookie(response);

		int responseCode = response.getStatus();

		switch (responseCode) {
			case 401:
				String errorMessage = response.readEntity(String.class);
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
	public Set<BookingDTO> getBookings() throws ServiceException {
		Client client = ClientBuilder.newClient();

		Invocation.Builder builder = client
				.target(WEB_SERVICE_URI + "/resources/bookings")
				.request();
		addCookieToBuilder(builder);
		Response response = builder.get();

		Set<BookingDTO> bDtos = new HashSet<>();

		int responseCode = response.getStatus();
		switch (responseCode) {
			case 401:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 200:
				bDtos = response.readEntity(new GenericType<Set<nz.ac.auckland.concert.common.dto.BookingDTO>>() {
				});
			case 201:
				break;
			case 204:
				break;
			default:
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		response.close();
		client.close();

		return bDtos;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		Client client = ClientBuilder.newClient();

		AsyncInvoker asyncInvoker = client
				.target(WEB_SERVICE_URI + "/newsitem")
				.request()
				.cookie(_storedCookie)
				.async();

		asyncInvoker.get(
				new InvocationCallback<Response>() {

					@Override
					public void completed(Response response) {
						NewsItemDTO newsItem = response.readEntity(NewsItemDTO.class);
						
						listener.newsItemReceived(newsItem);
						asyncInvoker.get(this);
					}

					@Override
					public void failed(Throwable throwable) {
						_logger.debug("public void failed(Throwable throwable) " + throwable.getMessage());
						throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
					}
				}
		);

		client.close();
	}

	@Override
	public void cancelSubscription() {
		Client client = ClientBuilder.newClient();

		AsyncInvoker asyncInvoker = client
				.target(WEB_SERVICE_URI + "newsitem/unsubscribe")
				.request()
				.cookie(_storedCookie)
				.async();

		_logger.info("cancelSubscription() "+asyncInvoker.get().toString());

		client.close();
	}

	/**
	 * helper
	 */
	private void processCookie(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();

		boolean containsKey = cookies.containsKey(Config.CLIENT_COOKIE);

		if (containsKey) {
			NewCookie getCookie = cookies.get(Config.CLIENT_COOKIE);
			_storedCookie = getCookie;
		}
		return;
	}

	/**
	 * helper
	 *
	 * @param builder
	 */
	private void addCookieToBuilder(Invocation.Builder builder) {
		if (_storedCookie != null) {
			_logger.debug(".cookie(" + Config.CLIENT_COOKIE + ", " + _storedCookie.getValue() + ");");
			builder.cookie(Config.CLIENT_COOKIE, _storedCookie.getValue());
		}
		return;
	}
}
