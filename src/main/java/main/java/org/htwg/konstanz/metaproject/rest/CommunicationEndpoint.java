package main.java.org.htwg.konstanz.metaproject.rest;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.communication.*;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.*;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import main.java.org.htwg.konstanz.metaproject.enums.UpdateStatus;
import main.java.org.htwg.konstanz.metaproject.persistance.CommDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.RelationTeamUserDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.RoleDAO;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationCreatorService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/communication")
public class CommunicationEndpoint {

    private static final Logger log = LoggerFactory.getLogger(CommunicationEndpoint.class);

    private final CommunicationService communicationService;

    private final CommunicationCreatorService communicationCreatorService;

    private final TokenService tokenService;

    private final RelationTeamUserDAO relTeamUserDao;

    private final RoleDAO roleDao;

    private final CommDAO commDao;

    public CommunicationEndpoint(CommunicationService communicationService, CommunicationCreatorService communicationCreatorService, TokenService tokenService, RelationTeamUserDAO relTeamUserDao, RoleDAO roleDao, CommDAO commDao) {
        this.communicationService = communicationService;
        this.communicationCreatorService = communicationCreatorService;
        this.tokenService = tokenService;
        this.relTeamUserDao = relTeamUserDao;
        this.roleDao = roleDao;
        this.commDao = commDao;
    }

    /**
     * Get all existing communication where the user by token is sending user.
     * These are only not answered communications of type
     * {@link CommAgreeRejectAbstract}.
     */
    @GetMapping(value = "/sending")
    public ResponseEntity<Object> getSendingCommsForUser(@RequestHeader String token) {
        log.info("Request <-- GET /communication/sending");

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Collection<CommAgreeRejectAbstract> sendingAll = communicationService
                .findUnrespondedCommunicationsBySendingUser(tokenInfo.getUserId());
        return ResponseEntity.ok(sendingAll);
    }

    /**
     * Get all existing communication where the user by token is target user.
     *
     * @RequestParam limit and
     * @RequestParam offset need to be either both used or none of them
     */
    @GetMapping(value = "/target")
    public ResponseEntity<Object> getTargetCommsForUser(@RequestHeader String token, @RequestParam(value = "limit", required = false) Integer limit, @RequestParam(value = "offset", required = false) Integer offset) {
        log.info("Request <-- GET /communication/target");

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Collection<CommAbstract> targetAll = null;
        if (offset != null && limit != null) {
            targetAll = communicationService.findCommunicationsByTargetUser(tokenInfo.getUserId(), offset, limit);
        } else {
            targetAll = communicationService.findCommunicationsByTargetUser(tokenInfo.getUserId());
        }
        return ResponseEntity.ok(targetAll);
    }

    /**
     * Get all unresponded communications for a user by token.
     */
    @GetMapping(value = "/targetUnresponded")
    public ResponseEntity<Object> getAllUnrespondedTargetCommsForUser(@RequestHeader String token) {
        log.info("Request <-- GET /communication/targetUnresponded");

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Collection<CommAbstract> targetUnresponded = communicationService
                .findUnrespondedCommunicationsByTargetUser(tokenInfo.getUserId());

        return ResponseEntity.ok(targetUnresponded);
    }

    /**
     * marks a message as {@link CommRead#READ} and saves the current time as readAt
     * @param id
     * @param token
     * @return
     */
    @PutMapping(value = "/{id}/markAsRead")
    public ResponseEntity<Object> markMessageAsRead(@PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- PUT /communication/{}/markAsRead", id);

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // load communication by id
        CommAbstract comm = communicationService.readCommunication(id, tokenInfo.getUserId());
        if (comm == null)
            return ResponseEntity.badRequest().build();
        else
            comm.setReadStatus(CommRead.READ);
        comm.setReadAt(new Date());
        commDao.save(comm);
        return ResponseEntity.ok(comm);
    }

    /**
     * marks all messages as {@link CommRead#READ} and their readAt time
     *
     * @param token
     * @param ids
     * @return
     */
    @PutMapping(value = "/markAllAsRead")
    public ResponseEntity<Object> markAllMessaAsRead(@RequestHeader String token, @RequestBody Long[] ids) {
        log.info("Request <-- PUT /communication/markAllAsRead");

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // load communication by id
        List<CommAbstract> comms = Lists.newArrayList();
        for (Long id : ids) {
            CommAbstract comm =communicationService.readCommunication(id, tokenInfo.getUserId());
            comm.setReadStatus(CommRead.READ);
            log.info("versucht zu readen: " + comm.getId());
            comm.setReadAt(new Date());
            comms.add(commDao.save(comm));
        }
        return ResponseEntity.ok(comms);
    }


