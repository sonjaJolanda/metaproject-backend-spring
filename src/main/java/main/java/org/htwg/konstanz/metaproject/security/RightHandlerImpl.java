package main.java.org.htwg.konstanz.metaproject.security;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.PersistanceService;
import main.java.org.htwg.konstanz.metaproject.persistance.RoleDAO;
import main.java.org.htwg.konstanz.metaproject.rights.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Check for some rights with given roles.
 *
 * @author SiKelle
 */
public class RightHandlerImpl implements RightHandler {

    public static final Logger log = LoggerFactory.getLogger(RightHandlerImpl.class);

    /**
     * Result of check rights chain.
     */
    private boolean valid = false;

    /**
     * This flag indicates, that the access check failed and this handler have
     * to return false.
     */
    private final boolean invalidHandler;

    /**
     * The data access object for roles.
     */
    private final RoleDAO roleDao;

    /**
     * The user which should be checked.
     */
    private final User user;

    /**
     * Persistance service to access to hibernate cache a.o.
     */
    private final PersistanceService persistanceService;

    /**
     * This constructor creates an invalid handler object, which is used to
     * avoid {@link NullPointerException} and returns false in every case.
     */
    public RightHandlerImpl() {
        log.info("Create invalid right handler, to return false in every case.");
        this.invalidHandler = true;
        this.roleDao = null;
        this.user = null;
        this.persistanceService = null;
    }

    /**
     * This constructor should be used to instantiate this handler with all
     * required objects.
     */
    public RightHandlerImpl(RoleDAO roleDao, User user, PersistanceService persistanceService) {
        this.invalidHandler = false;
        this.roleDao = roleDao;
        this.user = user;
        this.persistanceService = persistanceService;
    }

    @Override
    public boolean validate() {
        log.debug("Result for RightHandler, current state: {}", this.valid);
        if (invalidHandler) {
            return false;
        }
        // clear cache of hibernate to avoid errors of following statements
        persistanceService.clearCache();
        return valid;
    }

    @Override
    public RightHandlerImpl checkForSuperUser() {
        log.debug("Check SuperUser, current state: {}", this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleApp> relUserRoleApp = roleDao.findByUser(user, RelationUserRoleApp.class);
        for (RelationUserRoleApp rel : relUserRoleApp) {
            if (rel.getRole().getRoleRights().contains(Rights.SUPER_USER)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

    @Override
    public RightHandlerImpl checkForAppRight(Rights right) {
        log.debug("Check AppRight {}, current state: {}", right, this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleApp> relUserRoleApp = roleDao.findByUser(user, RelationUserRoleApp.class);
        for (RelationUserRoleApp rel : relUserRoleApp) {
            if (rel.getRole().getRoleRights().contains(right)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

    @Override
    public RightHandlerImpl checkForMetaprojectRight(Rights right, Long metaprojectId) {
        log.debug("Check MetaprojectRight {}, current state: {}", right, this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleMetaproject> relUserRoleMetaproject = roleDao.findByUser(user, RelationUserRoleMetaproject.class);
        for (RelationUserRoleMetaproject rel : relUserRoleMetaproject) {
            if (rel.getRole().getRoleRights().contains(right) && rel.getConnectedId().equals(metaprojectId)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

    @Override
    public RightHandlerImpl checkForProjectRight(Rights right, Long projectId) {
        log.debug("Check ProjectRight {}, current state: {}", right, this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleProject> relUserRoleProject = roleDao.findByUser(user, RelationUserRoleProject.class);
        for (RelationUserRoleProject rel : relUserRoleProject) {
            if (rel.getRole().getRoleRights().contains(right) && rel.getConnectedId().equals(projectId)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

    @Override
    public RightHandlerImpl checkForTeamRight(Rights right, Long teamId) {
        log.debug("Check TeamRight {}, current state: {}", right, this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleTeam> relUserRoleTeam = roleDao.findByUser(user, RelationUserRoleTeam.class);
        for (RelationUserRoleTeam rel : relUserRoleTeam) {
            if (rel.getRole().getRoleRights().contains(right) && rel.getConnectedId().equals(teamId)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

    @Override
    public RightHandlerImpl checkForUserRight(Rights right, Long connectedUserId) {
        log.debug("Check UserRight {}, current state: {}", right, this.valid);
        if (this.valid) {
            log.debug("Already valid, skip check...");
            return this;
        }
        if (invalidHandler) {
            return this;
        }
        Collection<RelationUserRoleUser> relUserRoleUser = roleDao.findByUser(user, RelationUserRoleUser.class);
        for (RelationUserRoleUser rel : relUserRoleUser) {
            if (rel.getRole().getRoleRights().contains(right) && rel.getConnectedId().equals(connectedUserId)) {
                this.valid = true;
                return this;
            }
        }
        return this;
    }

}
