package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Mail service for sending mails.
 * 
 * @author SiKelle
 *
 */
@Service
public class MailServiceImpl implements MailService {

	private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	private static final String MAIL_SMTP_PORT = "mail.smtp.port";
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";
	private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String MAIL_DEBUG = "mail.debug";

	private static final String MAIL_ENCODING_CONTENT_TYPE = "text/html; charset=utf-8";

	/* (non-Javadoc)
	 * @see org.htwg.konstanz.metaproject.mail.MailService#send(org.htwg.konstanz.metaproject.mail.MailAccounts, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void send(MailAccounts sender, String recipient, String subject, String text)
			throws AddressException, MessagingException {
		Properties properties = System.getProperties();
		properties.setProperty(MAIL_DEBUG, String.valueOf(Constants.MAIL_ENABLE_DEBUGGING));
		properties.setProperty(MAIL_SMTP_STARTTLS_ENABLE, "true");
		properties.setProperty(MAIL_SMTP_HOST, sender.getSmtpHost());
		properties.setProperty(MAIL_SMTP_PORT, String.valueOf(sender.getPort()));
		properties.setProperty(MAIL_SMTP_AUTH, "true");

		Session session = Session.getInstance(properties, (Authenticator) sender.getPasswordAuthentication());

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(sender.getEmail()));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
		msg.setSubject(subject);
		msg.setContent(text, MAIL_ENCODING_CONTENT_TYPE);

		Transport.send(msg);
	}

}
