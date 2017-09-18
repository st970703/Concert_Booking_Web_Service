package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "PERFORMERS")
public class Performer {
	@Id
	@GeneratedValue
	private Long _pId;

	@Column(nullable = false, name = "NAME")
	private String _name;

	@Column(nullable = false, name = "IMAGE_NAME" )
	private String _imageName;

	@Enumerated(EnumType.STRING)
	private Genre _genre;

	@ManyToMany(mappedBy = "_performers")
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

	public Set<Concert> getConcerts() {
		return _concerts;
	}

	public Genre getGenre() {
		return _genre;
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
