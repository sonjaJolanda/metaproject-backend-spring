package main.java.org.htwg.konstanz.metaproject.security;

import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.rights.RoleTypes;

/**
 * This handler is used to check for some {@link Rights} in combination with a
 * user and {@link RoleTypes}. All methods are chainable, if this Service is
 * instantiated correct with all parameters. With the method
 * {@link RightHandler#validate()} the result of a check is returned.
 *
 * @author SiKelle
 */
public interface RightHandler {

    /**
     * Get result of checked chain.
     */
    boolean validate();

    /**
     * Check whether user is {@link Rights#SUPER_USER}.
     */
    RightHandler checkForSuperUser();

    /**
     * Check for given app right.
     */
    RightHandler checkForAppRight(Rights right);

    /**
     * Check for given metaproject right with metaproject id.
     */
    RightHandler checkForMetaprojectRight(Rights right, Long metaprojectId);

    /**
     * Check for given project right with project id.
     */
    RightHandler checkForProjectRight(Rights right, Long projectId);

    /**
     * Check for given team right with team id.
     */
    RightHandler checkForTeamRight(Rights right, Long teamId);

    /**
     * Check for given user right with user id.
     */
    RightHandler checkForUserRight(Rights right, Long connectedUserId);

}