    /**
     * Get the count of all unresponded communications for a user by token.
     */
    @GetMapping(value = "/unresponded")
    public ResponseEntity<Object> getAllUnrespondedCountCommsForUser(@RequestHeader String token) {
        log.info("Request <-- GET /communication/unresponded");

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Collection<CommAbstract> targetUnresponded = communicationService
                .findUnrespondedCommunicationsByTargetUser(tokenInfo.getUserId());

        return ResponseEntity.ok(String.format("{\"unresponded\":%s}", targetUnresponded.size()));
    }

    /**
     * Read(delete!) a communication of type {@link CommInfoAbstract} and id. The status
     * of this communication has to be {@link CommStatus#UNTOUCHED} and the
     * logged in user has to be target user. This could be used for every comm
     * of type {@link CommReactionType#INFORMATION}.
     */
    @PutMapping(value = "/{id}/read")
    public ResponseEntity<Object> readCommInfo(@PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- PUT /communication/{}/read", id);

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // load communication by id
        CommInfoAbstract comm = communicationService.deleteCommunication(id, tokenInfo.getUserId());
        if (comm == null)
            return ResponseEntity.badRequest().build();
        else
            return ResponseEntity.ok(comm);
    }

    /**
     * Answer a communication of type {@link CommAgreeRejectAbstract} and id.
     * The status of this communication has to be {@link CommStatus#UNTOUCHED}
     * and the logged in user has to be target user. This could be used for
     * every comm of type {@link CommReactionType#AGREE_REJECT}.
     */
    @PutMapping(value = "/{id}/{answer}/answer")
    public ResponseEntity<Object> readCommAgreeReject(@PathVariable CommAnswer answer, @PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- PUT /communication/{}/{}/answer", id, answer.toString());

        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // load communication by id
        CommAgreeRejectAbstract comm = communicationService.validateAnswerCommunication(id, answer, tokenInfo.getUserId());
        if (comm == null) {
            log.error("Answer is not allowed.");
            return ResponseEntity.badRequest().body(AnswerStatusObject.getInstance(ErrorCode.NOT_ACTUAL));
        }

        AnswerStatusObject actionStatus;
        switch (comm.getType()) {
            case TEAM_REQUEST:
                actionStatus = answerActionForTeamRequest((CommTeamRequest) comm, answer);
                break;
            case TEAM_INVITE:
                actionStatus = answerActionForTeamInvite((CommTeamInvite) comm, answer);
                break;
            default:
                actionStatus = AnswerStatusObject.getInstance(ErrorCode.BAD_REQUEST);
                break;
        }

        // check whether action was performed correct
        if (!actionStatus.getErrorCode().equals(ErrorCode.OK)) {
            log.error("Action was not successfully performed.");
            return ResponseEntity.badRequest().body(actionStatus);
        }

        // persist answer for communication
        comm = communicationService.answerCommunication(id, answer, tokenInfo.getUserId());
        return ResponseEntity.ok(comm);
    }

