package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.AutomaticReminder;

import java.util.Collection;

/**
 * Data access object for {@link AutomaticReminder} objects. This class handles
 * requests to database.
 *
 * @author SiKelle
 */
public interface AutomaticReminderDAO {

    /**
     * Find a {@link AutomaticReminder} by id.
     */
    AutomaticReminder findById(Long id);

    /**
     * Save a new {@link AutomaticReminder} to database, returned
     * automaticReminder is handled by entity manager.
     */
    AutomaticReminder save(AutomaticReminder automaticReminder);

    /**
     * Update a {@link AutomaticReminder} by id. Returns null in case of error
     * or not found.
     */
    AutomaticReminder update(Long autoRemId, AutomaticReminder transientAutomaticReminder);

    /**
     * Delete a {@link AutomaticReminder} by id. The returned object isn't
     * handled anymore with entitymanager. Returns null, if project isn't found.
     */
    AutomaticReminder delete(Long autoRemId);

    /**
     * Find all {@link AutomaticReminder}.
     */
    Collection<AutomaticReminder> findAll();

}
