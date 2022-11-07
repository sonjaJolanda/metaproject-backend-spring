package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.PrioStatus;
import main.java.org.htwg.konstanz.metaproject.enums.ProjectAssignmentStatus;
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

import javax.persistence.OptimisticLockException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author maweissh, StChiari
 * @version 1.1
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class TeamEndpoint {

    private final static Logger log = LoggerFactory.getLogger(TeamEndpoint.class);

    private final RightService rightService;

    private final RoleDAO roleDao;

    private final TokenService tokenService;

    private final TeamDAO teamDao;

    private final UserDAO userDao;

    private final MetaprojectDAO metaDAO;

    private final CommunicationService communicationService;

    private final PrioTeamProjectDAO prioTeamProjectDao;

    private final RelationTeamUserDAO relationTeamUserDao;

    private final CommunicationCreatorService communicationCreatorService;

    private final CommDAO commDao;

    public TeamEndpoint(RightService rightService, RoleDAO roleDao, TokenService tokenService, TeamDAO teamDao, UserDAO userDao, MetaprojectDAO metaDAO, CommunicationService communicationService, PrioTeamProjectDAO prioTeamProjectDao, RelationTeamUserDAO relationTeamUserDao, CommunicationCreatorService communicationCreatorService, CommDAO commDao) {
        this.rightService = rightService;
        this.roleDao = roleDao;
        this.tokenService = tokenService;
        this.teamDao = teamDao;
        this.userDao = userDao;
        this.metaDAO = metaDAO;
        this.communicationService = communicationService;
        this.prioTeamProjectDao = prioTeamProjectDao;
        this.relationTeamUserDao = relationTeamUserDao;
        this.communicationCreatorService = communicationCreatorService;
        this.commDao = commDao;
    }

    @PostMapping(value = "metaproject/{metaid}/team")
    public ResponseEntity createTeam(@RequestBody Team team, @RequestHeader String token, @PathVariable Long metaid) throws ParseException {
        log.info("createTeam: check Token: " + token);

        Metaproject metaproject = metaDAO.findById(metaid);

        //Change from Right.METAPROJECT_TEAM_CREATE_ALWAYS to METAPROJECT_TEAM_CREATE because METAPROJECT_TEAM_CREATE_ALWAYS not exist
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_CREATE, metaid).validate();
        if (!hasRights && metaproject.getRegisterType().equals("Team")) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date regEnd = parserSDF.parse(metaproject.getTeamRegEnd());
        Date regStart = parserSDF.parse(metaproject.getTeamRegStart());
        Date now = new Date();

        if (!rightService.newRightHandler(token).checkForSuperUser().validate()) {
            if (!(regEnd.after(now) && regStart.before(now)) && metaproject.getRegisterType().equals("Team")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        if (!validTeam(metaid, team) && metaproject.getRegisterType().equals("Team"))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        log.info("createTeam: " + team.toString());

        User user = tokenService.getUserByToken(token);
        Metaproject mp = metaDAO.findById(metaid);
        team.setTeamLeader(user);
        team.setUpdateStatus(UpdateStatus.TEMPORARY);
        Team savedTeam = teamDao.save(team);

        if (metaproject.getRegisterType().equals("Team")) {

            //Add Relation to teamleader
            RelationTeamUser relation = new RelationTeamUser();
            relation.setTeamMemberStatus(TeamMemberStatus.TEAMMEMBER);
            relation.setUserId(user);
            relation.setTeamId(savedTeam);
            RelationTeamUser relationTeamLeader = relationTeamUserDao.save(relation);

            roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

            // remove all temporary relations for the teamleader
            Collection<RelationTeamUser> relations = relationTeamUserDao.findByUserAndMeta(user, mp);
            for (RelationTeamUser rel : relations) {
                log.info("Relation: {}", rel.getRelationTeamUserId());
                if (rel.getRelationTeamUserId() != relationTeamLeader.getRelationTeamUserId()) {
                    relationTeamUserDao.deleteById(rel.getRelationTeamUserId());
                }
            }

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
        }

        // Set role for new Team user to user
        roleDao.addRoleTeamToUser(user, savedTeam, DefaultRoles.METAPROJECT_TEAM_LEADER);
        roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_TEAM_MEMBER);

        // team.setTeamId(maxTeamId);
        return ResponseEntity.ok(savedTeam);
    }

    public boolean validTeam(long metaid, Team team) {
        Collection<Team> teams = teamDao.findByMetaprojectId(metaid);

        for (Team entry : teams) {
            if (entry.getTeamName().equals(team.getTeamName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metaprojectleader can save a Team in Database
     */
    @PostMapping(value = "metaproject/{metaid}/team/always")
    public ResponseEntity createTeamMetaprojectLeader(@RequestBody Team team, @RequestHeader String token, @PathVariable Long metaid) {
        log.info("createTeam: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_CREATE_ALWAYS, metaid)
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_CREATE, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!validTeam(metaid, team))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        log.info("createTeam: " + team.toString());
        Metaproject mp = metaDAO.findById(metaid);
        team.setMetaProjectId(mp);
        team.setUpdateStatus(UpdateStatus.TEMPORARY);
        Team savedTeam = teamDao.save(team);

        relationTeamUserDao.deleteByUserAndMeta(team.getTeamLeader(), mp);

        //Add Relation to teamleader
        RelationTeamUser relation = new RelationTeamUser();
        relation.setTeamMemberStatus(TeamMemberStatus.TEAMMEMBER);
        relation.setUserId(savedTeam.getTeamLeader());
        relation.setTeamId(savedTeam);
        relationTeamUserDao.save(relation);

        User currentUser = tokenService.getUserByToken(token);
        User user = savedTeam.getTeamLeader();

        // send appointment info
        communicationCreatorService.sendCommTeamLeaderAppointment(currentUser, user, savedTeam);

        //Remove Role Teamless from Teamleader
        roleDao.removeRoleMetaprojectFromUser(savedTeam.getTeamLeader(), mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

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

        // Set role for new Team user to user
        roleDao.addRoleTeamToUser(savedTeam.getTeamLeader(), savedTeam, DefaultRoles.METAPROJECT_TEAM_LEADER);
        roleDao.addRoleMetaprojectToUser(savedTeam.getTeamLeader(), mp, DefaultRoles.METAPROJECT_TEAM_MEMBER);

        // team.setTeamId(maxTeamId);
        return ResponseEntity.ok(savedTeam);
    }

    @DeleteMapping("metaproject/{metaid}/team/{id}")
    public ResponseEntity deleteTeamById(@PathVariable Long id, @RequestHeader String token, @PathVariable Long metaid) {

        log.info("deleteTeam: check Token: " + token);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_DELETE, metaid)
                .checkForTeamRight(Rights.METAPROJECT_TEAM_DELETE, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Before deleting a team first delete all connected tables/table cloums
        Team team = teamDao.findById(id);
        if (team == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Metaproject mp = metaDAO.findById(metaid);
        try {
            roleDao.removeRolesTeam(team);
            // delete all comms
            communicationService.revokeCommsForTeam(team.getTeamId());

            prioTeamProjectDao.deleteByTeam(team.getTeamId());
            Collection<RelationTeamUser> relations = relationTeamUserDao.findByTeam(team);
            for (RelationTeamUser rel : relations) {
                roleDao.addRoleMetaprojectToUser(rel.getUserId(), mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
            }
            relationTeamUserDao.deleteByTeam(team);
            teamDao.delete(id);
            // create request communication object
            User actionUser = tokenService.getUserByToken(token);
            communicationCreatorService.sendCommTeamDelete(actionUser, team.getProjectId(), team, relations);
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = userDao.findById(team.getTeamLeader().getUserId());

        roleDao.removeRoleTeamFromUser(user, team, DefaultRoles.METAPROJECT_TEAM_LEADER);
        roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_TEAM_MEMBER);
        roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

        return ResponseEntity.ok().build();

        // remove method could not be enough
        // em.remove(team);
        // return Response.noContent().build();
    }

    /**
     * Get a Team to assigned project
     *
     * @return Team
     */
    @GetMapping(value = "metaproject/{metaid}/team/{pid}/project")
    public ResponseEntity findTeamByProjectId(@PathVariable Long metaid, @PathVariable Long pid, @RequestHeader String token) {
        log.info("findTeamByProjectId: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // find method returns a team with a matching teamId
        Team team = teamDao.findByProjectId(pid);
        return ResponseEntity.ok(team);
    }

    /**
     * Get a Team from Database
     *
     * @return Team
     */
    @GetMapping(value = "metaproject/{metaid}/team/{id}")
    public ResponseEntity findTeamById(@PathVariable Long metaid, @PathVariable Long id, @RequestHeader String token) {
        log.info("getTeam: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_LIST, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Team team = teamDao.findById(id);
        return ResponseEntity.ok(team);
    }

    @GetMapping(value = "metaproject/{metaid}/team")
    public ResponseEntity getAllTeams(@PathVariable Long metaid, @RequestHeader String token) {
        log.info("getAllTeams: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_LIST, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<Team> teams = teamDao.findByMetaprojectId(metaid);
       /* for (Team team : teams) {
            em.detach(team);
        }*/
        return ResponseEntity.ok(teams);
    }

    @PutMapping(value = "metaproject/{metaid}/team/{id}")
    public ResponseEntity updateTeamById(@RequestBody Team team, @RequestHeader String token, @PathVariable Long metaid, @PathVariable Long id) {
        log.info("UpdateTeam");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_EDIT, metaid)
                .checkForTeamRight(Rights.METAPROJECT_TEAM_EDIT, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Team oldTeam = teamDao.findById(id);
        if (oldTeam.getUpdateStatus() == UpdateStatus.FINAL)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (oldTeam.getTeamName().equals(team.getTeamName()))
            return ResponseEntity.noContent().build();

        if (validTeam(metaid, team)) {
            try {
                teamDao.save(team);
            } catch (OptimisticLockException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getEntity());
            }
            return ResponseEntity.noContent().build();
        } else {
            log.info("Not valid!");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping(value = "metaproject/{metaid}/team/{param}/assignment")
    public ResponseEntity updateProjectAssignment(@RequestBody Collection<Team> teams, @PathVariable Long metaid,
                                                  @PathVariable("param") ProjectAssignmentStatus status, @RequestHeader String token) throws ParseException {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_SET_TEAM, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Metaproject metaproject = metaDAO.findById(metaid);
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date regEnd = parserSDF.parse(metaproject.getTeamRegEnd());
        Date now = new Date();

        if (!rightService.newRightHandler(token).checkForSuperUser().validate()) {
            if (!((regEnd.before(now) && status == ProjectAssignmentStatus.FINAL) || status == ProjectAssignmentStatus.TEMPORARY)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        List<Project> projects = new ArrayList<>();
        for (Team team : teams) {
            if (team.getProjectId() != null) {
                projects.add(team.getProjectId());
            }
        }
        if (hasDuplicates(projects)) {
            log.info("Jedes Projekt darf nur einmal zugewiesen werden.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        for (Team team : teams) {
            if (rightService.newRightHandler(token).checkForSuperUser().validate() || team.getProjectAssignmentStatus() != ProjectAssignmentStatus.FINAL) {
                team.setProjectAssignmentStatus(status);
                teamDao.update(team, team.getTeamId());
            } else {
                log.error("Die Zuweisung ist bereits final gesetzt worden und kann nicht mehr verändert werden.");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        User actionUser = tokenService.getUserByToken(token);
        if (status.equals(ProjectAssignmentStatus.FINAL)) {
            communicationCreatorService.sendCommAssingProjectToTeam(actionUser, metaproject);
        }
        return ResponseEntity.ok().build();
    }

    public static boolean hasDuplicates(List<Project> projects) {
        final List<Long> usedProjects = new ArrayList<>();
        for (Project project : projects) {
            final Long id = project.getProjectId();
            if (usedProjects.contains(id))
                return true;
            usedProjects.add(id);
        }
        return false;
    }

    /**
     * Update status of a Team in Database
     */
    @PutMapping(value = "metaproject/{metaid}/team/{id}/unlock")
    public ResponseEntity unlockTeam(@RequestHeader String token, @PathVariable Long metaid, @PathVariable Long id) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_UNLOCK, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Team team = teamDao.findById(id);
        team.setUpdateStatus(UpdateStatus.TEMPORARY);
        team.setProjectAssignmentStatus(null);
        team.setProjectId(null);
        teamDao.update(team, id);

        Collection<Team> mpTeams = teamDao.findByMetaprojectId(metaid);
        for (Team mpTeam : mpTeams) {
            mpTeam.setProjectAssignmentStatus(ProjectAssignmentStatus.TEMPORARY);
        }

        Collection<PrioTeamProject> prios = prioTeamProjectDao.findByTeam(id);
        for (PrioTeamProject prio : prios) {
            prio.setStatus(PrioStatus.TEMPORARY);
            prioTeamProjectDao.update(prio.getPrioTeamProject(), prio);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "metaproject/{metaid}/team/{id}/change")
    public ResponseEntity changeLeader(@RequestBody User user, @RequestHeader String token, @PathVariable Long metaid, @PathVariable Long tid) {
        log.info("TEAM_LEADER_CHANGE: Update team leader!");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForTeamRight(Rights.METAPROJECT_TEAM_EDIT, tid)
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_EDIT, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Team team = teamDao.findById(tid);
        if (team.getUpdateStatus() == UpdateStatus.FINAL)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        // find team member
        RelationTeamUser relTeamLeader = relationTeamUserDao.findByUserAndTeam(user, team);
        if (relTeamLeader == null) {
            log.error("No team member found with that id!");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        //save old leader for communication call
        User oldLeader = team.getTeamLeader();
        //remove role from old teamleader
        roleDao.removeRoleTeamFromUser(oldLeader, team, DefaultRoles.METAPROJECT_TEAM_LEADER);
        // set role to new teamleader
        roleDao.addRoleTeamToUser(relTeamLeader.getUserId(), team, DefaultRoles.METAPROJECT_TEAM_LEADER);
        // set new teamleader
        team.setTeamLeader(relTeamLeader.getUserId());
        teamDao.update(team, team.getTeamId());

        User actionUser = tokenService.getUserByToken(token);
        communicationCreatorService.sendCommTeamLeaderChange(actionUser, relTeamLeader.getUserId(), oldLeader, team);

        return ResponseEntity.ok().build();
    }
}