    /**
     * Special actions for a communication object of type
     * {@link CommType#TEAM_INVITE}.
     */
    private AnswerStatusObject answerActionForTeamInvite(CommTeamInvite comm, CommAnswer answer) {
        if (CommAnswer.POSITIVE.equals(answer)) {
            log.info("TEAM_INVITE: Insert a new teamUserRelation for this user: {} into team: {}",
                    comm.getTargetUser().getUserId(), comm.getTeam().getTeamId());

            //check if team has the maximum count of members
            if (reachedMaxSize(comm.getTeam())) {
                return AnswerStatusObject.getInstance(ErrorCode.TEAM_IS_FULL);
            }
            // check if user is already team member of an other team
            if (userIsAlreadyTeammember(comm.getTargetUser(), comm.getTeam().getMetaProjectId())) {
                return AnswerStatusObject.getInstance(ErrorCode.USER_IS_ALREADY_IN_TEAM);
            }
            // check if the status of the selected team is final
            if (comm.getTeam().getUpdateStatus() == UpdateStatus.FINAL) {
                return AnswerStatusObject.getInstance(ErrorCode.TEAM_IS_LOCKED);
            }

            RelationTeamUser oldRelResp = relTeamUserDao.findByUserAndTeam(comm.getTargetUser(), comm.getTeam());
            relTeamUserDao.updateStatus(oldRelResp.getRelationTeamUserId(), TeamMemberStatus.TEAMMEMBER);

            roleDao.addRoleMetaprojectToUser(comm.getTargetUser(), comm.getTeam().getMetaProjectId(),
                    DefaultRoles.METAPROJECT_TEAM_MEMBER);
            roleDao.removeRoleMetaprojectFromUser(comm.getTargetUser(), comm.getTeam().getMetaProjectId(),
                    DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

            Collection<RelationTeamUser> relations = relTeamUserDao.findByUserAndMeta(comm.getTargetUser(),
                    comm.getTeam().getMetaProjectId());
            for (RelationTeamUser relation : relations) {
                log.info("Relation: {}", relation.getRelationTeamUserId());
                if (relation.getRelationTeamUserId() != oldRelResp.getRelationTeamUserId()) {
                    relTeamUserDao.deleteById(relation.getRelationTeamUserId());
                }
            }

            // revoke other communications
            User user = comm.getTargetUser();
            // withdraw all invitations for the user
            Collection<CommTeamInvite> inviteComms = communicationService.findTeamInvitesForUser(user,
                    comm.getTeam().getMetaProjectId().getMetaprojectId());
            for (CommTeamInvite c : inviteComms) {
                // don't remove the actual communication!
                if (c.getId().equals(comm.getId())) {
                    continue;
                }
                c.setAnswer(CommAnswer.NEGATIVE);
                c.setStatus(CommStatus.FINISHED);
                communicationCreatorService.sendCommTeamInvitationReject(user, c.getSendingUser(), c.getTeam());
            }

            // reject all comm requests the user has sent
            Collection<CommTeamRequest> requestComms = communicationService.findTeamRequestsForUser(user,
                    comm.getTeam().getMetaProjectId().getMetaprojectId());
            for (CommTeamRequest c : requestComms) {
                commDao.deleteById(c.getId());
                communicationCreatorService.sendCommTeamApplicationWithdraw(c.getSendingUser(), c.getTeam());
            }

            // send agree comm
            communicationCreatorService.sendCommTeamInvitationAccept(comm.getTargetUser(), comm.getTeam());
            return AnswerStatusObject.getInstance(ErrorCode.OK);
        } else if (CommAnswer.NEGATIVE.equals(answer)) {
            RelationTeamUser relation = relTeamUserDao.findByUserAndTeam(comm.getTargetUser(), comm.getTeam());
            relTeamUserDao.deleteById(relation.getRelationTeamUserId());

            // send withdraw comm
            communicationCreatorService.sendCommTeamInvitationReject(comm.getTargetUser(), comm.getSendingUser(), comm.getTeam());
            return AnswerStatusObject.getInstance(ErrorCode.OK);
        } else {
            return AnswerStatusObject.getInstance(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Method checks if team has reached the maximum of team members
     *
     * @return true if team is complete
     */
    private boolean reachedMaxSize(Team team) {
        int maxTeamLength = team.getMetaProjectId().getTeamMaxSize();
        Collection<RelationTeamUser> countMembers = relTeamUserDao.findTeammemberByTeam(team);
        if (countMembers.size() >= maxTeamLength) {
            log.info("Team has the maximum count of members.");
            return true;
        }
        return false;
    }

    /**
     * Method checks if team has reached the maximum of team members
     *
     * @return true if team is complete
     */
    private boolean reachedMaxInvited(Team team) {
        int maxTeamLength = team.getMetaProjectId().getTeamMaxSize();
        Collection<RelationTeamUser> countMembers = relTeamUserDao.findByTeamAndStatus(team);
        if (countMembers.size() >= maxTeamLength) {
            log.info("Team has the maximum count of members.");
            return true;
        }
        return false;
    }

    /**
     * Method checks if user is member of another team in the metaproject.
     *
     * @return true if user is member of another team
     */
    private boolean userIsAlreadyTeammember(User user, Metaproject meta) {
        Collection<RelationTeamUser> relations = relTeamUserDao.findByUserAndMeta(user, meta);
        for (RelationTeamUser relation : relations) {
            if (relation.getTeamMemberStatus() == TeamMemberStatus.TEAMMEMBER) {
                log.info("User is allready teammember of a team.");
                return true;
            }
        }
        return false;
    }

    /**
     * Special actions for a communication object of type
     * {@link CommType#TEAM_REQUEST}.
     */
    private AnswerStatusObject answerActionForTeamRequest(CommTeamRequest comm, CommAnswer answer) {
        if (CommAnswer.POSITIVE.equals(answer)) {
            log.info("TEAM_REQUEST: Insert a new teamUserRelation for this user: {} into team: {}",
                    comm.getSendingUser().getUserId(), comm.getTeam().getTeamId());

            //check if team has the maximum count of members
            if (reachedMaxInvited(comm.getTeam())) {
                return AnswerStatusObject.getInstance(ErrorCode.TEAM_IS_FULL);
            }
            //check if user is already team member of an other team
            if (userIsAlreadyTeammember(comm.getSendingUser(), comm.getTeam().getMetaProjectId())) {
                return AnswerStatusObject.getInstance(ErrorCode.USER_IS_ALREADY_IN_TEAM);
            }
            //check if the status of the selected team is final
            if (comm.getTeam().getUpdateStatus() == UpdateStatus.FINAL) {
                return AnswerStatusObject.getInstance(ErrorCode.TEAM_IS_LOCKED);
            }

            RelationTeamUser relation = relTeamUserDao.findByUserAndTeam(comm.getSendingUser(), comm.getTeam());
            relTeamUserDao.updateStatus(relation.getRelationTeamUserId(), TeamMemberStatus.TEAMMEMBER);

            roleDao.addRoleMetaprojectToUser(comm.getSendingUser(), comm.getTeam().getMetaProjectId(),
                    DefaultRoles.METAPROJECT_TEAM_MEMBER);
            roleDao.removeRoleMetaprojectFromUser(comm.getSendingUser(), comm.getTeam().getMetaProjectId(),
                    DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);

            Collection<RelationTeamUser> relTeamUser = relTeamUserDao.findByUserAndMeta(comm.getSendingUser(),
                    comm.getTeam().getMetaProjectId());
            for (RelationTeamUser relationTeamUser : relTeamUser) {
                if (!relationTeamUser.getRelationTeamUserId().equals(relation.getRelationTeamUserId())) {
                    relTeamUserDao.deleteById(relationTeamUser.getRelationTeamUserId());
                }
            }

            // revoke other communications
            User user = comm.getSendingUser();
            // withdraw all invitations for the user
            Collection<CommTeamInvite> inviteComms = communicationService.findTeamInvitesForUser(user,
                    comm.getTeam().getMetaProjectId().getMetaprojectId());
            for (CommTeamInvite c : inviteComms) {
                c.setAnswer(CommAnswer.NEGATIVE);
                c.setStatus(CommStatus.FINISHED);
                communicationCreatorService.sendCommTeamInvitationReject(user, c.getSendingUser(), c.getTeam());
            }

            // reject all comm requests the user has sent
            Collection<CommTeamRequest> requestComms = communicationService.findTeamRequestsForUser(user,
                    comm.getTeam().getMetaProjectId().getMetaprojectId());
            for (CommTeamRequest c : requestComms) {
                // don't remove the actual communication!
                if (c.getId().equals(comm.getId())) {
                    continue;
                }
                commDao.deleteById(c.getId());
                communicationCreatorService.sendCommTeamApplicationWithdraw(c.getSendingUser(), c.getTeam());
            }

            // send agree comm
            communicationCreatorService.sendCommTeamApplicationAccept(comm.getTeam().getTeamLeader(),
                    comm.getSendingUser(), comm.getTeam());

            return AnswerStatusObject.getInstance(ErrorCode.OK);
        } else if (CommAnswer.NEGATIVE.equals(answer)) {
            RelationTeamUser relation = relTeamUserDao.findByUserAndTeam(comm.getSendingUser(), comm.getTeam());
            if (relation != null) {
                relTeamUserDao.deleteById(relation.getRelationTeamUserId());
            }

            // send withdraw comm
            communicationCreatorService.sendCommTeamApplicationReject(comm.getSendingUser(), comm.getTeam());
            return AnswerStatusObject.getInstance(ErrorCode.OK);
        } else {
            return AnswerStatusObject.getInstance(ErrorCode.BAD_REQUEST);
        }
    }

}
