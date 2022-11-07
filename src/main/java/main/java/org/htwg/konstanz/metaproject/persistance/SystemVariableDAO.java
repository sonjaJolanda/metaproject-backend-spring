package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.SystemVariable;

import java.util.Collection;

/**
 * Data access object for User class.
 *
 * @author SiKelle
 */
public interface SystemVariableDAO {

    SystemVariable save(SystemVariable secret);

    /**
     * Update existing user with user id. If user does not exist, null is
     * returned. The passed instance in will not be managed (any changes will
     * not be part of the transaction - unless you call update again).
     */
    SystemVariable update(SystemVariable transientSecret, String secretKey);

    /**
     * Find a secret by its key.
     */
    SystemVariable findByKey(String secretKey);

    /**
     * Find a list of all stored secrets.
     */
    Collection<SystemVariable> findAll();

    /**
     * Remove existing user by id from database. If user isn't found in database
     * this method returns null instead of removed user.
     */
    Collection<SystemVariable> remove(String secretKey);

    /**
     * gets the Variable for given key.
     */
    String getVariableKey(String key, String fallbackValue);

}
