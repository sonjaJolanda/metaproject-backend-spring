package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.RelationUserRoleAbstractDTO;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.*;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.RoleServiceOld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Endpoint for role management.
 *
 * @author SiKelle
 * @version 2.0
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/role")
public class RoleEndpoint {

    // This limit is used, to aoid to large request results,
    // when loading user role relations.
    private static final int MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE = 500;

    private static final Logger log = LoggerFactory.getLogger(RoleEndpoint.class);

    private final RoleDAO roleDao;

    private final UserDAO userDao;

    private final MetaprojectDAO metaprojectDao;

    private final ProjectDAO projectDao;

    private final TeamDAO teamDao;

    private final RightService rightService;

    private final RoleServiceOld roleServiceOld;

    public RoleEndpoint(RoleDAO roleDao, UserDAO userDao, MetaprojectDAO metaprojectDao, ProjectDAO projectDao, TeamDAO teamDao, RightService rightService, RoleServiceOld roleServiceOld) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.metaprojectDao = metaprojectDao;
        this.projectDao = projectDao;
        this.teamDao = teamDao;
        this.rightService = rightService;
        this.roleServiceOld = roleServiceOld;
    }

    /**
     * Get all existing roles from database. This collection includes all roles
     * of every type.
     */
    @GetMapping(value = "")
    public ResponseEntity getAllRoles(@RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(roleDao.findAll());
    }

    /**
     * Get a role by type and id from database.
     */
    @GetMapping(value = "/{type}/{id}")
    public ResponseEntity getAppRoleById(@RequestHeader String token, @PathVariable RoleTypes type, @PathVariable Long id) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST)
                .validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        switch (type) {
            case APP:
                return ResponseEntity.ok(roleDao.findById(id, RoleApp.class));
            case METAPROJECT:
                return ResponseEntity.ok(roleDao.findById(id, RoleMetaproject.class));
            case PROJECT:
                return ResponseEntity.ok(roleDao.findById(id, RoleProject.class));
            case TEAM:
                return ResponseEntity.ok(roleDao.findById(id, RoleTeam.class));
            case USER:
                return ResponseEntity.ok(roleDao.findById(id, RoleUser.class));
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all roles from a or assigned to a user by id from database.
     */
    @GetMapping(value = "/user/{user}")
    public ResponseEntity getAllRoles(@PathVariable("user") Long userId, @RequestHeader String token) {
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_USER_LIST)
                .validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // find all relations between user roles
        Collection<RelationUserRoleAbstract> rels = roleDao.findByUser(user, RelationUserRoleAbstract.class);
        Collection<RoleAbstract> roles = new HashSet<>();

        for (RelationUserRoleAbstract rel : rels) {
            roles.add(rel.getRole());
        }
        return ResponseEntity.ok(roles);
    }

    /**
     * Update a role of type {@link RoleTypes#APP} by id.
     */
    @PutMapping(value = "/APP/{roleId}")
    public ResponseEntity addRole(@PathVariable Long roleId, @RequestBody RoleApp transientRole, @RequestHeader String token) {
        return updateRole(RoleApp.class, transientRole, roleId, token);
    }

    /**
     * Update a role of type {@link RoleTypes#METAPROJECT} by id.
     */
    @PutMapping(value = "/METAPROJECT/{roleId}")
    public ResponseEntity addRole(@PathVariable Long roleId, @RequestBody RoleMetaproject transientRole, @RequestHeader String token) {
        return updateRole(RoleMetaproject.class, transientRole, roleId, token);
    }

    /**
     * Update a role of type {@link RoleTypes#PROJECT} by id.
     */
    @PutMapping(value = "/PROJECT/{roleId}")
    public ResponseEntity addRole(@PathVariable Long roleId, @RequestBody RoleProject transientRole, @RequestHeader String token) {
        return updateRole(RoleProject.class, transientRole, roleId, token);
    }

    /**
     * Update a role of type {@link RoleTypes#TEAM} by id.
     */
    @PutMapping(value = "/TEAM/{roleId}")
    public ResponseEntity addRole(@PathVariable Long roleId, @RequestBody RoleTeam transientRole, @RequestHeader String token) {
        return updateRole(RoleTeam.class, transientRole, roleId, token);
    }

    /**
     * Update a role of type {@link RoleTypes#USER} by id.
     */
    @PutMapping(value = "/USER/{roleId}")
    public ResponseEntity addRole(@PathVariable Long roleId, @RequestBody RoleUser transientRole, @RequestHeader String token) {
        return updateRole(RoleUser.class, transientRole, roleId, token);
    }

    /**
     * Delete a role by id. A default role could not be deleted.
     */
    @DeleteMapping(value = "/{roleId}")
    public ResponseEntity deleteRole(@PathVariable Long roleId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_DELETE).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (roleServiceOld.getRelatedUserToRoleByCount(roleId) > 0) {
            log.info("user_connected_to_role");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("user_connected_to_role");
        }

        RoleAbstract removedRole = roleDao.remove(roleId, RoleAbstract.class);
        if (removedRole == null) {
            log.error("A default role could not be deleted.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(removedRole);
    }

    /**
     * Create a new role of type {@link RoleTypes#APP}.
     */
    @PostMapping(value = "/APP")
    public ResponseEntity addRole(@RequestBody RoleApp role, @RequestHeader String token) {
        return createRole(RoleApp.class, role, token);
    }

    /**
     * Create a new role of type {@link RoleTypes#METAPROJECT}.
     */
    @PostMapping(value = "/METAPROJECT")
    public ResponseEntity addRole(@RequestBody RoleMetaproject role, @RequestHeader String token) {
        return createRole(RoleMetaproject.class, role, token);
    }

    /**
     * Create a new role of type {@link RoleTypes#PROJECT}.
     */
    @PostMapping(value = "/PROJECT")
    public ResponseEntity addRole(@RequestBody RoleProject role, @RequestHeader String token) {
        return createRole(RoleProject.class, role, token);
    }

    /**
     * Create a new role of type {@link RoleTypes#TEAM}.
     */
    @PostMapping(value = "/TEAM")
    public ResponseEntity addRole(@RequestBody RoleTeam role, @RequestHeader String token) {
        return createRole(RoleTeam.class, role, token);
    }

    /**
     * Create a new role of type {@link RoleTypes#USER}.
     */
    @PostMapping(value = "/USER")
    public ResponseEntity addRole(@RequestBody RoleUser role, @RequestHeader String token) {
        return createRole(RoleUser.class, role, token);
    }

    /**
     * Assign a role of type {@link RoleTypes#APP} to a {@link User}.
     */
    @PostMapping(value = "/APP/{user}/{role}")
    public ResponseEntity addUserRoleApp(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                         @RequestHeader(value = "token") String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleApp addRoleAppToUser = roleDao.addRoleAppToUser(user, roleId);
        if (addRoleAppToUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(addRoleAppToUser);
    }

    /**
     * Assign a role of type {@link RoleTypes#METAPROJECT} to a {@link User} and
     * a {@link Metaproject}.
     */
    @PostMapping(value = "/METAPROJECT/{user}/{role}/{elementId}")
    public ResponseEntity addUserRoleMetaproject(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                                 @PathVariable Long elementId, @RequestHeader String token) {
        /*boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return Response.status(Status.UNAUTHORIZED).build();
        }*/

        User user = userDao.findById(userId);
        Metaproject metaproject = metaprojectDao.findById(elementId);
        if (user == null || metaproject == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        RelationUserRoleMetaproject addRoleMetaprojectToUser = roleDao.addRoleMetaprojectToUser(user, metaproject,
                roleId);
        if (addRoleMetaprojectToUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(addRoleMetaprojectToUser);
    }

    /**
     * Assign a role of type {@link RoleTypes#PROJECT} to a {@link User} and a
     * {@link Project}.
     */
    @PostMapping(value = "/PROJECT/{user}/{role}/{elementId}")
    public ResponseEntity addUserRoleProject(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                             @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Project project = projectDao.findById(elementId);
        if (user == null || project == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleProject addRoleProjectToUser = roleDao.addRoleProjectToUser(user, project, roleId);
        if (addRoleProjectToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(addRoleProjectToUser);
    }

    /**
     * Assign a role of type {@link RoleTypes#TEAM} to a {@link User} and a
     * {@link Team}.
     */
    @PostMapping(value = "/TEAM/{user}/{role}/{elementId}")
    public ResponseEntity addUserRoleTeam(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                          @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Team team = teamDao.findById(elementId);
        if (user == null || team == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleTeam addRoleTeamToUser = roleDao.addRoleTeamToUser(user, team, roleId);
        if (addRoleTeamToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(addRoleTeamToUser);
    }

    /**
     * Assign a role of type {@link RoleTypes#USER} to a {@link User} and a
     * {@link User}.
     */
    @PostMapping(value = "/USER/{user}/{role}/{elementId}")
    public ResponseEntity addUserRoleUser(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                          @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        User elementUser = userDao.findById(elementId);
        if (user == null || elementUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleUser addRoleUserToUser = roleDao.addRoleUserToUser(user, elementUser, roleId);
        if (addRoleUserToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(addRoleUserToUser);
    }

    /**
     * Remove a role of type {@link RoleTypes#APP} by id from a {@link User} by id.
     */
    @DeleteMapping(value = "/APP/{user}/{role}")
    public ResponseEntity removeUserRoleApp(@PathVariable("user") Long userId, @PathVariable("role") Long roleId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userDao.findById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleApp removedRoleAppToUser = roleDao.removeRoleAppFromUser(user, roleId);
        if (removedRoleAppToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(removedRoleAppToUser);
    }

    /**
     * Remove a role of type {@link RoleTypes#METAPROJECT} by id from a
     * {@link User} by id.
     */
    @DeleteMapping(value = "/METAPROJECT/{user}/{role}/{elementId}")
    public ResponseEntity removeUserRoleMetaproject(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                                    @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Metaproject metaproject = metaprojectDao.findById(elementId);
        if (user == null || metaproject == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleMetaproject removedRoleMetaprojectToUser = roleDao.removeRoleMetaprojectFromUser(user, metaproject, roleId);
        if (removedRoleMetaprojectToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(removedRoleMetaprojectToUser);
    }

    /**
     * Remove a role of type {@link RoleTypes#PROJECT} by id from a {@link User} by id.
     */
    @DeleteMapping(value = "/PROJECT/{user}/{role}/{elementId}")
    public ResponseEntity removeUserRoleProject(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                                @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Project project = projectDao.findById(elementId);
        if (user == null || project == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleProject removedRoleProjectToUser = roleDao.removeRoleProjectFromUser(user, project, roleId);
        if (removedRoleProjectToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(removedRoleProjectToUser);
    }

    /**
     * Remove a role of type {@link RoleTypes#TEAM} by id from a {@link User} by id.
     */
    @DeleteMapping(value = "/TEAM/{user}/{role}/{elementId}")
    public ResponseEntity removeUserRoleTeam(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                             @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Team team = teamDao.findById(elementId);
        if (user == null || team == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleTeam removedRoleTeamToUser = roleDao.removeRoleTeamFromUser(user, team, roleId);
        if (removedRoleTeamToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(removedRoleTeamToUser);
    }

    /**
     * Remove a role of type {@link RoleTypes#USER} by id from a {@link User} by id.
     */
    @DeleteMapping(value = "/USER/{user}/{role}/{elementId}")
    public ResponseEntity removeUserRoleUser(@PathVariable("user") Long userId, @PathVariable("role") Long roleId,
                                             @PathVariable Long elementId, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_ASSIGN).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        User elementUser = userDao.findById(elementId);
        if (user == null || elementUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        RelationUserRoleUser removedRoleUserToUser = roleDao.removeRoleUserFromUser(user, elementUser, roleId);
        if (removedRoleUserToUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(removedRoleUserToUser);
    }

    /**
     * List all user with a role of type {@link RoleTypes#APP} by id.
     */
    @GetMapping(value = "/APP/{roleId}/user")
    public ResponseEntity getUserWithRoleApp(@PathVariable Long roleId) {
        Long count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleApp.class);
        if (count >= MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE) {
            // return status, caused by to large result
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }

        Collection<RelationUserRoleApp> rels = roleDao.findUsersByRoleId(roleId, RelationUserRoleApp.class);
        if (rels == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Collection<RelationUserRoleAbstractDTO> dtoList = new LinkedList<>();
        for (RelationUserRoleAbstract rel : rels)
            dtoList.add(new RelationUserRoleAbstractDTO(rel));

        return ResponseEntity.ok(dtoList);
    }

    /**
     * List all user with a role of type {@link RoleTypes#METAPROJECT} by id.
     */
    @GetMapping(value = "/METAPROJECT/{roleId}/user")
    public ResponseEntity getUserWithRoleMetaproject(@PathVariable Long roleId) {
        Long count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleMetaproject.class);
        if (count >= MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE) {
            // return status, caused by to large result
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }

        Collection<RelationUserRoleMetaproject> rels = roleDao.findUsersByRoleId(roleId,
                RelationUserRoleMetaproject.class);
        if (rels == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Collection<RelationUserRoleAbstractDTO> dtoList = new LinkedList<>();
        for (RelationUserRoleAbstract rel : rels)
            dtoList.add(new RelationUserRoleAbstractDTO(rel));

        return ResponseEntity.ok(dtoList);
    }

    /**
     * List all user with a role of type {@link RoleTypes#PROJECT} by id.
     */
    @GetMapping(value = "/PROJECT/{roleId}/user")
    public ResponseEntity getUserWithRoleProject(@PathVariable Long roleId) {
        Long count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleProject.class);
        if (count >= MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE) {
            // return status, caused by to large result
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
        Collection<RelationUserRoleProject> rels = roleDao.findUsersByRoleId(roleId, RelationUserRoleProject.class);
        if (rels == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Collection<RelationUserRoleAbstractDTO> dtoList = new LinkedList<>();
        for (RelationUserRoleAbstract rel : rels)
            dtoList.add(new RelationUserRoleAbstractDTO(rel));

        return ResponseEntity.ok(dtoList);
    }

    /**
     * Get role project leader
     */
    @GetMapping(value = "/projectleader")
    public ResponseEntity getRoleProjectLeader() {
        log.info("Request <-- GET /role/projectleader");
        RoleProject role = roleDao.findDefaultByKey(DefaultRoles.METAPROJECT_PROJECT_LEADER, RoleProject.class);
        return ResponseEntity.ok(role);
    }

    /**
     * List all user with a role of type {@link RoleTypes#TEAM} by id.
     */
    @GetMapping(value = "/TEAM/{roleId}/user")
    public ResponseEntity getUserWithRoleTeam(@PathVariable Long roleId) {
        Long count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleTeam.class);
        if (count >= MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE) {
            // return status, caused by to large result
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
        Collection<RelationUserRoleTeam> rels = roleDao.findUsersByRoleId(roleId, RelationUserRoleTeam.class);
        if (rels == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Collection<RelationUserRoleAbstractDTO> dtoList = new LinkedList<>();
        for (RelationUserRoleAbstract rel : rels)
            dtoList.add(new RelationUserRoleAbstractDTO(rel));

        return ResponseEntity.ok(dtoList);
    }

    /**
     * List all user with a role of type {@link RoleTypes#USER} by id.
     */
    @GetMapping(value = "/USER/{roleId}/user")
    public ResponseEntity getUserWithRoleUser(@PathVariable Long roleId) {
        Long count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleUser.class);
        if (count >= MAX_RELATION_USER_ROLE_LOAD_FROM_DATABASE) {
            // return status, caused by to large result
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
        Collection<RelationUserRoleUser> rels = roleDao.findUsersByRoleId(roleId, RelationUserRoleUser.class);
        if (rels == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Collection<RelationUserRoleAbstractDTO> dtoList = new LinkedList<>();
        for (RelationUserRoleAbstract rel : rels)
            dtoList.add(new RelationUserRoleAbstractDTO(rel));

        return ResponseEntity.ok(dtoList);
    }

    /**
     * This generic method is used to perform an update action. It can be used
     * to create end points for every subclass of {@link RoleAbstract}. The type
     * describes the subclass on which this action is performed. It also
     * performs right checks of given user token on this action.
     */
    private <T extends RoleAbstract> ResponseEntity updateRole(Class<T> type, T transientRole, Long roleId, String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_EDIT)
                .validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        T role = roleDao.update(transientRole, roleId, type);
        if (role == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(role);
    }

    /**
     * This generic method is used to perform a create action. It can be used to
     * create end points for every subclass of {@link RoleAbstract}. The type
     * describes the subclass on which this action is performed. It also
     * performs right checks of given user token on this action.
     */
    private <T extends RoleAbstract> ResponseEntity createRole(Class<T> type, T role, String token) {

        /*boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_CREATE).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return Response.status(Status.UNAUTHORIZED).build();
        }*/

        return ResponseEntity.status(HttpStatus.CREATED).body(roleDao.save(role, type));
    }

}
