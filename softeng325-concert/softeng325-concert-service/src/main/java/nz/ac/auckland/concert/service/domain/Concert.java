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
public class Concert {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false)
	private String _title;

	@ElementCollection
	@Convert(converter = LocalDateTimeConverter.class)
	private Set<LocalDateTime> _dates;

	public Map<PriceBand, BigDecimal> getTicketPrices() {
		return _tariff;
	}

	@ElementCollection
	@MapKeyColumn
	private Map<PriceBand, BigDecimal> _tariff;

	@Column(nullable = false)
	@ManyToMany(mappedBy= "performers")
	private Set<Performer> _performers;

	public Concert() {
	}

	public Concert(Long id, String title, Set<LocalDateTime> dates,
					  Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers) {
		_id = id;
		_title = title;
		_dates = new HashSet<LocalDateTime>(dates);
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performers = new HashSet<Performer>(performers);
	}

	public Long getId() {
		return _id;
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
}
