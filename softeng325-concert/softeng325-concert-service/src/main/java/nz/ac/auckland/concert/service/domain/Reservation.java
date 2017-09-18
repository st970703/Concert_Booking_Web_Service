package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Entity
public class Reservation {
	@Id
	@GeneratedValue
	private Long _id;

	@Enumerated
	@Column(name = "SEAT_TYPE")
	PriceBand _seatType;

	@ManyToOne
	@JoinColumn(nullable = false, name = "CONCERT")
	private Concert _concert;

	@OneToMany
	@Column(nullable = false, name = "SEAT")
	private Set<Seat> _seats;

	@Column(nullable = false, name = "CONFIRMED")
	private boolean _confirmed;

	public Concert getConcert() {
		return _concert;
	}

	public Reservation() {}

	public Reservation(PriceBand seatType, Concert concert, Set<Seat> seats) {
		_seatType = seatType;
		_concert = concert;
		_seats = seats;
		_confirmed = false;
	}

	public PriceBand getSeatType() {
		return _seatType;
	}

	public Long getId() {
		return _id;
	}

	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reservation))
			return false;
		if (obj == this)
			return true;

		Reservation rhs = (Reservation) obj;
		return new EqualsBuilder().
				append(_seatType, rhs._seatType).
				append(_seats, rhs._seats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_seatType).
				append(_seats).
				hashCode();
	}
}

