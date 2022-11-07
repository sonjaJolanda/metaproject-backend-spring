package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.rights.*;

import java.util.Collection;

/**
 * Interface for data access object for roles.
 *
 * @author SiKelle
 */
public interface RoleDAO {

    /**
     * Save a new role to database. The returned role object is handled by
     * entity manager and contains the generated id. The type describes the
     * subclass of this role, the role type. This method checks for valid rights
     * to a role and throws an {@link IllegalArgumentException} in case of
     * error.
     */
    <T extends RoleAbstract> T save(T role, Class<T> type) throws IllegalArgumentException;

    /**
     * Update a role by id, the returned role is handled by entity manager. If
     * role isn't found by id, a null value is returned. The type describes the
     * subclass of this role, the role type. This method checks for valid rights
     * to a role and throws an {@link IllegalArgumentException} in case of
     * error.
     */
    <T extends RoleAbstract> T update(T transientRole, Long id, Class<T> type) throws IllegalArgumentException;

    /**
     * Find a role by id, if no role is found, a null value is returned. The
     * role type describes the subclass of this role, the right class is found
     * by action pattern.
     */
    <T extends RoleAbstract> T findById(Long id, Class<T> type);

    /**
     * Find all relation {@link RelationUserRoleAbstract} of a user by id and
     * given type. The user must have an id.
     */
    <T extends RelationUserRoleAbstract> Collection<T> findByUser(User user, Class<T> type);

    /**
     * Find a {@link RelationUserRoleAbstract} by role to get all {@link User}
     * with that role. This method should only be used in combination with
     * {@link RoleDAO#findUsersByRoleIdCount(Long, Class)} to avoid to much
     * traffic and too large results.
     */
    <T extends RelationUserRoleAbstract> Collection<T> findUsersByRoleId(Long roleId, Class<T> type);

    /**
     * Find a {@link RelationUserRoleAbstract} by role to get all {@link User}
     * with that role and return the count of this query.
     */
    <T extends RelationUserRoleAbstract> Long findUsersByRoleIdCount(Long roleId, Class<T> type);

    /**
     * Find a default role by its default role key, if no role is found, a null
     * value is returned. The type describes the subclass of this role, the role
     * type.
     */
    <T extends RoleAbstract> T findDefaultByKey(DefaultRoles role, Class<T> type);

    /**
     * Delete a role by id. The removed role is returned and if no role is found
     * with that id, null is returned. The type describes the subclass of this
     * role, the role type. If the role is a default role it could not be
     * deleted, then null is returned.
     */
    <T extends RoleAbstract> T remove(Long id, Class<T> type);

    /**
     * Add a user (important, that user id is set!!) to {@link DefaultRoles} of
     * type {@link RoleTypes#APP}. Returns the updated and handled relation role
     * user object or null if role isn't found. If user already has role, no new
     * relation is created and the existing relation is returned.
     */
    RelationUserRoleApp addRoleAppToUser(User user, DefaultRoles role);

    /**
     * Add a user (important, that user id is set!!) to {@link DefaultRoles} of
     * type {@link RoleTypes#METAPROJECT}. Returns the updated and handled
     * relation role user object or null if role isn't found. If user already
     * has role, no new relation is created and the existing relation is
     * returned.
     */
    RelationUserRoleMetaproject addRoleMetaprojectToUser(User user, Metaproject metaproject, DefaultRoles role);

    /**
     * Add a user (important, that user id is set!!) to {@link DefaultRoles} of
     * type {@link RoleTypes#PROJECT}. Returns the updated and handled relation
     * role user object or null if role isn't found. If user already has role,
     * no new relation is created and the existing relation is returned.
     */
    RelationUserRoleProject addRoleProjectToUser(User user, Project project, DefaultRoles role);

    /**
     * Add a user (important, that user id is set!!) to {@link DefaultRoles} of
     * type {@link RoleTypes#TEAM}. Returns the updated and handled relation
     * role user object or null if role isn't found. If user already has role,
     * no new relation is created and the existing relation is returned.
     */
    RelationUserRoleTeam addRoleTeamToUser(User user, Team team, DefaultRoles role);

    /**
     * Add a user (important, that user id is set!!) to {@link DefaultRoles} of
     * type {@link RoleTypes#USER}. Returns the updated and handled relation
     * role user object or null if role isn't found. If user already has role,
     * no new relation is created and the existing relation is returned.
     */
    RelationUserRoleUser addRoleUserToUser(User user, User elementUser, DefaultRoles role);

    /**
     * Add a user (important, that user id is set!!) to an {@link RoleTypes#APP}
     * role by id. Returns the updated and handled relation role user object or
     * null if role isn't found. If user already has role, no new relation is
     * created and the existing relation is returned.
     */
    RelationUserRoleApp addRoleAppToUser(User user, Long id);

    /**
     * Add a user (important, that user id is set!!) to an
     * {@link RoleTypes#METAPROJECT} role by id. Returns the updated and handled
     * relation role user object or null if role isn't found. If user already
     * has role, no new relation is created and the existing relation is
     * returned.
     */
    RelationUserRoleMetaproject addRoleMetaprojectToUser(User user, Metaproject metaproject, Long id);

