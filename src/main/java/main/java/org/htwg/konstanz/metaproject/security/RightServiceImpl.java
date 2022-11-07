package main.java.org.htwg.konstanz.metaproject.security;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.RelationMetaprojectUser;
import main.java.org.htwg.konstanz.metaproject.entities.TokenInfo;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.*;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationCreatorService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Right service implementation for the management service.
 *
 * @author SiKelle
 */
@Service
public class RightServiceImpl implements RightService {

    private static final Logger log = LoggerFactory.getLogger(RightServiceImpl.class);

    private final UserDAO userDao;

    private final TokenService tokenService;

    private final RoleDAO roleDao;

    private final MetaprojectDAO metaDao;

    private final PersistanceService persistanceService;

    private final RelationMetaprojectUserDAO relationMetaprojectUserDao;

    private final CommunicationCreatorService communicationCreatorService;

    public RightServiceImpl(UserDAO userDao, TokenService tokenService, RoleDAO roleDao, MetaprojectDAO metaDao, PersistanceService persistanceService, RelationMetaprojectUserDAO relationMetaprojectUserDao, CommunicationCreatorService communicationCreatorService) {
        this.userDao = userDao;
        this.tokenService = tokenService;
        this.roleDao = roleDao;
        this.metaDao = metaDao;
        this.persistanceService = persistanceService;
        this.relationMetaprojectUserDao = relationMetaprojectUserDao;
        this.communicationCreatorService = communicationCreatorService;
    }

    @Override
    public synchronized RightHandler newRightHandler(String token) {
        TokenInfo tokenInfo = tokenService.getTokenInfo(token);
        if (tokenInfo == null) {
            log.error("Could not validate token, an error object is returned {}", token);
            return new RightHandlerImpl();
        }
        return newRightHandler(tokenInfo.getUserId());
    }

    @Override
    public synchronized RightHandler newRightHandler(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            log.error("Could not find user by userId {}.", userId);
            return new RightHandlerImpl();
        }
        RightHandler rightHandler = new RightHandlerImpl(roleDao, user, persistanceService);
        log.debug("New instance for RightHandler in RightService");
        return rightHandler;
    }

    @Override
    public Collection<UserRight> getAllUserRights(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            log.error("User not found");
            return null;
        }
        Collection<RelationUserRoleAbstract> userRoles = roleDao.findByUser(user, RelationUserRoleAbstract.class);
        Collection<UserRight> result = new HashSet<>();
        for (RelationUserRoleAbstract userRole : userRoles) {
            for (Rights right : userRole.getRole().getRoleRights()) {
                UserRight newUserRight = new UserRight();
                newUserRight.setElementId(userRole.getConnectedId());
                newUserRight.setElementType(userRole.getRole().getRoleType().toString());
                newUserRight.setRight(right);
                result.add(newUserRight);
            }
        }
        return result;
    }


    public void createRelationMetaToAllUserAddRoleToAllUser(Metaproject metaproject, User actionUser) {
        Collection<User> allUsers = userDao.getAll();

        for (User user : allUsers) {
            createRelationMetaUserAddRoleToUser(metaproject, user, actionUser);
        }
    }

    public void createRelationMetaUserAddRoleToUser(Metaproject metaproject, User assignUser, User actionUser) {
        RelationMetaprojectUser existingRelation = relationMetaprojectUserDao.findByUserAndMetaproject(assignUser, metaproject);
        if (existingRelation != null) {
            log.info("createRelationMetaprojectUser: " + assignUser.getFullName() + " is already registered in the metaproject");
        } else {
            RelationMetaprojectUser rmu = new RelationMetaprojectUser();
            rmu.setMetaprojectId(metaproject);
            rmu.setUserId(assignUser);
            communicationCreatorService.sendCommMetaprojectAddMember(actionUser, metaproject, assignUser);
            relationMetaprojectUserDao.save(rmu);
        }

        RoleMetaproject metaprojet_member_teamless = roleDao.findById(16L, RoleMetaproject.class);
        RoleMetaproject metaprojet_member = roleDao.findById(18L, RoleMetaproject.class);

        if (roleDao.findRelationUserRoleMetaproject(assignUser, metaprojet_member_teamless, metaproject) != null) {
            log.info(assignUser.getFullName() + "already has Role metaprojet_member_teamless for mp: " + metaproject.getMetaprojectId());
        } else {
            roleDao.addRoleMetaprojectToUser(assignUser, metaproject, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        }

        if (roleDao.findRelationUserRoleMetaproject(assignUser, metaprojet_member, metaproject) != null) {
            log.info(assignUser.getFullName() + "already has Role metaprojet_member for mp: " + metaproject.getMetaprojectId());
        } else {
            roleDao.addRoleMetaprojectToUser(assignUser, metaproject, DefaultRoles.METAPROJECT_MEMBER);
        }
    }

    public void deleteRelationMetaToAllUserRemoveRoleFromAllNormalUser(Metaproject metaproject) {
        Collection<User> users = userDao.getAll();

        for (User user : users) {
            RoleMetaproject metaproject_project_creator = roleDao.findById(25L, RoleMetaproject.class);
            if (!(user.equals(metaproject.getMetaprojectLeader()) || roleDao.findRelationUserRoleMetaproject(user, metaproject_project_creator, metaproject) != null)) {

                relationMetaprojectUserDao.deleteByMetaprojectAndUser(metaproject, user);

                roleDao.removeRoleMetaprojectFromUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
                roleDao.removeRoleMetaprojectFromUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER);
            }
        }
    }

    public void createRelationNonPreRegMetaToUserAddRoleToNormalUser(User user) {
        Collection<Metaproject> allMetaprojects = metaDao.findAllNonPreRegistration();
        Iterator<Metaproject> iterator = allMetaprojects.iterator();

        RoleMetaproject metaprojet_member_teamless = roleDao.findById(16L, RoleMetaproject.class);
        RoleMetaproject metaprojet_member = roleDao.findById(18L, RoleMetaproject.class);

        while (iterator.hasNext()) {
            Metaproject metaproject = iterator.next();

            RelationMetaprojectUser rmu = new RelationMetaprojectUser();
            rmu.setMetaprojectId(metaproject);
            rmu.setUserId(user);
            relationMetaprojectUserDao.save(rmu);

            roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
            roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER);
            log.info(user.getFullName() + " added Relation and Roles to Metaproject " + metaproject.getMetaprojectTitle());
        }
    }
}
