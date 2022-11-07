package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommAgreeRejectAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.CommDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Implementation for {@link CommunicationService} interface.
 *
 * @author SiKelle
 */
@Service
public class CommunicationServiceImpl implements CommunicationService {

    private static final Logger log = LoggerFactory.getLogger(CommunicationServiceImpl.class);

    private final CommDAO commDao;

    public CommunicationServiceImpl(CommDAO commDao) {
        this.commDao = commDao;
    }

    @Override
    public Collection<CommAbstract> findCommunicationsByTargetUser(Long userId, int offset, int limit) {
        return commDao.findByTargetUserId(CommAbstract.class, userId, offset, limit);
    }

    @Override
    public Collection<CommAbstract> findCommunicationsByTargetUser(Long userId) {
        return commDao.findByTargetUserId(CommAbstract.class, userId);
    }

    @Override
    public Collection<CommAgreeRejectAbstract> findUnrespondedCommunicationsBySendingUser(Long userId) {
        return commDao.findUnansweredBySendingUserId(CommAgreeRejectAbstract.class, userId);
    }

    private <T extends CommAbstract> Collection<T> findCommunicationsByTargetUser(Class<T> type, Long userId) {
        return commDao.findByTargetUserId(type, userId);
    }

    @Override
    public Collection<CommAbstract> findUnrespondedCommunicationsByTargetUser(Long userId) {
        return commDao.findUnrespondedByTargetUserId(CommAbstract.class, userId);
    }

    @Override
    public Collection<CommTeamInvite> findTeamInvitesForUser(User user, Long metaprojectId) {
        Collection<CommTeamInvite> all = findCommunicationsByTargetUser(CommTeamInvite.class, user.getUserId());
        Collection<CommTeamInvite> result = new LinkedList<>();
        for (CommTeamInvite comm : all) {
            // if metaproject id from team equals metaproject id
            // and request is not finished
            if (!comm.getStatus().equals(CommStatus.FINISHED)
                    && comm.getTeam().getMetaProjectId().getMetaprojectId().equals(metaprojectId)) {
                result.add(comm);
            }
        }
        return result;
    }

    @Override
    public Collection<CommTeamRequest> findTeamRequestsForUser(User user, Long metaprojectId) {
        Collection<CommTeamRequest> all = findCommunicationsBySendingUser(CommTeamRequest.class, user.getUserId());
        Collection<CommTeamRequest> result = new LinkedList<>();
        for (CommTeamRequest comm : all) {
            // if metaproject id from team equals metaproject id
            // and request is not finished
            if (!comm.getStatus().equals(CommStatus.FINISHED)
                    && comm.getTeam().getMetaProjectId().getMetaprojectId().equals(metaprojectId)) {
                result.add(comm);
            }
        }
        return result;
    }

    private <T extends CommAbstract> Collection<T> findCommunicationsBySendingUser(Class<T> type, Long userId) {
        return commDao.findBySendingUserId(type, userId);
    }

    @Override
    public CommInfoAbstract deleteCommunication(Long id, Long userId) {
        log.info("delete Communication");
        CommInfoAbstract comm = commDao.findById(CommInfoAbstract.class, id);
        if (comm == null) {
            log.error("No comm found with that id {}", id);
            return null;
        }
        // check whether user is target user (is allowed)
        if (!comm.getTargetUser().getUserId().equals(userId)) {
            log.error("The user is not the target user. This user isn't allowed to read this comm.");
            return null;
        }
        comm.readComm();
        commDao.save(comm);
        return comm;
    }

    @Override
    public CommInfoAbstract readCommunication(Long id, Long userId) {
        CommInfoAbstract comm = commDao.findById(CommInfoAbstract.class, id);
        if (comm == null) {
            log.error("No comm found with that id {}", id);
            return null;
        }
        // check whether user is target user (is allowed)
        if (!comm.getTargetUser().getUserId().equals(userId)) {
            log.error("The user is not the target user. This user isn't allowed to read this comm.");
            return null;
        }
        return comm;
    }

