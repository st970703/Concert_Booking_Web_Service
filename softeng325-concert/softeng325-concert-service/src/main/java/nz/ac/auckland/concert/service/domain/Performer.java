package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Performer {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false)
	private String _name;

	@Column(nullable = false)
	private String _imageName;

	public Genre getGenre() {
		return _genre;
	}

	@Enumerated
	@Column(nullable = false)
	private Genre _genre;

	public Set<Concert> getConcerts() {
		return _concerts;
	}

	@ManyToMany(mappedBy = "_performers")
	@Column(nullable = false)
	private Set<Concert> _concerts;

	public Performer() {}

	public Performer(Long id, String name, String imageName, Genre genre, Set<Concert> concerts) {
		_id = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concerts = new HashSet<Concert>(concerts);
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
				append(_concerts, rhs._concerts).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_name).
				append(_imageName).
				append(_genre).
				append(_concerts).
				hashCode();
	}

	public Set<Long> getConcertIds() {
		Set<Long> result = _concerts
				.stream()
				.map(concert -> concert.getId())
				.collect(Collectors.toSet());

		return result;
	}
}
