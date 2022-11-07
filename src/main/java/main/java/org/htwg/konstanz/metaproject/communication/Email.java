package main.java.org.htwg.konstanz.metaproject.communication;

import main.java.org.htwg.konstanz.metaproject.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This class represents an email. The email could have many states, whether it
 * is already send or not.
 * 
 * @author SiKelle
 *
 */
@Entity
@Table(name = "Email")
public class Email {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull(message = "receiver is null")
	@ManyToOne
	private User receiver;

	@NotNull(message = "subject is null")
	@Column(name = "subject")
	private String subject;

	@Lob
	@NotNull(message = "body is null")
	@Column(name = "body", length = 16000)
	private String body;

	@NotNull(message = "status is null")
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EmailStatus status = EmailStatus.WAITING;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public EmailStatus getStatus() {
		return status;
	}

	public void setStatus(EmailStatus status) {
		this.status = status;
	}

}
