package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;

public class NewsItemMapper {
	static nz.ac.auckland.concert.common.dto.NewsItemDTO toDto(nz.ac.auckland.concert.service.domain.NewsItem nItem) {
		nz.ac.auckland.concert.common.dto.NewsItemDTO nDto = new NewsItemDTO(
				nItem.getId(),
				nItem.getTimetamp(),
				nItem.getContent()
		);

		return nDto;
	}

	public static nz.ac.auckland.concert.service.domain.NewsItem toDomainModel(nz.ac.auckland.concert.common.dto.NewsItemDTO nDto) {
		nz.ac.auckland.concert.service.domain.NewsItem nItem = new NewsItem(
				nDto.getId(),
				nDto.getTimetamp(),
				nDto.getContent()
		);

		return nItem;
	}
}
