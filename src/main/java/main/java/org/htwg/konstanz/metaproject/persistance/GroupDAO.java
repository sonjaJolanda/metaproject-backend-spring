package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;

import java.util.Collection;

/**
 * Interface for group data access object.
 *
 * @author SiKelle
 */
public interface GroupDAO {

    /**
     * Save a new group to database. The returned group object is handled by
     * entity manager and contains the generated id.
     */
    UserGroup save(UserGroup group);

    /**
     * Update a group by id, the returned group is handled by entity manager. If
     * group isn't found by id, a null value is returned.
     */
    UserGroup update(UserGroup transientGroup, Long id);

    /**
     * Find a group by id, if no group is found, a null value is returned.
     */
    UserGroup findById(Long id);

    /**
     * Delete a group by id. The removed group is returned and if no group is
     * found with that id, null is returned.
     */
    UserGroup remove(Long id);

    Collection<UserGroup> findAll();

}