package nz.ac.auckland.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
	@Id
	@Column(nullable = false, name = "USERNAME")
	private String _username;

	@Column(nullable = false, name = "PASSWORD")
	private String _password;

	@Column(name = "FIRST_NAME")
	private String _firstname;

	@Column(name = "LAST_NAME")
	private String _lastname;

	@Column(name = "TOKEN")
	private String _tokenKey;

	public Set<Reservation> getReservations() {
		return _reservations;
	}

	@OneToMany(fetch = FetchType.EAGER,
			cascade = CascadeType.ALL)
	@JoinColumn(name = "RESERVATION")
	private Set<Reservation> _reservations;

	private CreditCard _cCard;

	public User() {
	}

	public User(String username, String password, String lastname, String firstname) {
		this(username, password, lastname, firstname, null, null);
	}

	public User(String username, String password, String lastname, String firstname, CreditCard cCard, Set<Reservation> reservations) {
		_username = username;
		_password = password;
		_lastname = lastname;
		_firstname = firstname;
		_reservations = reservations;
		_cCard = cCard;
	}

	public User(String username, String password) {
		this(username, password, null, null, null, null);
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

	public void setCreditCard(CreditCard creditCard) {
		_cCard = creditCard;
	}

	public String getFirstname() {
		return _firstname;
	}

	public String getLastname() {
		return _lastname;
	}

	public String getId() {
		return _username;
	}

	public String getToken() {
		return _tokenKey;
	}

	public void setToken(String tokenKey) {
		if (tokenKey != null) {
			_tokenKey = tokenKey;
		}
	}

	public void setReservation(Set<Reservation> reservations) {
		_reservations = reservations;
	}

	public void addReservation(Reservation reservation) {
		_reservations.add(reservation);
	}

	public void removeReservation(Reservation reservation) {
		_reservations.remove(reservation);
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
