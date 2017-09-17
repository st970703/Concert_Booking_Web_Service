package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;

public class CreditCardMapper {
	static nz.ac.auckland.concert.common.dto.CreditCardDTO toDto(nz.ac.auckland.concert.service.domain.CreditCard cCard) {
		nz.ac.auckland.concert.common.dto.CreditCardDTO cDto = new CreditCardDTO (
				CreditCardDTO.Type.valueOf(cCard.getType().toString()),
				cCard.getName(),
				cCard.getNumber(),
				cCard.getExpiryDate()
		);

		return cDto;
	}

}
