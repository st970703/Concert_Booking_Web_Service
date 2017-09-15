package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.PerformerDTO;

/**
 * Helper class to convert between domain-model and DTO objects representing
 *
 */
public class PerformerMapper {
	static nz.ac.auckland.concert.common.dto.PerformerDTO toDto(nz.ac.auckland.concert.service.domain.Performer performer){

		nz.ac.auckland.concert.common.dto.PerformerDTO performerDTO = new PerformerDTO(
				performer.getId(),
				performer.getName(),
				performer.getImageName(),
				performer.getGenre(),
				performer.getConcertIds()

		);

		return performerDTO;
	}

}
