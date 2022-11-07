package main.java.org.htwg.konstanz.metaproject.mail;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * Mail service to send mails from configured mail accounts.
 * 
 * @author SiKelle
 *
 */
public interface MailService {

	/**
	 * Send a mail with given account to a sender with subject and text.
	 * 
	 * @param sender
	 * @param recipient
	 * @param subject
	 * @param text
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void send(MailAccounts sender, String recipient, String subject, String text)
			throws AddressException, MessagingException;

}