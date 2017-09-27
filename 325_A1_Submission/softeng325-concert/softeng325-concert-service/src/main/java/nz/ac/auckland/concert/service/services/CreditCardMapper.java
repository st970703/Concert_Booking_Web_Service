package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.service.domain.CreditCard;

public class CreditCardMapper {
	static nz.ac.auckland.concert.common.dto.CreditCardDTO toDto(nz.ac.auckland.concert.service.domain.CreditCard cCard) {
		nz.ac.auckland.concert.common.dto.CreditCardDTO cDto = new CreditCardDTO(
				CreditCardDTO.Type.valueOf(cCard.getType().toString()),
				cCard.getName(),
				cCard.getNumber(),
				cCard.getExpiryDate()
		);

		return cDto;
	}

	static CreditCard toDomainModel(nz.ac.auckland.concert.common.dto.CreditCardDTO dtoCreditCard) {

		CreditCard creditCard = new CreditCard(
				CreditCard.Type.valueOf(dtoCreditCard.getType().toString()),
				dtoCreditCard.getName(),
				dtoCreditCard.getNumber(),
				dtoCreditCard.getExpiryDate());

		return creditCard;
	}

}
