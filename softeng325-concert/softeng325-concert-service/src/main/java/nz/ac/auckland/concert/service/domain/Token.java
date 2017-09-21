//package nz.ac.auckland.concert.service.domain;
//
//import org.apache.commons.lang3.builder.EqualsBuilder;
//import org.apache.commons.lang3.builder.HashCodeBuilder;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "TOKEN")
//public class Token {
//
//	@Id
//	@Column(name = "TOKEN_KEY")
//	private String _tokenKey;
//
//	@OneToOne
//	@JoinColumn(name = "USER")
//	private User _user;
//
//	public Token() {
//	}
//
//	public Token(String tokenKey, User user) {
//		_tokenKey = tokenKey;
//		_user = user;
//	}
//
//	public User getUser() {
//		return _user;
//	}
//
//	public String getToenKey() {
//		return _tokenKey;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof Token))
//			return false;
//		if (obj == this)
//			return true;
//
//		Token rhs = (Token) obj;
//		return new EqualsBuilder()
//				.append(this._tokenKey, rhs._tokenKey)
//				.append(this._user, rhs._user)
//				.isEquals();
//	}
//
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder(17, 31)
//				.append(_tokenKey)
//				.append(_user)
//				.hashCode();
//	}
//}
