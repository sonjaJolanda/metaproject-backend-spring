package main.java.org.htwg.konstanz.metaproject.mail;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;

/**
 * Configuration of all mail accounts for sending mails from Metaproject.
 *
 * @author SiKelle
 *
 */
public enum MailAccounts {

    /**
     * Default HTWP metaproject account.
     */

    HTWG("asmtp.htwg-konstanz.de", 25, Constants.CONTACT_MAIL_USERNAME, Constants.CONTACT_MAIL_PASSWORD, "metaproject@htwg-konstanz.de");

    private String smtpHost;
    private int port;
    private String username;
    private String password;
    private String email;

    private MailAccounts(String smtpHost, int port, String username, String password, String email)
    {
        this.smtpHost = smtpHost;
        this.port = port;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getPort()
    {
        return port;
    }

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public MailAuthenticator getPasswordAuthentication()
    {
        return new MailAuthenticator(username, password);
    }

    public String getEmail()
    {
        return email;
    }
}

