package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table
public class Concert implements Comparable<Concert> {
	@Id
	@GeneratedValue
	@Column( nullable = false)
	private Long _cId;

	@Column(nullable = false)
	private String _title;

	@ElementCollection
	@Convert(converter = LocalDateTimeConverter.class)
	@CollectionTable(
			joinColumns = @JoinColumn(name = "_cId")
	)
	@Column(nullable = false)
	private Set<LocalDateTime> _dates;

	public Map<PriceBand, BigDecimal> getTicketPrices() {
		return _tariff;
	}

	@ElementCollection
	@CollectionTable(joinColumns = @JoinColumn(name = "TARIFF"))
	@MapKeyColumn( name = "PRICEBAND")
	@MapKeyClass(PriceBand.class)
	@MapKeyEnumerated(EnumType.STRING)
	@Column
	private Map<PriceBand, BigDecimal> _tariff;

	@Column(nullable = false)
	@ManyToMany(mappedBy= "performers", cascade = {CascadeType.PERSIST})
	@JoinTable(
			name = "CONCERT_PERFORMER",
			joinColumns = @JoinColumn(name = "C_ID"),
			inverseJoinColumns = @JoinColumn(name = "P_ID")
	)
	private Set<Performer> _performers;

	public Concert() {
	}

	public Concert(Long id, String title, Set<LocalDateTime> dates,
					  Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers) {
		_cId = id;
		_title = title;
		_dates = new HashSet<LocalDateTime>(dates);
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performers = new HashSet<Performer>(performers);
	}

	public Long getId() {
		return _cId;
	}

	public String getTitle() {
		return _title;
	}

	public Set<LocalDateTime> getDates() {
		return Collections.unmodifiableSet(_dates);
	}

	public BigDecimal getTicketPrice(PriceBand seatType) {
		return _tariff.get(seatType);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert))
			return false;
		if (obj == this)
			return true;

		Concert rhs = (Concert) obj;
		return new EqualsBuilder().
				append(_title, rhs._title).
				append(_dates, rhs._dates).
				append(_tariff, rhs._tariff).
				append(_performers, rhs._performers).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_title).
				append(_dates).
				append(_tariff).
				append(_performers).
				hashCode();
	}

	public Set<Performer> getPerformers() {
		return _performers;
	}

	public Set<Long> getPerformerIds() {
		Set<Long> result = _performers
				.stream()
				.map(performer -> performer.getId())
				.collect(Collectors.toSet());

		return result;
	}

	@Override
	public String toString() {
		//todo
		StringBuffer buffer = new StringBuffer();
		buffer.append("Concert, id: ");
		buffer.append(_cId);
		buffer.append(", title: ");
		buffer.append(_title);
		buffer.append(", date: ");
		buffer.append(_dates.toString());
		buffer.append(", featuring: ");
		for (Performer performer: _performers) {
			buffer.append(performer.getName()+", ");
		}

		return buffer.toString();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}

	//helper

}
