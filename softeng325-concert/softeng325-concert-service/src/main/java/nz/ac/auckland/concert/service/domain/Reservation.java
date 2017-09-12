package nz.ac.auckland.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
public class Reservation {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false, name = "RESERVATION_REQUEST")
	private ReservationRequest _request;

	@ElementCollection
	private Set<Seat> _seats;

	public Reservation() {}

	public Reservation(Long id, ReservationRequest request, Set<Seat> seats) {
		_id = id;
		_request = request;
		_seats = new HashSet<Seat>(seats);
	}

	public Long getId() {
		return _id;
	}

	public ReservationRequest getReservationRequest() {
		return _request;
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
				append(_request, rhs._request).
				append(_seats, rhs._seats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_request).
				append(_seats).
				hashCode();
	}
}
*/