package nz.ac.auckland.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
	@Id
	@GeneratedValue
	private Long  _uId;

	@Column(nullable = false, name = "USERNAME")
	private String _username;

	@Column(nullable = false, name = "PASSWORD")
	private String _password;

	@Column(nullable = false, name = "FIRST_NAME")
	private String _firstname;

	@Column(nullable = false, name = "LAST_NAME")
	private String _lastname;

	private CreditCard _cCard;

	@OneToMany
	@JoinColumn(name = "RESERVATION")
	private Set<Reservation> _reservations;

	protected User() {}

	public User(String username, String password, String lastname, String firstname, CreditCard cCard, Set<Reservation> reservations) {
		_username = username;
		_password = password;
		_lastname = lastname;
		_firstname = firstname;
		_cCard = cCard;
		_reservations = reservations;
	}

	public User(String username, String password) {
		this(username, password, null, null, null, null);
	}

	public Long getId() {
		return _uId;
	}
	
	public CreditCard getCreditCard() {
		return _cCard;
	}

	public String getUsername() {
		return _username;
	}

	public String getPassword() {
		return _password;
	}

	public String getFirstname() {
		return _firstname;
	}

	public String getLastname() {
		return _lastname;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;
		if (obj == this)
			return true;

		User rhs = (User) obj;
		return new EqualsBuilder().
				append(_username, rhs._username).
				append(_password, rhs._password).
				append(_firstname, rhs._firstname).
				append(_lastname, rhs._lastname).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_username).
				append(_password).
				append(_firstname).
				append(_password).
				hashCode();
	}
}
