package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.User;

public class UserMapper {
	static nz.ac.auckland.concert.common.dto.UserDTO toDto(nz.ac.auckland.concert.service.domain.User user) {
		nz.ac.auckland.concert.common.dto.UserDTO uDto = new UserDTO(
				user.getUsername(),
				user.getPassword(),
				user.getLastname(),
				user.getFirstname()
		);

		return uDto;
	}

	static nz.ac.auckland.concert.service.domain.User toDomainModel(nz.ac.auckland.concert.common.dto.UserDTO uDto) {
		nz.ac.auckland.concert.service.domain.User user = new User(
				uDto.getUsername(),
				uDto.getPassword(),
				uDto.getLastname(),
				uDto.getFirstname()
		);

		return user;
	}
}
