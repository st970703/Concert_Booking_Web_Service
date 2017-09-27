package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;

import java.util.HashSet;
import java.util.Set;

public class NewsItemListenerImp implements ConcertService.NewsItemListener {

	private Set<NewsItemDTO> _nDtos = new HashSet<>();

	@Override
	public void newsItemReceived(NewsItemDTO newsItem) {
		_nDtos.add(newsItem);
	}

	public Set<NewsItemDTO> getNewsItems() {
		return _nDtos;
	}
}
