package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.RelationMetaprojectUser;
import main.java.org.htwg.konstanz.metaproject.entities.User;

import java.util.Collection;

/**
 * Data access object for resource relation metaproject user.
 */
public interface RelationMetaprojectUserDAO {

    /**
     * Save a new relation in database. The new created and handled entity is
     * returned as copy of inserted parameter, updated with generated id.
     */
    RelationMetaprojectUser save(RelationMetaprojectUser transRel);

    /**
     * Find a relation by user and metaproject.
     */
    RelationMetaprojectUser findByUserAndMetaproject(User user, Metaproject metaproject);

    /**
     * Delete relations by metaproject
     */
    Collection<RelationMetaprojectUser> deleteByMetaproject(Metaproject metaproject);

    /**
     * Find all relations by metaproject
     */
    Collection<RelationMetaprojectUser> findByMetaproject(Metaproject metaproject);

    /**
     * Find all relations by user
     */
    Collection<RelationMetaprojectUser> findByUser(User user);

    /**
     * Delete a relation by metaproject and user
     */
    void deleteByMetaprojectAndUser(Metaproject metaproject, User user);

    RelationMetaprojectUser delete(RelationMetaprojectUser relationMetaprojectUser);

}
