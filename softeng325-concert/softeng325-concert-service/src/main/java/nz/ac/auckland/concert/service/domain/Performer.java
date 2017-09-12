package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement(name = "performer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Performer {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false, name = "NAME")
	private String _name;

	@Column(nullable = false, name = "IMAGE_NAME")
	private String _imageName;

	@Enumerated
	private Genre _genre;

	@OneToMany(mappedBy = "performer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@Column(name = "CONCERT_ID")
	private Set<Long> _concertIds;

	public Performer() {}

	public Performer(Long id, String name, String imageName, Genre genre, Set<Long> concertIds) {
		_id = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concertIds = new HashSet<Long>(concertIds);
	}

	public Long getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getImageName() {
		return _imageName;
	}

	public Set<Long> getConcertIds() {
		return Collections.unmodifiableSet(_concertIds);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Performer))
			return false;
		if (obj == this)
			return true;

		Performer rhs = (Performer) obj;
		return new EqualsBuilder().
				append(_name, rhs._name).
				append(_imageName, rhs._imageName).
				append(_genre, rhs._genre).
				append(_concertIds, rhs._concertIds).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_name).
				append(_imageName).
				append(_genre).
				append(_concertIds).
				hashCode();
	}
}
