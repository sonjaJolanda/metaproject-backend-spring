package main.java.org.htwg.konstanz.metaproject.rest;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.MetaprojectUserDTO;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationCreatorService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sethuemm on 23.12.2015.
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class RelationMetaprojectUserEndpoint {

    private final static Logger log = LoggerFactory.getLogger(ProjectEndpoint.class);

    private final RoleDAO roleDao;

    private final RightService rightService;

    private final CommunicationCreatorService communicationCreatorService;

    private final TokenService tokenService;

    private final TeamDAO teamDAO;

    private final RelationMetaprojectUserDAO relationMetaprojectUserDao;

    private final RelationTeamUserDAOImpl relationTeamUserDAO;

    private final UserDAO userDAO;

    private final MetaprojectDAO metaprojectDAO;

    private final ProjectDAO projectDAO;

    public RelationMetaprojectUserEndpoint(RoleDAO roleDao, RightService rightService, CommunicationCreatorService communicationCreatorService, TokenService tokenService, TeamDAO teamDAO, RelationMetaprojectUserDAO relationMetaprojectUserDao, RelationTeamUserDAOImpl relationTeamUserDAO, UserDAO userDAO, MetaprojectDAO metaprojectDAO, ProjectDAO projectDAO) {
        this.roleDao = roleDao;
        this.rightService = rightService;
        this.communicationCreatorService = communicationCreatorService;
        this.tokenService = tokenService;
        this.teamDAO = teamDAO;
        this.relationMetaprojectUserDao = relationMetaprojectUserDao;
        this.relationTeamUserDAO = relationTeamUserDAO;
        this.userDAO = userDAO;
        this.metaprojectDAO = metaprojectDAO;
        this.projectDAO = projectDAO;
    }


    @PostMapping(value = "metaproject/{metaid}/MetaprojectUser/{userId}")
    public ResponseEntity createRelationMetaprojectUser(@PathVariable Long metaid, @PathVariable Long userId, @RequestHeader String token) {

       /* boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_INVITE_ALWAYS, metaid).validate();
        if (!hasRights) {
            log.info("createRelationMetaprojectUser 1: User has no permissions to do that operation.");
            return Response.status(Status.UNAUTHORIZED).build();
        }*/

        User user = userDAO.findById(userId);
        Metaproject metaproject = metaprojectDAO.findById(metaid);

        if (metaproject == null || user == null)
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();

        RelationMetaprojectUser existingRelation = relationMetaprojectUserDao.findByUserAndMetaproject(user, metaproject);
        if (existingRelation != null) {
            log.info("createRelationMetaprojectUser 2: User is already registered in the metaproject");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        RelationMetaprojectUser rmu = new RelationMetaprojectUser();
        rmu.setMetaprojectId(metaproject);
        rmu.setUserId(user);
        relationMetaprojectUserDao.save(rmu);

        User actionUser = tokenService.getUserByToken(token);

        // create and send a new communication object
        communicationCreatorService.sendCommMetaprojectAddMember(actionUser, metaproject, user);

        // Set initial role for unregistered user to metaproject
        roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER);

        return ResponseEntity.status(HttpStatus.CREATED).body(rmu);
    }

    /**
     * Save a RelationMetaprojectUser in Database
     */
    @PostMapping(value = "metaproject/{metaid}/MetaprojectUser")
    public ResponseEntity createRelationMetaUser(@PathVariable Long metaid, @RequestHeader String token) throws ParseException {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_REGISTER).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("create Relation Meta");
        Metaproject metaproject = metaprojectDAO.findById(metaid);

        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date regEnd = parserSDF.parse(metaproject.getStudentRegEnd());
        Date regStart = parserSDF.parse(metaproject.getStudentRegStart());
        Date now = new Date();

        if (!(regEnd.after(now) && regStart.before(now)))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        User user = tokenService.getUserByToken(token);
        RelationMetaprojectUser existingRelation = relationMetaprojectUserDao.findByUserAndMetaproject(user, metaproject);
        if (existingRelation != null) {
            log.info("createRelationMetaprojectUser 2: User is already registered in the metaproject");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        RelationMetaprojectUser rmu = new RelationMetaprojectUser();
        rmu.setMetaprojectId(metaproject);
        rmu.setUserId(user);
        relationMetaprojectUserDao.save(rmu);

        // Set initial role for unregistered user to metaproject
        roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        roleDao.addRoleMetaprojectToUser(user, metaproject, DefaultRoles.METAPROJECT_MEMBER);

        // return ResponseEntity.created(UriBuilder.fromResource(RelationMetaprojectUserEndpoint.class).path(String.valueOf(user.getUserId())).build()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(rmu);
    }

    @DeleteMapping("metaproject/{metaId}/MetaprojectUser")
    public ResponseEntity deleteRelationMetaprojectUserById(@PathVariable Long metaId, @RequestHeader String token) {
        log.info("deleteUserFromMetaproject: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_UNREGISTER, metaId).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Metaproject mp = metaprojectDAO.findById(metaId);
        User user = tokenService.getUserByToken(token);

        // Delete role for registered user in metaproject
        roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_MEMBER);

        RelationMetaprojectUser relation = relationMetaprojectUserDao.findByUserAndMetaproject(user, mp);
        if (relation == null)
            return ResponseEntity.notFound().build();

        log.info("delete " + RelationMetaprojectUser.class.getName() + " with id" + relation.getRelationMetaprojectUserId());
        relationMetaprojectUserDao.delete(relation);

        // User aus Team des Metaprojekts entfernen
        Collection<Team> teams = teamDAO.findByMetaprojectId(mp.getMetaprojectId());
        for (Team team : teams) {
            RelationTeamUser relationTeamUser = relationTeamUserDAO.findByUserAndTeam(user, team);
            if (relationTeamUser != null) {
                relationTeamUserDAO.deleteById(relationTeamUser.getRelationTeamUserId());

                User actionUser = tokenService.getUserByToken(token);
                communicationCreatorService.sendCommTeamRemoveMember(actionUser, user, teamDAO.findById(metaId)); // create and send a new communication object
            }
        }

        // return ResponseEntity.created(UriBuilder.fromResource(RelationMetaprojectUserEndpoint.class).path(String.valueOf(user.getUserId())).build()).build();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "metaproject/{metaid}/MetaprojectUser/{userId}")
    public ResponseEntity findById(@PathVariable Long metaid, @PathVariable Long userId, @RequestHeader String token) {
        log.info("getARelationMetaprojectUser: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDAO.findById(userId);
        Metaproject metaproject = metaprojectDAO.findById(metaid);
        RelationMetaprojectUser relation = relationMetaprojectUserDao.findByUserAndMetaproject(user, metaproject);
        return ResponseEntity.ok(relation);
    }

    @GetMapping(value = "metaproject/{metaid}/MetaprojectUser")
    public ResponseEntity getAllRelationMetaprojectUser(@PathVariable Long metaid, @RequestHeader String token) {
        log.info("getAllRelationMetaprojectUser: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Metaproject metaproject = metaprojectDAO.findById(metaid);
        if (metaproject == null)
            return ResponseEntity.notFound().build();

        Collection<RelationMetaprojectUser> relations = relationMetaprojectUserDao.findByMetaproject(metaproject);
        return ResponseEntity.ok(relations);
    }

    @GetMapping(value = "metaproject/{metaid}/user")
    public ResponseEntity<Object> getAllUserOfMetaproject(@PathVariable Long metaid, @RequestHeader String token) {
        log.info("Request <-- GET /metaproject/" + metaid + "/user");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MetaprojectUserDTO> metaprojectUserDTOs = Lists.newArrayList();

        Metaproject mp = metaprojectDAO.findById(metaid);
        Collection<User> allUsers = userDAO.getAll();

        Map<Long, Project> leadersOfProjects = new HashMap<>();
        for (Project project : projectDAO.findByMetaproject(metaid)) {
            leadersOfProjects.put(project.getProjectLeader().getUserId(), project);
        }

        Set<Long> creators = mp.getProjectCreatorUsers().stream().map(User::getUserId).collect(Collectors.toSet()); // addCreatorUsers
        mp.getProjectCreatorGroups().stream().map(g -> g.getUsers().stream() // addCreatorUserGroups
                .map(User::getUserId).collect(Collectors.toSet())).forEach(creators::addAll);

        // check if a user is teammember, creator or leader and if so create a dto and add it to the list
        for (User user : allUsers) {
            RelationMetaprojectUser mpUser = relationMetaprojectUserDao.findByUserAndMetaproject(user, mp);
            boolean userIsLeader = leadersOfProjects.containsKey(user.getUserId());
            boolean userIsCreator = creators.contains(user.getUserId());

            if (mpUser == null && !userIsLeader && !userIsCreator)
                continue;

            MetaprojectUserDTO dto = new MetaprojectUserDTO();
            dto.metaprojectId = mp.getMetaprojectInfoDto();
            dto.userId = user;

            if (userIsCreator)
                dto.roles.add("Projektersteller");
            if (userIsLeader) {
                dto.roles.add("Projektleiter");
                if (leadersOfProjects.get(user.getUserId()) != null) {
                    dto.projectId = leadersOfProjects.get(user.getUserId()).getProjectId();
                    dto.projectTitle = leadersOfProjects.get(user.getUserId()).getProjectTitle();
                }
            }

            if (mpUser != null) {
                dto.relationMetaprojectUserId = mpUser.getRelationMetaprojectUserId();
                for (RelationTeamUser teamUser : relationTeamUserDAO.findByUserAndMeta(mpUser.getUserId(), mp)) {
                    if (mp.getRegisterType().equals("Team")) {
                        dto.roles.add("Mitglied in Team " + dto.teamName);
                        Team team = teamUser.getTeamId();
                        if (team.getProjectId() != null) {
                            dto.projectId = team.getProjectId().getProjectId();
                            dto.projectTitle = team.getProjectId().getProjectTitle();
                        }
                        if (teamUser.getTeamId() != null) {
                            dto.teamName = team.getTeamName();
                            dto.teamId = team.getTeamId();
                        }
                    }
                }
            }
            metaprojectUserDTOs.add(dto);
        }
        return ResponseEntity.ok(metaprojectUserDTOs);
    }

    @GetMapping(value = "metaproject/{metaid}/MetaprojectProjectleader")
    public ResponseEntity MetaprojectProjectleader(@PathVariable Long metaid, @RequestHeader String token) {

        log.info("MetaprojectProjectleader: check Token: " + token);
        return ResponseEntity.ok(roleDao.findMetaprojectProjectErsteller(metaid));
    }

    @PutMapping(value = "metaproject/{metaid}/MetaprojectProjectleader")
    public ResponseEntity DeleteMetaprojectProjectleader(@PathVariable Long metaid, @RequestBody Integer userId, @RequestHeader String token) {

        log.info("DeleteMetaprojectProjectleader: check Token: " + token);
        roleDao.removeMetaprojectProjectErsteller(metaid, userId);
        return ResponseEntity.ok().build();
    }
}
