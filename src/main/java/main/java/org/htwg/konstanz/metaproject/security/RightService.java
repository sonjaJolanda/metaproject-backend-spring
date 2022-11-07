package main.java.org.htwg.konstanz.metaproject.security;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.rights.UserRight;

import java.util.Collection;

/**
 * Interface for a right handler. This component is used to handle all
 * management requests with rights.
 *
 * @author SiKelle
 */
public interface RightService {

    /**
     * Get all rights of a user in a {@link UserRight} representation. This
     * could be used in frontend to decide whether methods/functions are
     * allowed.
     */
    Collection<UserRight> getAllUserRights(Long userId);

    /**
     * Start of check rights chain with given token. Return a
     * {@link RightHandler} by token.
     */
    RightHandler newRightHandler(String token);

    /**
     * Start of check rights chain with given userId. Return a
     * {@link RightHandler} by userId.
     */
    RightHandler newRightHandler(Long userId);

    /**
     * Create a relation between a specific Metaproject and all users of the system
     * Set all necessary roles (METAPROJECT_MEMBER, METAPROJECT_MEMBER_TEAMLESS)  for all users of the system.
     * <p>
     * actionUser is needed for communicationservice
     */
    void createRelationMetaToAllUserAddRoleToAllUser(Metaproject metaproject, User actionUser);

    void createRelationMetaUserAddRoleToUser(Metaproject metaproject, User assignUser, User actionUser);


    /**
     * Delete the relation between a specific Metaproject and all users of the system
     * Remove all necessary roles (METAPROJECT_MEMBER, METAPROJECT_MEMBER_TEAMLESS) for all users of the system
     * except Projectleader and Metaprojectleader
     */
    void deleteRelationMetaToAllUserRemoveRoleFromAllNormalUser(Metaproject metaproject);

    /**
     * Create a relation between all existing Metaprojects without a needed preRegistration and a new users of the system.
     * Set all necessary roles (METAPROJECT_MEMBER, METAPROJECT_MEMBER_TEAMLESS) for this users.
     */
    void createRelationNonPreRegMetaToUserAddRoleToNormalUser(User user);
}
