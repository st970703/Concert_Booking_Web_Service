package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;

public class NewsItemMapper {
	static nz.ac.auckland.concert.common.dto.NewsItemDTO toDto (nz.ac.auckland.concert.service.domain.NewsItem nItem) {
		nz.ac.auckland.concert.common.dto.NewsItemDTO nDto = new NewsItemDTO(
				nItem.getId(),
				nItem.getTimetamp(),
				nItem.getContent()
		);

		return nDto;
	}
}
