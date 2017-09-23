package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
public class Seat {

	public enum Availability {Reserved, Booked, Available};

	@Id
	@GeneratedValue
	private Long _id;

	@ManyToOne
	private Concert _concert;

	@Column(nullable = false, name = "ROW")
	private SeatRow _row;

	@Column(nullable = false, name = "NUMBER")
	private SeatNumber _number;

	@Column(name = "AVAILABILITY")
	private Availability _status = Availability.Available;

	public Seat() {}

	public Seat(SeatRow row, SeatNumber number, Concert concert) {
		_row = row;
		_number = number;
		_concert = concert;
	}

	public SeatRow getRow() {
		return _row;
	}

	public SeatNumber getNumber() {
		return _number;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;
		if (obj == this)
			return true;

		Seat rhs = (Seat) obj;
		return new EqualsBuilder().
				append(_row, rhs._row).
				append(_number, rhs._number).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_row).
				append(_number).
				hashCode();
	}

	@Override
	public String toString() {
		return _row + _number.toString();
	}
}

