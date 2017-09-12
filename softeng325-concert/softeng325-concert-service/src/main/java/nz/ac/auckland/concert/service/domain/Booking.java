package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;

@Embeddable
public class Booking {

	@ManyToOne
	@JoinColumn(name = "CONCERT_ID")
	private Long _concertId;

	@Column(nullable = false, name = "ConcertTitle")
	private String _concertTitle;

	@Column
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _dateTime;

	@ElementCollection
	private Set<Seat> _seats;

	@Enumerated
	private PriceBand _priceBand;

	public Booking() {
	}

	public Booking(Long concertId, String concertTitle,
				   LocalDateTime dateTime, Set<Seat> seats, PriceBand priceBand) {
		_concertId = concertId;
		_concertTitle = concertTitle;
		_dateTime = dateTime;

		_seats = new HashSet<Seat>();
		_seats.addAll(seats);

		_priceBand = priceBand;
	}

	public Long getConcertId() {
		return _concertId;
	}

	public String getConcertTitle() {
		return _concertTitle;
	}

	public LocalDateTime getDateTime() {
		return _dateTime;
	}

	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}

	public PriceBand getPriceBand() {
		return _priceBand;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Booking))
			return false;
		if (obj == this)
			return true;

		Booking rhs = (Booking) obj;
		return new EqualsBuilder().append(_concertId, rhs._concertId)
				.append(_concertTitle, rhs._concertTitle)
				.append(_dateTime, rhs._dateTime)
				.append(_seats, rhs._seats)
				.append(_priceBand, rhs._priceBand).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(_concertId)
				.append(_concertTitle).append(_dateTime).append(_seats)
				.append(_priceBand).hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("concert: ");
		buffer.append(_concertTitle);
		buffer.append(", date/time ");
		buffer.append(_seats.size());
		buffer.append(" ");
		buffer.append(_priceBand);
		buffer.append(" seats.");
		return buffer.toString();
	}
}
