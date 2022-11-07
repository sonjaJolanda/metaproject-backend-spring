package main.java.org.htwg.konstanz.metaproject.services;

/**
 * This service is used to deliver information regarding roles.
 *
 * @author LaSiefermann
 */
public interface RoleServiceOld {

    /**
     * Get a count of connected users to a specific role.
     */
    int getRelatedUserToRoleByCount(Long roleId);

}
