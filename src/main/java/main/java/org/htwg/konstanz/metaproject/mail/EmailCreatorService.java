package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.Email;
import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplateType;

import java.util.Collection;

/**
 * This service can be used to create an {@link Email} object for a
 * {@link CommAbstract} object with templates in database.
 * 
 * @author SiKelle
 *
 */
public interface EmailCreatorService {

	/**
	 * Create all emails (that is only one in the most cases) for a
	 * communication object extending {@link CommAbstract} with an email
	 * template of a specific type. Returns an error email in case of exception.
	 * 
	 * @param comm
	 * @param emailType
	 * @return
	 */
	public <T extends CommAbstract> Collection<Email> getEmailForType(T comm, EmailTemplateType emailType);

}