package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Embeddable
public class ReservationRequest {

	@Column(nullable = false, name = "NUMBER_OF_SEATS")
	private int _numberOfSeats;

	@Column(nullable = false, name = "SEAT_TYPE")
	private PriceBand _seatType;

	@ManyToOne
	@JoinColumn(name = "CONCERT_ID")
	private Long _concertId;


	private LocalDateTime _date;

	public ReservationRequest() {}

	public ReservationRequest(int numberOfSeats, PriceBand seatType, Long concertId, LocalDateTime date) {
		_numberOfSeats = numberOfSeats;
		_seatType = seatType;
		_concertId = concertId;
		_date = date;
	}

	public int getNumberOfSeats() {
		return _numberOfSeats;
	}

	public PriceBand getSeatType() {
		return _seatType;
	}

	public Long getConcertId() {
		return _concertId;
	}

	public LocalDateTime getDate() {
		return _date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReservationRequest))
			return false;
		if (obj == this)
			return true;

		ReservationRequest rhs = (ReservationRequest) obj;
		return new EqualsBuilder().
				append(_numberOfSeats, rhs._numberOfSeats).
				append(_seatType, rhs._seatType).
				append(_concertId, rhs._concertId).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_numberOfSeats).
				append(_seatType).
				append(_concertId).
				append(_date).
				hashCode();
	}
}
