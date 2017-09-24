package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.service.domain.jpa.SeatNumberConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@Embeddable
public class Seat {
	@Enumerated(EnumType.STRING)
	@Column(  name = "ROW", nullable= false)
	private SeatRow _row;
	
	@Column( name = "NUMBER", nullable= false)
	@Convert(converter = SeatNumberConverter.class)
	private SeatNumber _number;

	public Seat(SeatRow row, SeatNumber number) {
		_row = row;
		_number = number;
	}

	public Seat() {}

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
        return new EqualsBuilder()
				.append(_row, rhs._row)
				.append(_number, rhs._number)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(_row)
				.append(_number)
				.hashCode();
	}
	
	@Override
	public String toString() {
		String result = _row + _number.toString();

		return result;
	}
}

