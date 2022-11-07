package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.entities.User;

/**
 * This service makes an user authentication with LDAP.
 *
 * @author SiKelle
 */
public interface LDAPService {

    /**
     * Verify a user with username and password against LDAP system. Returns
     * null in case of error.
     */
    User checkUserCredentials(String userName, String userPass);

}