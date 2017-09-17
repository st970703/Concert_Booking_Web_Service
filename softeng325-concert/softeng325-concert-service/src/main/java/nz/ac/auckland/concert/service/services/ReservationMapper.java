package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Reservation;
import nz.ac.auckland.concert.service.domain.Seat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

public class ReservationMapper {

	private static ReservationRequestDTO rrDto;
	private static Set<SeatDTO> sDtos;
	private static Set<Seat> seats;

	static nz.ac.auckland.concert.common.dto.ReservationDTO toDto(nz.ac.auckland.concert.service.domain.Reservation reservation) {
		//concert id to concert obj
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();
		eManager.getTransaction().begin();
		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c where c.id = :id", Concert.class);
		Concert concert = concertQuery.getSingleResult( );

		rrDto = new ReservationRequestDTO(
				reservation.getSeats().size(),
				reservation.getSeatType(),
				reservation.getConcert().getId(),
				//todo
				concert.getDates().iterator().next()
		);

		seats = new HashSet<>();
		sDtos = new HashSet<>();
		for (Seat seat: seats) {
			sDtos.add(SeatMapper.toDto(seat));
		}

		nz.ac.auckland.concert.common.dto.ReservationDTO rDto = new ReservationDTO(
				reservation.getId(),
				rrDto,
				sDtos
		);

		return rDto;
	}

	static nz.ac.auckland.concert.service.domain.Reservation toDomainModel( nz.ac.auckland.concert.common.dto.ReservationDTO rDto) {
		seats = new HashSet<>();
		sDtos = rDto.getSeats();

		for (SeatDTO sDto: sDtos) {
			seats.add(SeatMapper.toDomainModel(sDto));
		}

		//concert id to concert obj
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();
		eManager.getTransaction().begin();
		TypedQuery<Concert> concertQuery = eManager.createQuery("select c from Concert c where c.id = :id", Concert.class);
		Concert concert = concertQuery.getSingleResult( );

		nz.ac.auckland.concert.service.domain.Reservation reservation = new Reservation(
				rDto.getReservationRequest().getSeatType(),
				concert,
				seats
		);

		return reservation;
	}
}
