package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.CategoryReminder;

import java.util.Collection;

/**
 * Data access object for {@link CategoryReminder} objects. This class handles
 * requests to database.
 *
 * @author SiKelle
 */
public interface CategoryReminderDAO {

    /**
     * Find a {@link CategoryReminder} by id.
     */
    CategoryReminder findById(Long id);

    /**
     * Find all {@link CategoryReminder}.
     */
    Collection<CategoryReminder> findAll();

}
