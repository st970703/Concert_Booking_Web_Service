//package nz.ac.auckland.concert.service.services;
//
//import nz.ac.auckland.concert.common.dto.BookingDTO;
//import nz.ac.auckland.concert.common.dto.SeatDTO;
//import nz.ac.auckland.concert.service.domain.Booking;
//import nz.ac.auckland.concert.service.domain.Concert;
//import nz.ac.auckland.concert.service.domain.Seat;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import java.util.HashSet;
//import java.util.Set;
//
////todo
//public class BookingMapper {
//
//	static nz.ac.auckland.concert.common.dto.BookingDTO toDto (nz.ac.auckland.concert.service.domain.Booking booking){
//		Set<Seat> seats = booking.getSeats();
//		Set<SeatDTO> sDtos = new HashSet<>();
//		for(Seat seat: seats) {
//			sDtos.add(SeatMapper.toDto(seat));
//		}
//
//		nz.ac.auckland.concert.common.dto.BookingDTO bDto = new BookingDTO(
//				booking.getConcert().getId(),
//				booking.getConcert().getTitle(),
//				booking.getDateTime(),
//				sDtos,
//				booking.getPriceBand()
//		);
//
//		return bDto;
//	}
//
//	static nz.ac.auckland.concert.service.domain.Booking toDomainModel(nz.ac.auckland.concert.common.dto.BookingDTO bDto) {
//		Set<SeatDTO> sDtos = bDto.getSeats();
//		Set<Seat> seats = new HashSet<>();
//		for(SeatDTO sDto: sDtos) {
//			seats.add(SeatMapper.toDomainModel(sDto));
//		}
//
//		//concert id to concert obj
//		PersistenceManager pManager = PersistenceManager.instance();
//		EntityManager eManager = pManager.createEntityManager();
//		eManager.getTransaction().begin();
//		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c where c.id = :id", Concert.class);
//		Concert concert = concertQuery.getSingleResult( );
//
//		nz.ac.auckland.concert.service.domain.Booking booking = new Booking(
//				concert,
//				concert.getTitle(),
//				bDto.getDateTime(),
//				seats,
//				bDto.getPriceBand()
//		);
//
//		return booking;
//	}
//}
