package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table
public class Performer {
	@Id
	@GeneratedValue
	@Column( nullable = false)
	private Long _pId;

	@Column(nullable = false)
	private String _name;

	@Column(nullable = false)
	private String _imageName;

	public Genre getGenre() {
		return _genre;
	}

	@Enumerated(EnumType.STRING)
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
		_pId = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concerts = new HashSet<Concert>(concerts);
	}

	public Performer(Long id, String name, String imageName, Genre genre) {
		this(id, name, imageName, genre, null);
	}

	public Performer(String name, String imageName, Genre genre) {
		this(null, name, imageName, genre, null);
	}

	public Long getId() {
		return _pId;
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
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("Performer, id: ")
				.append(_pId)
				.append(", name: ")
				.append(_name)
				.append(", s3 image: ")
				.append(_imageName)
				.append(", genre: ")
				.append(_genre.toString());

		return buffer.toString();
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
