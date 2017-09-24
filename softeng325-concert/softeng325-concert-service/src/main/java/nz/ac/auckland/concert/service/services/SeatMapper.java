package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Seat;

import javax.persistence.EntityManager;

public class SeatMapper {

	static nz.ac.auckland.concert.common.dto.SeatDTO toDto(nz.ac.auckland.concert.service.domain.Seat seat) {

		nz.ac.auckland.concert.common.dto.SeatDTO sDto = new SeatDTO(
				seat.getRow(),
				seat.getNumber()

		);

		return sDto;
	}

	static nz.ac.auckland.concert.service.domain.Seat toDomainModel(nz.ac.auckland.concert.common.dto.SeatDTO sDto) {
		//todo
		//concert id to concert obj
		PersistenceManager pManager = PersistenceManager.instance();
		EntityManager eManager = pManager.createEntityManager();
		eManager.getTransaction().begin();

		nz.ac.auckland.concert.service.domain.Seat seat = new Seat(
				sDto.getRow(),
				sDto.getNumber()
		);

		return seat;
	}
}