    /**
     * Add a user (important, that user id is set!!) to an
     * {@link RoleTypes#PROJECT} role by id. Returns the updated and handled
     * relation role user object or null if role isn't found. If user already
     * has role, no new relation is created and the existing relation is
     * returned.
     */
    RelationUserRoleProject addRoleProjectToUser(User user, Project project, Long id);

    /**
     * Add a user (important, that user id is set!!) to an
     * {@link RoleTypes#TEAM} role by id. Returns the updated and handled
     * relation role user object or null if role isn't found. If user already
     * has role, no new relation is created and the existing relation is
     * returned.
     */
    RelationUserRoleTeam addRoleTeamToUser(User user, Team team, Long id);

    /**
     * Add a user (important, that user id is set!!) to an
     * {@link RoleTypes#USER} role by id. Returns the updated and handled
     * relation role user object or null if role isn't found. If user already
     * has role, no new relation is created and the existing relation is
     * returned.
     */
    RelationUserRoleUser addRoleUserToUser(User user, User elementUser, Long id);

    /**
     * Find a {@link RelationUserRoleApp} by its fields. Returns null if no
     * entity is found.
     */
    RelationUserRoleApp findRelationUserRoleApp(User user, RoleApp role);

    /**
     * Find a {@link RelationUserRoleMetaproject} by its fields. Returns null if
     * no entity is found.
     */
    RelationUserRoleMetaproject findRelationUserRoleMetaproject(User user, RoleMetaproject role,
                                                                       Metaproject metaproject);
    /**
     * Find a {@link RelationUserRoleProject} by its fields. Returns null if no
     * entity is found.
     */
    RelationUserRoleAbstract findRelationUserRole(User user, RoleAbstract role);

    /**
     * Find a {@link RelationUserRoleProject} by its fields. Returns null if no
     * entity is found.
     */
    RelationUserRoleProject findRelationUserRoleProject(User user, RoleProject role, Project project);

    /**
     * Find a {@link RelationUserRoleTeam} by its fields. Returns null if no
     * entity is found.
     */
    RelationUserRoleTeam findRelationUserRoleTeam(User user, RoleTeam role, Team team);

    /**
     * Find a {@link RelationUserRoleUser} by its fields. Returns null if no
     * entity is found.
     */
    RelationUserRoleUser findRelationUserRoleUser(User user, RoleUser role, User elementUser);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link RoleTypes#APP} role by id. Returns null if role isn't found.
     */
    RelationUserRoleApp removeRoleAppFromUser(User user, Long id);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link RoleTypes#METAPROJECT} role by id. Returns null if role isn't
     * found.
     */
    RelationUserRoleMetaproject removeRoleMetaprojectFromUser(User user, Metaproject metaproject, Long id);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link RoleTypes#PROJECT} role by id. Returns null if role isn't found.
     */
    RelationUserRoleProject removeRoleProjectFromUser(User user, Project project, Long id);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link RoleTypes#TEAM} role by id. Returns null if role isn't found.
     */
    RelationUserRoleTeam removeRoleTeamFromUser(User user, Team team, Long id);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link RoleTypes#USER} role by id. Returns null if role isn't found.
     */
    RelationUserRoleUser removeRoleUserFromUser(User user, User elementUser, Long id);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link DefaultRoles} of type {@link RoleTypes#APP} role by id. Returns
     * null if role isn't found.
     */
    RelationUserRoleApp removeRoleAppFromUser(User user, DefaultRoles role);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link DefaultRoles} of type {@link RoleTypes#METAPROJECT} role by id.
     * Returns null if role isn't found.
     */
    RelationUserRoleMetaproject removeRoleMetaprojectFromUser(User user, Metaproject metaproject,
                                                                     DefaultRoles role);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link DefaultRoles} of type {@link RoleTypes#PROJECT} role by id.
     * Returns null if role isn't found.
     */
    RelationUserRoleProject removeRoleProjectFromUser(User user, Project project, DefaultRoles role);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link DefaultRoles} of type {@link RoleTypes#TEAM} role by id. Returns
     * null if role isn't found.
     */
    RelationUserRoleTeam removeRoleTeamFromUser(User user, Team team, DefaultRoles role);

    /**
     * Remove a user (important, that user id is set!!) from a
     * {@link DefaultRoles} of type {@link RoleTypes#USER} role by id. Returns
     * null if role isn't found.
     */
    RelationUserRoleUser removeRoleUserFromUser(User user, User elementUser, DefaultRoles role);

    /**
     * Remove all user roles of type {@link RoleTypes#METAPROJECT} from a metaproject by id.
     * This method could be called on metaproject delete.
     */
    Collection<RelationUserRoleMetaproject> removeRolesMetaproject(Metaproject metaproject);

    /**
     * Remove all user roles of type {@link RoleTypes#PROJECT} from a project by
     * id. This method could be called on project delete.
     */
    Collection<RelationUserRoleProject> removesRoleProject(Project project);

    /**
     * Remove all user roles of type {@link RoleTypes#TEAM} from a team by id.
     * This method could be called on team delete.
     */
    Collection<RelationUserRoleTeam> removeRolesTeam(Team team);

    /**
     * List all roles of a specific type.
     */
    <T extends RoleAbstract> Collection<T> findAll(Class<T> type);

    Collection<RoleAbstract> findAll();

    Collection<User> findMetaprojectProjectErsteller(long metaid);

    void removeMetaprojectProjectErsteller(long metaid, long userid);
}