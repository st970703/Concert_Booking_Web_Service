package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Entity
public class Reservation {
	@Id
	@GeneratedValue
	private Long _id;

	@Enumerated
	PriceBand _seatType;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Concert _concert;

	@Column(nullable = false)
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;

	@OneToMany(mappedBy= "reservation")
	@Column(nullable = false)
	private Set<Seat> _seats;

	@ManyToOne
	@JoinColumn
	private Concert concert;

	@Column(nullable = false)
	private boolean _confirmed;

	public Reservation() {}

	public Reservation(PriceBand seatType, Concert concert, LocalDateTime date, Set<Seat> seats, boolean confirmed) {
		_seatType = seatType;
		_concert = concert;
		_date = date;
		_seats = seats;
		_confirmed = confirmed;
	}

	public PriceBand getSeatType() {
		return _seatType;
	}

	public LocalDateTime getDate() {
		return _date;
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
				append(_date, rhs._date).
				append(_seats, rhs._seats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_seatType).
				append(_date).
				append(_seats).
				hashCode();
	}
}

