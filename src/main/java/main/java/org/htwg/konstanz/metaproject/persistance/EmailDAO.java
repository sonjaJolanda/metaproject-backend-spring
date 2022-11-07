package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.Email;
import main.java.org.htwg.konstanz.metaproject.communication.EmailStatus;

import java.util.Collection;

/**
 * Data access object for {@link Email} class.
 *
 * @author SiKelle
 */
public interface EmailDAO {

    /**
     * Save a new email in database. The default status for this email is
     * {@link EmailStatus#WAITING}. The returned email is handled.
     */
    Email save(Email email);

    /**
     * Update existing email with id. If email does not exist, null is returned.
     * The passed instance in will not be managed (any changes will not be part
     * of the transaction - unless you call update again).
     */
    Email update(Email transientEmail, Long id);

    /**
     * Update all emails in given collection.
     */
    Collection<Email> updateAll(Collection<Email> transientEmails);

    Email findById(Long id);

    /**
     * Find a collection of emails, which are not send and already waiting.
     */
    Collection<Email> findAllWaitingEmails();

}
