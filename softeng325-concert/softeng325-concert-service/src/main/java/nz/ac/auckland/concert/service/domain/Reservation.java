package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Reservation {
	@Id
	@GeneratedValue
	@Column( name = "RID" )
	private Long _rId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PRICEBAND", nullable= false )
	private PriceBand _seatType;
	
	@ManyToOne(fetch = FetchType.LAZY )
	@JoinColumn(name="_cId",nullable = false )
	private Concert _concert;
	
	@Column(name = "DATE", nullable = false )
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;
	
	@ElementCollection(fetch = FetchType.LAZY )
	@CollectionTable(name = "RESERVATION_SEAT",joinColumns= @JoinColumn( name = "RID" ) )
	@Column( name = "SEAT" )
	private Set<Seat> _seats;
	
	@Column(nullable = false, name = "BOOKING_ID" )
	private Long _bId;
	
	@Column(nullable = false, name = "CONFIRMED" )
	private boolean _confirmed;
	
	public Reservation() {}
	
	public Reservation(PriceBand seatType, Concert concert, LocalDateTime date , Set<Seat> seats, Long bookingId) {
		_seatType = seatType;
		_concert = concert;
		_date = date;
		_seats = new HashSet<>(seats);
		_bId = bookingId;
		_confirmed = false;
	}
	
	public Long getId() {
		return _rId;
	}
	
	public PriceBand getSeatType() {
		return _seatType;
	}
	
	public Concert getConcert() {
		return _concert;
	}
	
	public LocalDateTime getDate() {
		return _date;
	}
	
	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}
	
	public Long getBookingId() {
		return _bId;
	}
	
	public boolean getCConfirmed() {
		return _confirmed;
	}
	
	public void setConfirmed(boolean status) {
		_confirmed = status;
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
        	append(_concert, rhs._concert).
        	append(_date, rhs._date).
            append(_seats, rhs._seats).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	        	append(_seatType).
	        	append(_concert).
	        	append(_date).
	            append(_seats).
	            hashCode();
	}
	

}
