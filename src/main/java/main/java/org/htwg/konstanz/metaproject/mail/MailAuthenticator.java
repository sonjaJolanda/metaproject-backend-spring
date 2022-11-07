package main.java.org.htwg.konstanz.metaproject.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Authenticator model for sending mails.
 *
 * @author SiKeller
 */
public class MailAuthenticator extends Authenticator {

    private String user;
    private String password;

    /**
     * Instantiate this mail authenticator with user and password.
     *
     * @param user
     * @param password
     */
    public MailAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     *
     * This method is called automatically on request and must be implemented.
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }
}
