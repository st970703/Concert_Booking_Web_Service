package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.Concert;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Helper class to convert between domain-model and DTO objects representing
 *
 */
public class ConcertMapper {

	private static Map<PriceBand, BigDecimal> ticketPrices;

	static nz.ac.auckland.concert.common.dto.ConcertDTO toDto(nz.ac.auckland.concert.service.domain.Concert concert) {
		populateHM();

		nz.ac.auckland.concert.common.dto.ConcertDTO concertDTO = new ConcertDTO(
				concert.getId(),
				concert.getTitle(),
				concert.getDates(),
				concert.getTicketPrices(),
				concert.getPerformerIds()
		);

		return concertDTO;
	}

	private static void populateHM() {
		Concert concert = new Concert();

		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandA,
				concert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandA));
		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandB,
				concert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandB));
		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandC,
				concert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandC));
	}
}
