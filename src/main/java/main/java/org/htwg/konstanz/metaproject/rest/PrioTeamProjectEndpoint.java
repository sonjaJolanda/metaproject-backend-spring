package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.PrioStatus;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import main.java.org.htwg.konstanz.metaproject.enums.UpdateStatus;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
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
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author maweissh
 * @version 1.0
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class PrioTeamProjectEndpoint {

    private final static Logger log = LoggerFactory.getLogger(PrioTeamProjectEndpoint.class);

    private final RightService rightService;

    private final MetaprojectDAO metaDAO;

    private final TeamDAO teamDAO;

    private final RelationTeamUserDAO relTeamUserDAO;

    private final PrioTeamProjectDAO prioDAO;

    private final ProjectDAO projectDAO;

    private final PrioTeamProjectDAO prioTeamProjectDao;

    private final RelationTeamUserDAO relTeamUserDao;

    private final CommDAO commDao;

    private final TokenService tokenService;

    private final CommunicationCreatorService communicationCreatorService;

    public PrioTeamProjectEndpoint(RightService rightService, MetaprojectDAO metaDAO, TeamDAO teamDAO, RelationTeamUserDAO relTeamUserDAO, PrioTeamProjectDAO prioDAO, ProjectDAO projectDAO, PrioTeamProjectDAO prioTeamProjectDao, RelationTeamUserDAO relTeamUserDao, CommDAO commDao, TokenService tokenService, CommunicationCreatorService communicationCreatorService) {
        this.rightService = rightService;
        this.metaDAO = metaDAO;
        this.teamDAO = teamDAO;
        this.relTeamUserDAO = relTeamUserDAO;
        this.prioDAO = prioDAO;
        this.projectDAO = projectDAO;
        this.prioTeamProjectDao = prioTeamProjectDao;
        this.relTeamUserDao = relTeamUserDao;
        this.commDao = commDao;
        this.tokenService = tokenService;
        this.communicationCreatorService = communicationCreatorService;
    }

    @PostMapping(value = "metaproject/{metaid}/prioteamproject/{status}")
    public ResponseEntity createNewPriority(@RequestBody Collection<PrioTeamProject> priorities, @PathVariable PrioStatus status, @RequestHeader String token,
                                            @PathVariable Long metaid) throws ParseException {
        log.info("Request <-- POST /metaproject/{}/prioteamproject/{}", metaid, status);

        if (priorities.size() == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        int should_count = 0;
        int actual_count = 0;

        Collection<Project> projects = projectDAO.findByMetaproject(metaid);
        log.info("ProjectSize: " + projects.size());
        for (int i = 0; i < projects.size(); i++) {
            should_count += (i + 1) * (i + 1);
        }
        for (PrioTeamProject entity : priorities) {
            if (entity.getPrioritisation() != null) {
                actual_count += Integer.parseInt(entity.getPrioritisation()) * Integer.parseInt(entity.getPrioritisation());
            }
        }

        log.info("actual: " + actual_count + "should_Count" + should_count);

        if (should_count != actual_count) {
            log.info("Jede Priorisierung darf/muss nur einmal vergeben werden.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        log.info(priorities.toString());
        log.info("priorities size: " + priorities.size());
        for (PrioTeamProject priority : priorities) {

            boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid)
                    .checkForTeamRight(Rights.METAPROJECT_PRIO_SET, priority.getTeamId().getTeamId()).validate();
            if (!hasRights) {
                log.info("User has no permissions to do that operation.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Metaproject metaproject = metaDAO.findById(metaid);

            // Check TeamSize + Check Status = Final
            if (!minTeamSize(metaproject, priority.getTeamId()) && status == PrioStatus.FINAL && !rightService.newRightHandler(token).checkForSuperUser()
                    .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid).validate()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }

            SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date teamRegEnd = parserSDF.parse(metaproject.getTeamRegEnd());
            Date projectRegEnd = parserSDF.parse(metaproject.getProjectRegEnd());
            Date now = new Date();

            /* Why do we need this Right check?
            //
            if (!(teamRegEnd.after(now) && projectRegEnd.before(now))) {
                log.info("");
                return Response.status(Status.BAD_REQUEST).build();
            }
            */

            // Why do we need this Right check?
            /*
            if (!rightService.newRightHandler(token).checkForSuperUser().checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid).validate()) {
                log.info("Ich bin gekommen - aber Warum!!!!!");
                return Response.status(Status.BAD_REQUEST).build();
            }
            */

            if (priority.getPrioTeamProject() != null && priority.getPrioTeamProject() != 0) {
                /*
                if (rightService.newRightHandler(token).checkForSuperUser().validate() || priority.getStatus() == PrioStatus.TEMPORARY) {
                    log.info("Update status and prioritization of team {}", priority.getTeamId().getTeamName());
                    em.createNativeQuery("update Prioteamproject set prioritisation = " + priority.getPrioritisation() + ", status = " + status.ordinal() + " where prioTeamProject = " + priority.getPrioTeamProject()).executeUpdate();
                    em.flush();
                } else {
                    log.info("Die Priorisierung wurde schon final gesetzt und kann nicht mehr verändert werden.");
                    return Response.status(Status.CONFLICT).build();
                }*/
                priority.setStatus(status);
                prioTeamProjectDao.saveWithFlush(priority);

               /* ToDo die 2 zeilen hierüber testen -> soll das gleiche wie die 4 zeilen hierunter sein
                em.createNativeQuery("update Prioteamproject set prioritisation = " + priority.getPrioritisation() + ", status = " + status.ordinal()
                        + " where prioTeamProject = " + priority.getPrioTeamProject()
                ).executeUpdate();
                em.flush();*/
            } else {
                PrioTeamProject prio = new PrioTeamProject();
                prio.setPrioritisation(priority.getPrioritisation());
                prio.setProjectId(priority.getProjectId());
                prio.setTeamId(priority.getTeamId());
                prio.setStatus(status);
                prioDAO.save(prio);
            }
        }

        // create and send a new communication object
        // if prio is set final
        if (PrioStatus.FINAL.equals(status)) {
            PrioTeamProject prio = priorities.iterator().next();
            User actionUser = tokenService.getUserByToken(token);
            Team team = prio.getTeamId();
            communicationCreatorService.sendCommTeamSendPrio(actionUser, team);

            team.setUpdateStatus(UpdateStatus.FINAL);
            teamDAO.update(team, team.getTeamId());

            //delete all relations which are not fixed team member relations
            Collection<RelationTeamUser> relations = relTeamUserDao.findByTeam(team);
            for (RelationTeamUser relation : relations) {
                log.info("Relation: {}", relation.getRelationTeamUserId());
                if (!relation.getTeamMemberStatus().equals(TeamMemberStatus.TEAMMEMBER)) {
                    relTeamUserDao.deleteById(relation.getRelationTeamUserId());
                }
            }

            // withdraw all invitations
            Collection<CommTeamInvite> inviteComms = commDao.findCommTeamInviteByTeam(team.getTeamId());
            for (CommTeamInvite c : inviteComms) {
                if (!CommStatus.FINISHED.equals(c.getStatus())) {
                    commDao.deleteById(c.getId());
                    communicationCreatorService.sendCommTeamInvitationWithdraw(actionUser, c.getTargetUser(), team);
                }
            }

            // reject all requests
            Collection<CommTeamRequest> requestComms = commDao.findCommTeamRequestByTeam(team.getTeamId());
            for (CommTeamRequest c : requestComms) {
                if (CommStatus.UNTOUCHED.equals(c.getStatus())) {
                    c.setAnswer(CommAnswer.NEGATIVE);
                    c.setStatus(CommStatus.FINISHED);
                    communicationCreatorService.sendCommTeamApplicationReject(c.getSendingUser(), c.getTeam());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    public boolean minTeamSize(Metaproject meta, Team team) {
        Collection<RelationTeamUser> relations = relTeamUserDAO.findByTeam(team);
        int count = 0;
        for (RelationTeamUser relation : relations) {
            if (relation.getTeamMemberStatus() == TeamMemberStatus.TEAMMEMBER) {
                count++;
            }
        }
        return count >= meta.getTeamMinSize();
    }

    @DeleteMapping("metaproject/{metaid}/prioteamproject/{tid}")
    public ResponseEntity deletePriorityById(@PathVariable Long tid, @PathVariable Long metaid, @RequestHeader String token) {
        log.info("Request <-- DELETE /metaproject/{}/prioteamproject/{}", metaid, tid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid)
                .checkForTeamRight(Rights.METAPROJECT_PRIO_SET, tid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<PrioTeamProject> prios = prioTeamProjectDao.findByMetaAndTeam(metaid, tid);
        for (PrioTeamProject prio : prios) {
            prioTeamProjectDao.delete(prio.getPrioTeamProject());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "metaproject/{metaid}/prioteamproject/{tid}/{pid}")
    public ResponseEntity findPriorityByProjectAndTeam(@PathVariable Long tid, @PathVariable Long pid, @RequestHeader String token, @PathVariable Long metaid) {
        log.info("Request <-- GET /metaproject/{}/prioteamproject/{}/{}", metaid, tid, pid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid)
                .checkForTeamRight(Rights.METAPROJECT_PRIO_SET, tid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<PrioTeamProject> resultList = prioTeamProjectDao.findByTeamAndProject(tid, pid);
        log.info("Anzahl treffer (findPriorityById):" + resultList.size());
        return ResponseEntity.ok(resultList.stream().findFirst().orElse(null));
    }

    @GetMapping(value = "metaproject/{metaid}/prioteamproject/{tid}")
    public ResponseEntity findPrioritiesByTeam(@PathVariable Long tid, @RequestHeader String token, @PathVariable Long metaid) {
        log.info("Request <-- GET /metaproject/{}/prioteamproject/{}", metaid, tid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_PRIO_SET, metaid)
                .checkForTeamRight(Rights.METAPROJECT_PRIO_SET, tid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Collection<PrioTeamProject> prios = prioTeamProjectDao.findByTeam(tid);
        return ResponseEntity.ok(prios);
    }

    @GetMapping(value = "metaproject/{metaid}/prioteamproject")
    public ResponseEntity findPrioritiesByMeta(@PathVariable Long metaid, @RequestHeader String token) {
        log.info("Request <-- POST /metaproject/{}/prioteamproject", metaid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_PRIO, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Collection<PrioTeamProject> prios = prioTeamProjectDao.findByMeta(metaid);
        return ResponseEntity.ok(prios);
    }

    /**
     * Update status of the priorisation of a Team in Database
     */
    @PutMapping(value = "metaproject/{metaid}/prioteamproject/{tid}/unlock")
    public ResponseEntity unlockPrio(@RequestHeader String token, @PathVariable Long metaid, @PathVariable Long tid) {

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_TEAM_UNLOCK, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<PrioTeamProject> prios = prioTeamProjectDao.findByTeam(tid);
        for (PrioTeamProject prio : prios) {
            prio.setStatus(PrioStatus.TEMPORARY);
            prioTeamProjectDao.update(prio.getPrioTeamProject(), prio);
        }
        Team team = teamDAO.findById(tid);
        team.setUpdateStatus(UpdateStatus.TEMPORARY);
        teamDAO.update(team, tid);
        return ResponseEntity.ok().build();
    }
}
