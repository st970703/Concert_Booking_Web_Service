//package nz.ac.auckland.concert.service.domain;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
//import org.apache.commons.lang3.builder.EqualsBuilder;
//import org.apache.commons.lang3.builder.HashCodeBuilder;
//
//import nz.ac.auckland.concert.common.types.PriceBand;
//
//import javax.persistence.*;
//
//@Entity
//public class Booking {
//	@Id
//	@GeneratedValue
//	private Long _bId;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="CONCERT", nullable = false )
//	private Concert _concert;
//
//	@Column( name = "DATE", nullable= false)
//	@Convert(converter = LocalDateTimeConverter.class)
//	private LocalDateTime _dateTime;
//
//	@ElementCollection(fetch = FetchType.LAZY)
//	@CollectionTable(name = "SEAT",joinColumns= @JoinColumn( name = "BID" ) )
//	@Column( name = "SEAT" )
//	private Set<Seat> _seats;
//
//	@Enumerated(EnumType.STRING)
//	@Column( nullable= false, name = "PRICEBAND" )
//	private PriceBand _priceBand;
//
//	public Booking() {
//	}
//
//	public Booking( Concert concert, LocalDateTime dateTime, Set<Seat> seats, PriceBand priceBand) {
//
//		_dateTime = dateTime;
//		_concert = concert;
//		_seats = new HashSet<Seat>();
//		_seats.addAll(seats);
//
//		_priceBand = priceBand;
//	}
//
//	public Long getId() {
//		return _bId;
//	}
//
//	public Concert getConcert() {
//		return _concert;
//	}
//
//	public LocalDateTime getDateTime() {
//		return _dateTime;
//	}
//
//	public Set<Seat> getSeats() {
//		return Collections.unmodifiableSet(_seats);
//	}
//
//	public void addReservation(Seat seat) {
//		_seats.add(seat);
//	}
//
//	public PriceBand getPriceBand() {
//		return _priceBand;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof Seat))
//			return false;
//		if (obj == this)
//			return true;
//
//		Booking rhs = (Booking) obj;
//		return new EqualsBuilder()/*.append(_concertId, rhs._concertId)
//				.append(_concertTitle, rhs._concertTitle)*/
//				.append(_dateTime, rhs._dateTime)
//				.append(_seats, rhs._seats)
//				.append(_priceBand, rhs._priceBand).isEquals();
//	}
//
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder(17, 31)/*.append(_concertId)
//				.append(_concertTitle)*/.append(_dateTime).append(_seats)
//				.append(_priceBand).hashCode();
//	}
//
//	@Override
//	public String toString() {
//		StringBuffer buffer = new StringBuffer();
///*		buffer.append("concert: ");
//		buffer.append(_concertTitle);*/
//		buffer.append(", date/time ");
//		buffer.append(_seats.size());
//		buffer.append(" ");
//		buffer.append(_priceBand);
//		buffer.append(" seats.");
//		return buffer.toString();
//	}
//}
