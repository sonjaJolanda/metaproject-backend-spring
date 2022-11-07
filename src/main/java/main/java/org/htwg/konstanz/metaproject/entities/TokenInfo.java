package main.java.org.htwg.konstanz.metaproject.entities;

import org.joda.time.DateTime;

/**
 * A model to store information from a json web token.
 *
 * @author FaAmbros
 */
public class TokenInfo {

	private Long userId;

	private DateTime issued;

	private DateTime expires;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public DateTime getIssued() {
		return issued;
	}

	public void setIssued(DateTime issued) {
		this.issued = issued;
	}

	public DateTime getExpires() {
		return expires;
	}

	public void setExpires(DateTime expires) {
		this.expires = expires;
	}

}
