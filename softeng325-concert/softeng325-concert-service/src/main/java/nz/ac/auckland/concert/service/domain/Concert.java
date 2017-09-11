package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false, name = "TITLE")
	private String _title;

	@Column
	@Convert(converter = LocalDateTimeConverter.class)
	private Set<LocalDateTime> _dates;


	private Map<PriceBand, BigDecimal> _tariff;


	private Set<Long> _performerIds;

	public Concert() {
	}

	public Concert(Long id, String title, Set<LocalDateTime> dates,
					  Map<PriceBand, BigDecimal> ticketPrices, Set<Long> performerIds) {
		_id = id;
		_title = title;
		_dates = new HashSet<LocalDateTime>(dates);
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performerIds = new HashSet<Long>(performerIds);
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

	public Set<Long> getPerformerIds() {
		return Collections.unmodifiableSet(_performerIds);
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
				append(_performerIds, rhs._performerIds).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_title).
				append(_dates).
				append(_tariff).
				append(_performerIds).
				hashCode();
	}
}
