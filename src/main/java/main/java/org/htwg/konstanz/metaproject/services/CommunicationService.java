package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommAgreeRejectAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;

import java.util.Collection;

/**
 * This service wraps the common actions on communications. This service can not
 * be used to create new communications.
 *
 * @author SiKelle
 */
public interface CommunicationService {

    /**
     * Find all communications for a target {@link User}.
     */
    Collection<CommAbstract> findCommunicationsByTargetUser(Long userId, int offset, int limit);

    Collection<CommAbstract> findCommunicationsByTargetUser(Long userId);

    /**
     * Find all unresponded communications for a target {@link User}. Status is
     * not {@link CommStatus#FINISHED}.
     */
    Collection<CommAbstract> findUnrespondedCommunicationsByTargetUser(Long userId);

    /**
     * Find all unresponded communications for a sending {@link User}. Status is
     * is {@link CommStatus#UNTOUCHED}. These are only communications of type {@link CommAgreeRejectAbstract}.
     */
    Collection<CommAgreeRejectAbstract> findUnrespondedCommunicationsBySendingUser(Long userId);

    /**
     * Alle comms für ein User in Metaproject wo er eingeladen wurde.
     */
    Collection<CommTeamInvite> findTeamInvitesForUser(User user, Long metaprojectId);

    /**
     * Alle comms für ein User in Metaproject wo er sich beworben hat.
     */
    Collection<CommTeamRequest> findTeamRequestsForUser(User user, Long metaprojectId);

    /**
     * Read a communication of type {@link CommInfoAbstract} and id. The second
     * parameter is the user who reads this communication. Returns null, if the
     * action failed. Deletes the communication.
     * @param id
     * @param userId
     * @return
     */
    CommInfoAbstract deleteCommunication(Long id, Long userId);

    /**
     * Read a communication of type {@link CommInfoAbstract} and id. The second
     * parameter is the user who reads this communication. Returns null, if the
     * action failed.
     */
    CommInfoAbstract readCommunication(Long id, Long userId);

    /**
     * Check whether the answer is a valid answer for this communication of type
     * {@link CommAgreeRejectAbstract} and id. Returns null, if answer was
     * illegal. The second parameter is the user who reads this communication.
     * Returns the communication object if action is allowed.
     */
    CommAgreeRejectAbstract validateAnswerCommunication(Long id, CommAnswer answer, Long userId);

    /**
     * Answer a communication of type {@link CommAgreeRejectAbstract} and id.
     * Returns null, if answer was illegal. The second parameter is the user who
     * reads this communication. Returns null, if the action failed.
     */
    CommAgreeRejectAbstract answerCommunication(Long id, CommAnswer answer, Long userId);

    /**
     * Revoke a communication request and delete it. The email is revoked too,
     * if it isn't send yet.
     */
    CommAbstract revokeCommunication(Long id);

    /**
     * Revoke all communication requests of type {@link CommType#TEAM_INVITE}
     * for a team by id.
     */
    Collection<CommTeamInvite> revokeCommTeamInvite(Team team);

    /**
     * Revoke all communication requests of type {@link CommType#TEAM_REQUEST}
     * by team id.
     */
    Collection<CommTeamRequest> revokeCommTeamRequest(Team team);

    /**
     * Revoke all communication requests of type {@link CommType#TEAM_REQUEST}
     * for a {@link User} in a specific {@link Metaproject} by id. Revokes only
     * not {@link CommStatus#FINISHED} communications.
     */
    Collection<CommTeamRequest> revokeCommTeamRequest(User user, Metaproject metaproject);

    /**
     * Revoke all communication requests of type {@link CommType#TEAM_REQUEST}
     * for a {@link User} in a specific {@link Metaproject} by id <b>except the
     * given communication</b>. Revokes only not {@link CommStatus#FINISHED}
     * communications.
     */
    Collection<CommTeamRequest> revokeCommTeamRequest(CommAbstract comm, User user, Metaproject metaproject);

    /**
     * Revoke all communication for every type for a {@link Metaproject}. This
     * method should be called when a metaproject is deleted.
     */
    Collection<CommAbstract> revokeCommsForMetaproject(Long metaprojectId);

    /**
     * Revoke all communication for every type for a {@link Project}. This
     * method should be called when a project is deleted.
     */
    Collection<CommAbstract> revokeCommsForProject(Long projectId);

    /**
     * Revoke all communication for every type for a {@link Team}. This method
     * should be called when a team is deleted.
     */
    Collection<CommAbstract> revokeCommsForTeam(Long teamId);

}