    @Override
    public CommAgreeRejectAbstract validateAnswerCommunication(Long id, CommAnswer answer, Long userId) {
        CommAgreeRejectAbstract comm = commDao.findById(CommAgreeRejectAbstract.class, id);
        if (comm == null) {
            log.error("No comm found with that id {}", id);
            return null;
        }
        // check whether user is target user (is allowed)
        if (!comm.getTargetUser().getUserId().equals(userId)) {
            log.error("The user is not the target user. This user isn't allowed to read this comm.");
            return null;
        }
        if (comm.isValidAnswer(answer)) {
            return comm;
        }
        return null;
    }

    @Override
    public CommAgreeRejectAbstract answerCommunication(Long id, CommAnswer answer, Long userId) {
        CommAgreeRejectAbstract comm = commDao.findById(CommAgreeRejectAbstract.class, id);
        if (comm == null) {
            log.error("No comm found with that id {}", id);
            return null;
        }
        // check whether user is target user (is allowed)
        if (!comm.getTargetUser().getUserId().equals(userId)) {
            log.error("The user is not the target user. This user isn't allowed to read this comm.");
            return null;
        }
        comm.setAnswer(answer);
        return comm;
    }

    @Override
    public CommAbstract revokeCommunication(Long id) {
        return commDao.deleteById(id);
    }

    @Override
    public Collection<CommTeamInvite> revokeCommTeamInvite(Team team) {
        Collection<CommTeamInvite> comms = commDao.findCommTeamInviteByTeam(team.getTeamId());
        for (CommTeamInvite comm : comms) {
            revokeCommunication(comm.getId());
        }
        return comms;
    }

    @Override
    public Collection<CommTeamRequest> revokeCommTeamRequest(Team team) {
        Collection<CommTeamRequest> comms = commDao.findCommTeamRequestByTeam(team.getTeamId());
        for (CommTeamRequest comm : comms) {
            revokeCommunication(comm.getId());
        }
        return comms;
    }

    @Override
    public Collection<CommTeamRequest> revokeCommTeamRequest(User user, Metaproject metaproject) {
        Collection<CommTeamRequest> all = findCommunicationsByTargetUser(CommTeamRequest.class, user.getUserId());
        Collection<CommTeamRequest> result = new LinkedList<>();
        // and request is not finished
        for (CommTeamRequest comm : all) {
            // if metaproject id from team equals metaproject id
            // and request is not finished
            if (!comm.getStatus().equals(CommStatus.FINISHED)
                    && comm.getTeam().getMetaProjectId().getMetaprojectId().equals(metaproject.getMetaprojectId())) {
                result.add(comm);
                // delete
                revokeCommunication(comm.getId());
            }
        }
        return result;
    }

    @Override
    public Collection<CommTeamRequest> revokeCommTeamRequest(CommAbstract exceptComm, User user,
                                                             Metaproject metaproject) {
        Collection<CommTeamRequest> all = findCommunicationsByTargetUser(CommTeamRequest.class, user.getUserId());
        Collection<CommTeamRequest> result = new LinkedList<>();
        // and request is not finished
        for (CommTeamRequest comm : all) {
            // if metaproject id from team equals metaproject id
            // and request is not finished and not the same communication
            // request as given one
            if (!comm.getStatus().equals(CommStatus.FINISHED) && !comm.getId().equals(exceptComm.getId())
                    && comm.getTeam().getMetaProjectId().getMetaprojectId().equals(metaproject.getMetaprojectId())) {
                result.add(comm);
                // delete
                revokeCommunication(comm.getId());
            }
        }
        return result;
    }

    @Override
    public Collection<CommAbstract> revokeCommsForMetaproject(Long metaprojectId) {
        Collection<CommAbstract> comms = commDao.findByMetaproject(metaprojectId);
        commDao.deleteAll(comms);
        return comms;
    }

    @Override
    public Collection<CommAbstract> revokeCommsForProject(Long projectId) {
        Collection<CommAbstract> comms = commDao.findByProject(projectId);
        commDao.deleteAll(comms);
        return comms;
    }

    @Override
    public Collection<CommAbstract> revokeCommsForTeam(Long teamId) {
        Collection<CommAbstract> comms = commDao.findByTeam(teamId);
        commDao.deleteAll(comms);
        return comms;
    }

}
