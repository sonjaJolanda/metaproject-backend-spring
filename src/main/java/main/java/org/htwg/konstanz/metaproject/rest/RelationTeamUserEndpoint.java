package main.java.org.htwg.konstanz.metaproject.rest;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.TeamsByUserInfoDTO;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import main.java.org.htwg.konstanz.metaproject.enums.UpdateStatus;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationCreatorService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sethuemm on 29.12.2015.
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class RelationTeamUserEndpoint {

    private static final String LOG_STATEMENT_NO_PERMISSION = "User has no permissions to do that operation.";

    private static final Logger log = LoggerFactory.getLogger(RelationMetaprojectUserEndpoint.class);

    private final RightService rightService;

    private final RoleDAO roleDao;

    private final TeamDAO teamDao;

    private final UserDAO userDao;

    private final MetaprojectDAO metaprojectDAO;

    private final RelationTeamUserDAO relTeamUserDao;

    private final RelationMetaprojectUserDAO relMetaUserDao;

    private final TokenService tokenService;

    private final CommunicationCreatorService communicationCreatorService;

    private final CommDAO commDao;

    private final CommunicationService communicationService;

    private final RelationTeamUserDAO relationTeamUserDAO;

    public RelationTeamUserEndpoint(RightService rightService, RoleDAO roleDao, TeamDAO teamDao, UserDAO userDao, MetaprojectDAO metaprojectDAO, RelationTeamUserDAO relTeamUserDao, RelationMetaprojectUserDAO relMetaUserDao, TokenService tokenService, CommunicationCreatorService communicationCreatorService, CommDAO commDao, CommunicationService communicationService, RelationTeamUserDAO relationTeamUserDAO) {
        this.rightService = rightService;
        this.roleDao = roleDao;
        this.teamDao = teamDao;
        this.userDao = userDao;
        this.metaprojectDAO = metaprojectDAO;
        this.relTeamUserDao = relTeamUserDao;
        this.relMetaUserDao = relMetaUserDao;
        this.tokenService = tokenService;
        this.communicationCreatorService = communicationCreatorService;
        this.commDao = commDao;
        this.communicationService = communicationService;
        this.relationTeamUserDAO = relationTeamUserDAO;
    }

    @PostMapping(value = "/team/{teamId}/user/advertised")
    public ResponseEntity<Object> createRelationAdvertised(@PathVariable Long teamId, @RequestHeader String token) {
        Team team = teamDao.findById(teamId);
        Long metaId = team.getMetaProjectId().getMetaprojectId();

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_REQUEST, metaId).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = tokenService.getUserByToken(token);
        log.info("CREATE TeamUserRelation Adversisement: tid: " + teamId + " | uid: " + user.getUserId());
        try {
            //check if team has the maximum count of members
            //check if user is already teammember of an other team
            //check if user has already a relation to the selected team
            //check if the status of the selected team ist not final
            if (reachedMaxSize(team) || isUserAlreadyInTeam(user, team) || isUserAlreadyTeammember(user, team.getMetaProjectId()) || team.getUpdateStatus() == UpdateStatus.FINAL)
                return ResponseEntity.status(HttpStatus.CONFLICT).build();

            RelationTeamUser relationTeamUser = new RelationTeamUser();
            relationTeamUser.setTeamId(team);
            relationTeamUser.setTeamMemberStatus(TeamMemberStatus.ADVERTISED);
            relationTeamUser.setUserId(user);
            relTeamUserDao.save(relationTeamUser);

            // create request communication object
            communicationCreatorService.sendCommTeamRequest(user, team);
        } catch (Exception e) {
            log.info("***** Fehler bei Erstellung der TeamUserRelation: " + e.getMessage() + " *****");
        }
        //return ResponseEntity.created(UriBuilderfromResource(RelationMetaprojectUserEndpoint.class).path(String.valueOf(user.getUserId())).build()).build();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/team/{teamId}/user/{userId}/teammember")
    public ResponseEntity createRelationTeammember(@PathVariable Long teamId, @PathVariable Long userId, @RequestHeader() String token) {

        Team team = teamDao.findById(teamId);
        //Team team = teamDao.findByProjectId((long) 999);
        Metaproject mp = team.getMetaProjectId();
        if (mp.getRegisterType().equals("Single")) {
            //Projekt herholen(über ProjektID), dann schauen was Min und MAx Anzahl Mitgliedern ist. Wenn MAx erreicht, dann return - response(Fehlermeldung) status zurückliefern
            //Gleiches im Frontend: Button ausgrauen oder Ähnliches
/*
			Wenn dieser Teil nicht auskommentiert: Hinzufügen funktioniert wenn team.projectid=281, aber  Herauslöschen nicht !hier muss projectId in Datenbank in Team Tabelle herausgenommen werden dann funktioniert es.
			Diese Funktionen benötigt man um folgende Fälle zu überprüfen
			//1. check if team has the maximum count of members
			//2. check if user is already teammember of an other team
			//3. check if user has already a relation to the selected team
			//4. check if the status of the selected team is final*/
            User user = tokenService.getUserByToken(token);
            if (reachedMaxAmount(team) || isUserAlreadyInTeam(user, team) || isUserAlreadyTeammember(user, team.getMetaProjectId()) || team.getUpdateStatus() == UpdateStatus.FINAL)
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        // Routine to check if token is valid & if user has the permissions
        //boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForMetaprojectRight(Rights.METAPROJECT_TEAM_INVITE_ALWAYS, mp.getMetaprojectId()).validate();

        //ToDo To add user to teams during team creation process
        /*
         boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForMetaprojectRight(Rights.METAPROJECT_TEAM_INVITE, mp.getMetaprojectId()).validate();

         if (!hasRights && mp.getRegisterType().equals("Team")) {
         // User has no rights
         log.info(LOG_STATEMENT_NO_PERMISSION);
         return Response.status(Status.UNAUTHORIZED).build();
         }
         */

        User user = userDao.findById(userId);
        log.info("CREATE TeamUserRelation: tid: " + teamId + " | uid: " + user.getUserId());
        try {
            //check if user is already in team or member of another team
            if (isUserAlreadyInTeam(user, team) || isUserAlreadyTeammember(user, mp)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            RelationMetaprojectUser relation = relMetaUserDao.findByUserAndMetaproject(user, mp);
            if (relation == null) {
                RelationMetaprojectUser rmu = new RelationMetaprojectUser();
                rmu.setMetaprojectId(mp);
                rmu.setUserId(user);
                relMetaUserDao.save(rmu);
            }

            roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_MEMBER);
            roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_TEAM_MEMBER);
            roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

            relTeamUserDao.deleteByUserAndMeta(user, mp);

            // withdraw all invitations for the user
            Collection<CommTeamInvite> inviteComms = communicationService.findTeamInvitesForUser(user,
                    mp.getMetaprojectId());
            for (CommTeamInvite comm : inviteComms) {
                comm.setAnswer(CommAnswer.NEGATIVE);
                comm.setStatus(CommStatus.FINISHED);
                communicationCreatorService.sendCommTeamInvitationReject(user, comm.getSendingUser(), comm.getTeam());
            }

            // reject all comm requests the user has sent
            Collection<CommTeamRequest> requestComms = communicationService.findTeamRequestsForUser(user,
                    mp.getMetaprojectId());
            for (CommTeamRequest comm : requestComms) {
                commDao.deleteById(comm.getId());
                communicationCreatorService.sendCommTeamApplicationWithdraw(comm.getSendingUser(), comm.getTeam());
            }

            RelationTeamUser relationTeamUser = new RelationTeamUser();
            relationTeamUser.setTeamId(team);
            relationTeamUser.setTeamMemberStatus(TeamMemberStatus.TEAMMEMBER);
            relationTeamUser.setUserId(user);
            relTeamUserDao.save(relationTeamUser);

            User actionUser = tokenService.getUserByToken(token);
            if (mp.getRegisterType().equals("Team")) {
                communicationCreatorService.sendCommMetaprojectleaderAddTeamMember(actionUser, user, team);
            }
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            log.info("***** Fehler bei Erstellung der TeamUserRelation: " + e.getMessage() + " *****");
        }
        // return ResponseEntity.created(UriBuilder.fromResource(RelationMetaprojectUserEndpoint.class).path(String.valueOf(user.getUserId())).build()).build();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Save a RelationTeamUserEndpoint in Database with the Status invited.
     */
    @PostMapping(value = "/team/{teamId}/user/{userId}/invited")
    public ResponseEntity createRelationInvited(@PathVariable Long teamId, @PathVariable Long userId, @RequestHeader String token) {
        Team team = teamDao.findById(teamId);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_INVITE_ALWAYS, team.getMetaProjectId().getMetaprojectId())
                .checkForTeamRight(Rights.METAPROJECT_TEAM_INVITE, teamId).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("CREATE TeamUserRelation Invited: tid: " + teamId + " | uid: " + userId);
        try {
            User user = userDao.findById(userId);

            //1. check if user is member of the metaproject
            //2. check if team has the maximum count of members
            //3. check if user is already teammember of an other team
            //4. check if user has already a relation to the selected team
            //5. check if the status of the selected team is final
            if (!userIsMetaMember(user, team.getMetaProjectId()) || reachedMaxInvitd(team)
                    || isUserAlreadyInTeam(user, team) || isUserAlreadyTeammember(user, team.getMetaProjectId())
                    || team.getUpdateStatus() == UpdateStatus.FINAL) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            String inviteDate = parserSDF.format(new Date());

            RelationTeamUser relationTeamUser = new RelationTeamUser();
            relationTeamUser.setTeamId(team);
            relationTeamUser.setTeamMemberStatus(TeamMemberStatus.INVITED);
            relationTeamUser.setUserId(user);
            relationTeamUser.setInviteDate(inviteDate);
            relTeamUserDao.save(relationTeamUser);

            // send team invite
            communicationCreatorService.sendCommTeamInvite(user, team);
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            log.info("***** Fehler bei Erstellung der TeamUserRelation: " + e.getMessage() + " *****");
        }
        // return ResponseEntity.created(UriBuilder.fromResource(RelationMetaprojectUserEndpoint.class).path(String.valueOf(userId)).build()).build();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Checks if team reached max size of members with team members and invited members
     */
    private boolean reachedMaxInvitd(Team team) {
        int maxTeamLength = team.getMetaProjectId().getTeamMaxSize();
        Collection<RelationTeamUser> countMembers = relTeamUserDao.findByTeamAndStatus(team);
        if (countMembers.size() >= maxTeamLength) {
            log.info("Can't invite user to team. Team has the maximum count of members.");
            return true;
        }
        return false;
    }

    /**
     * cs fr
     * Checks if team reached max size of members with team members and invited members
     */
    private boolean reachedMaxAmount(Team team) {
        //method for single register
        int maxTeamLength = team.getProjectId().getMaxAmountMember();
        Collection<RelationTeamUser> countMembers = relTeamUserDao.findTeammemberByTeam(team);
        if (countMembers.size() >= maxTeamLength) {
            log.info("User can't join to team. Team has the maximum count of members.");
            return true;
        }
        return false;
    }

    /**
     * Checks if user is member of the metaproject
     */
    private boolean userIsMetaMember(User user, Metaproject meta) {
        RelationMetaprojectUser relation = relMetaUserDao.findByUserAndMetaproject(user, meta);
        return relation != null;
    }

    /**
     * Checks if team reached max size of members with team members
     */
    private boolean reachedMaxSize(Team team) {
        int maxTeamLength = team.getMetaProjectId().getTeamMaxSize();
        Collection<RelationTeamUser> countMembers = relTeamUserDao.findTeammemberByTeam(team);
        if (countMembers.size() >= maxTeamLength) {
            log.info("Can't add user to team. Team has the maximum count of members.");
            return true;
        }
        return false;
    }

    /**
     * Checks if user has already a relation to the team.
     */
    private boolean isUserAlreadyInTeam(User user, Team team) {
        RelationTeamUser relation = relTeamUserDao.findByUserAndTeam(user, team);
        if (relation != null) {
            log.info("User is already in team.");
            return true;
        }
        return false;
    }

    /**
     * Checks if user is already a member of another team.
     */
    private boolean isUserAlreadyTeammember(User user, Metaproject meta) {
        Collection<RelationTeamUser> relations = relTeamUserDao.findByUserAndMeta(user, meta);
        for (RelationTeamUser relation : relations) {
            if (relation.getTeamMemberStatus() == TeamMemberStatus.TEAMMEMBER) {
                log.info("User is already teammember of a team.");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if user is already a member of another team
     *
     * @return number of free places in all teams
     */
    @GetMapping(value = "/userIsAlreadyTeammember/{metaid}/{userid}")
    public ResponseEntity isUserAlreadyTeammember(@PathVariable Long metaid, @PathVariable Long userid, @RequestHeader String token) {
        User user = userDao.findById(userid);
        Metaproject mp = metaprojectDAO.findById(metaid);
        boolean isUserAlreadyTeammember = isUserAlreadyTeammember(user, mp);
        return ResponseEntity.ok(isUserAlreadyTeammember);
    }

    /**
     * Delete a RelationTeamUserEndpoint in Database
     */
    @DeleteMapping("/team/{teamId}/user/{userId}")
    public ResponseEntity deleteRelationTeamUser(@PathVariable Long teamId, @PathVariable Long userId, @RequestHeader String token) {
        Team team = teamDao.findById(teamId);
        Metaproject mp = team.getMetaProjectId();

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_EDIT, team.getMetaProjectId().getMetaprojectId())
                .checkForTeamRight(Rights.METAPROJECT_TEAM_EDIT, teamId).validate();
        if (!hasRights && mp.getRegisterType().equals("Team")) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        RelationTeamUser relation = relTeamUserDao.findByUserAndTeam(user, team);

        //check if status of team is final and the user is teammember of the team
        if (relation.getTeamMemberStatus() == TeamMemberStatus.TEAMMEMBER
                && team.getUpdateStatus() == UpdateStatus.FINAL
                && !rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_EDIT, team.getMetaProjectId().getMetaprojectId()).validate()
                && team.getMetaProjectId().getRegisterType().equals("Team")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        //check if the user is teamleader of the team
        if (user.getUserId().equals(team.getTeamLeader().getUserId())) {
            log.info("Teamleader can't be removed");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        relTeamUserDao.deleteById(relation.getRelationTeamUserId());

        User currentUser = tokenService.getUserByToken(token);

        // if user is only invited, remove invitation comm and send withdraw communication
        if (relation.getTeamMemberStatus() == TeamMemberStatus.INVITED) {
            // delete comm invite
            Collection<CommTeamInvite> comms = commDao.findCommTeamInviteByTeamAndUser(teamId, user.getUserId());
            for (CommTeamInvite comm : comms) {
                commDao.deleteById(comm.getId());
                communicationCreatorService.sendCommTeamInvitationWithdraw(currentUser, user, comm.getTeam());
            }
        } else {
            // if user is teammember send remove communication
            if (mp.getRegisterType().equals("Team")) {
                communicationCreatorService.sendCommTeamRemoveMember(currentUser, user, team);
            }
        }

        // Set roles for deleted Project
        roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a RelationTeamUserEndpoint in Database
     */
    @DeleteMapping("/team/{teamId}/user/advertised")
    public ResponseEntity deleteAdvertisement(@PathVariable Long teamId, @RequestHeader String token) {
        Team team = teamDao.findById(teamId);
        Long metaId = team.getMetaProjectId().getMetaprojectId();

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_REQUEST, metaId).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = tokenService.getUserByToken(token);
        Collection<RelationTeamUser> relations = relTeamUserDao.findByTeamAndMemberStatus(team, TeamMemberStatus.ADVERTISED);
        relTeamUserDao.deleteAll(relations);

        // delete comm request
        Collection<CommTeamRequest> comms = commDao.findCommTeamRequestByTeamAndUser(teamId, user.getUserId());
        for (CommTeamRequest comm : comms) {
            commDao.deleteById(comm.getId());
            communicationCreatorService.sendCommTeamApplicationWithdraw(user, comm.getTeam());
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/team/{id}/user/{uid}")
    public ResponseEntity findByTeamAndUser(@PathVariable() Long id, @PathVariable Long uid, @RequestHeader String token) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForTeamRight(Rights.METAPROJECT_TEAM_EDIT, id).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(uid);
        Team team = teamDao.findById(id);
        RelationTeamUser rur = relTeamUserDao.findByUserAndTeam(user, team);
        return ResponseEntity.ok(rur);
    }

    /**
     * Get the number of overall free places
     *
     * @return number of free places in all teams
     */
    @GetMapping(value = "/countAllFreePlaces")
    public ResponseEntity countAllFreePlaces(@RequestHeader String token) {

        long amountOfTeams = teamDao.getNumberOfTeams();
        long totalPlaces = amountOfTeams * 6; // every team consists of 6 people
        long amountTeamUserRelations = relTeamUserDao.getNumberOfRelations(); //every entry represents one place in a team

        //total free places = total amount of places - amount of mapped users in a team
        long totalFreePlaces = totalPlaces - amountTeamUserRelations;

        return ResponseEntity.ok(totalFreePlaces);
    }

    /**
     * Get the number of overall free places
     *
     * @return number of free places in all teams
     */
    @GetMapping(value = "/countMetaprojectFreePlaces/{metaid}")
    public ResponseEntity countMetaprojectFreePlaces(@PathVariable Long metaid, @RequestHeader String token) {

        // sum up team sizes of every team
        long totalPlaces = 0;
        Collection<Team> teams = teamDao.findByMetaprojectId(metaid);
        for (Team team : teams) {
            int maxTeamSize = team.getMetaProjectId().getTeamMaxSize();
            totalPlaces += maxTeamSize;
        }

        //count all relationsships in RealtionTeamUser table, every entry represents a user who is assigned to a team or project
        //TODO add count of free places in single project registration

        Metaproject metaproject = metaprojectDAO.findById(metaid);
        long amountTeamUserRelations = relTeamUserDao.getNumberOfRelationsForMetaproject(metaproject);

        //total free places = total amount of places - amount of mapped users in a team
        long totalFreePlaces = totalPlaces - amountTeamUserRelations;
        return ResponseEntity.ok(totalFreePlaces);
    }

    /**
     * Get a RelationTeamUser from Database by user
     *
     * @return RelationTeamUser
     */
    @GetMapping(value = "/team/user/{uid}/relations")
    public ResponseEntity findByUser(@PathVariable Long uid, @RequestHeader String token) {
        //		boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
        //				.checkForTeamRight(Rights.METAPROJECT_TEAM_EDIT, uid).validate();
        //		if (!hasRights) {
        //			log.info(LOG_STATEMENT_NO_PERMISSION);
        //			return Response.status(Status.UNAUTHORIZED).build();
        //		}
        log.info("find" + RelationTeamUser.class.getName() + " user (uid:" + uid + ")");
        Collection<RelationTeamUser> relationsTeamUser = relTeamUserDao.findByUser(userDao.findById(uid));
        return ResponseEntity.ok(relationsTeamUser);
    }


    @GetMapping(value = "/team/user/{uid}/myteams")
    public ResponseEntity<List<TeamsByUserInfoDTO>> findTeamByUser(@PathVariable Long uid, @RequestHeader String token) {
        log.info("Request <-- GET /team/user/{uid}/myteams");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.METAPROJECT_LIST).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Collection<RelationTeamUser> rur = relTeamUserDao.findByUser(userDao.findById(uid));
        User userOfToken = tokenService.getUserByToken(token);

        List<TeamsByUserInfoDTO> result = Lists.newArrayList();

        for (RelationTeamUser relation : rur) {
            Team team = teamDao.findById(relation.getTeamId().getTeamId());
            boolean hasRight = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_VIEW_INFO, team.getMetaProjectId().getMetaprojectId()).validate();
            if (!hasRight) {
                continue;
            }
            Collection<RelationTeamUser> relationsByTeam = relationTeamUserDAO.findByTeam(relation.getTeamId());

            TeamsByUserInfoDTO dto = new TeamsByUserInfoDTO();
            dto.teamName = team.getTeamName();
            dto.projectId = team.getProjectId();
            dto.teamLeader = team.getTeamLeader();
            dto.teamId = team.getTeamId();
            dto.projectAssignmentStatus = (team.getProjectAssignmentStatus() == null) ? null : team.getProjectAssignmentStatus().name();
            dto.updateStatus = team.getUpdateStatus().name();
            dto.metaProjectId = team.getMetaProjectId();

            for (RelationTeamUser relationTeamUser : relationsByTeam) {
                User teamMember = relationTeamUser.getUserId();
                dto.teamMembers.add(teamMember);

                if (teamMember.getUserId().equals(userOfToken.getUserId())) {
                    dto.isInvited = (relationTeamUser.getTeamMemberStatus().name().equals("INVITED")) ? true : false;
                }
            }
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }


    /**
     * Get all RelationTeamUserEndpoint from Database by team
     *
     * @return Collection<RelationTeamUserEndpoint>
     */
    @GetMapping(value = "/team/{teamid}/user")
    public ResponseEntity getAllTeamUser(@PathVariable Long teamid, @RequestHeader String token) {
        Team team = teamDao.findById(teamid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_VIEW_INFO, team.getMetaProjectId().getMetaprojectId()).validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<RelationTeamUser> result = relTeamUserDao.findByTeam(team);
        log.info("Anzahl treffer (getAllTeamUser):" + result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * Get all RelationTeamUserEndpoint from Database by team, but only the
     * confirmed relations between a team and a user.
     *
     * @return Collection<RelationTeamUserEndpoint>
     */
    @GetMapping(value = "/team/{teamid}/user/confirmed")
    public ResponseEntity getAllConfirmedTeamUser(@PathVariable Long teamid, @RequestHeader String token) {
        Team team = teamDao.findById(teamid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_VIEW_INFO, team.getMetaProjectId().getMetaprojectId())
                .validate();
        if (!hasRights) {
            log.info(LOG_STATEMENT_NO_PERMISSION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<RelationTeamUser> result = relTeamUserDao.findTeammemberByTeam(team);
        return ResponseEntity.ok(result);
    }
}
