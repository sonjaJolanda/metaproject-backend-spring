package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplate;
import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplateType;

/**
 * Data access object for {@link EmailTemplate} objects.
 * 
 * @author SiKelle
 *
 */
public interface EmailTemplateDAO {

	/**
	 * Find an email template by id. The found template is handled by entity
	 * manager.
	 */
	EmailTemplate findById(EmailTemplateType type);

}