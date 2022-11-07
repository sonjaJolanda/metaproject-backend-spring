package main.java.org.htwg.konstanz.metaproject.communication;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "EmailTemplate")
public class EmailTemplate {

	@Id
	@Enumerated(EnumType.STRING)
	private EmailTemplateType emailTemplateType;

	@Lob
	@NotNull(message = "template is null")
	@Column(name = "template", length = 16000)
	private String template;

	@NotNull(message = "subject is null")
	@Column(name = "subject")
	private String subject;

	public EmailTemplateType getEmailTemplateType() {
		return emailTemplateType;
	}

	public void setEmailTemplateType(EmailTemplateType emailTemplateType) {
		this.emailTemplateType = emailTemplateType;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
