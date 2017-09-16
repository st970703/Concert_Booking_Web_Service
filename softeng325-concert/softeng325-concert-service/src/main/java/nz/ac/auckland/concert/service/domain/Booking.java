package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;

@Embeddable
public class Booking {
	private Long _id;

	private Map<PriceBand, Set<Seat>> bookedSeats;

	@ManyToOne
	@JoinColumn(name = "CONCERT_ID", nullable = false)
	private Concert _concert;

	@Column(nullable = false)
	private String _concertTitle;

	@Column(nullable = false)
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _dateTime;

	@OneToMany( mappedBy = "booking", cascade = {CascadeType.PERSIST})
	private Set<Seat> _seats;

	@Enumerated(EnumType.STRING)
	private PriceBand _priceBand;

	public Booking() {
	}

	public Booking(Concert concert, String concertTitle,
				   LocalDateTime dateTime, Set<Seat> seats, PriceBand priceBand) {
		_concert = concert;
		_concertTitle = concertTitle;
		_dateTime = dateTime;

		_seats = new HashSet<Seat>();
		_seats.addAll(seats);

		_priceBand = priceBand;
	}

	public Long getId() {
		return _id;
	}

	public Concert getConcert() {
		return _concert;
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
		return new EqualsBuilder()
				.append(_concert.getId(), rhs._concert.getId())
				.append(_concertTitle, rhs._concertTitle)
				.append(_dateTime, rhs._dateTime)
				.append(_seats, rhs._seats)
				.append(_priceBand, rhs._priceBand).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(_concert.getId())
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
