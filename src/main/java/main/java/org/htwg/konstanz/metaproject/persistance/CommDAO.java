package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommAgreeRejectAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamInvite;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.CommTeamRequest;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;

import java.util.Collection;

/**
 * Data access object for {@link CommAbstract} and sub classes.
 *
 * @author SiKelle
 */
public interface CommDAO {

    /**
     * Find a communication by id. Returns the abstract super class type for
     * this communication. Returns null, if no entity was found.
     */
    CommAbstract findById(Long id);

    /**
     * A generic method to find communications by id of all communication types.
     * Returns null if no entity was found.
     */
    <T extends CommAbstract> T findById(Class<T> type, Long id);

    /**
     * A generic method to find all communications with target user by id of
     * communication type. Use super class {@link CommAbstract} to find every
     * type of communication.
     */
    <T extends CommAbstract> Collection<T> findByTargetUserId(Class<T> type, Long userId);

    /**
     * A generic method to find all communications with target user by id of
     * communication type. Use super class {@link CommAbstract} to find every
     * type of communication. This method can paginate the result. Order of this
     * collection is always by created date field of these communications. The
     * newest objects are first.
     */
    <T extends CommAbstract> Collection<T> findByTargetUserId(Class<T> type, Long userId, int offset, int limit);

    /**
     * A generic method to find all unresponded (is {@link CommStatus#UNTOUCHED}
     * , the status {@link CommStatus#ANSWERED} is a responded/finished
     * communication for the target user) communications with target user by id
     * of communication type. Use super class {@link CommAbstract} to find every
     * type of communication.
     */
    <T extends CommAbstract> Collection<T> findUnrespondedByTargetUserId(Class<T> type, Long userId);

    /**
     * A generic method to find all communications with sending user by id of
     * communication type. Use super class {@link CommAbstract} to find every
     * type of communication.
     */
    <T extends CommAbstract> Collection<T> findBySendingUserId(Class<T> type, Long userId);

    /**
     * A generic method to find all unresponded (not {@link CommStatus#FINISHED}
     * and not {@link CommStatus#UNTOUCHED}, the status
     * {@link CommStatus#ANSWERED} is an unresponded communication for the
     * sending user) communications with sending user by id of communication
     * type. Use super class {@link CommAbstract} to find every type of
     * communication.
     */
    <T extends CommAbstract> Collection<T> findUnrespondedBySendingUserId(Class<T> type, Long userId);

    /**
     * A generic method to find all unanswered ({@link CommStatus#UNTOUCHED})
     * communications with sending user by id of communication type. Use super
     * class {@link CommAgreeRejectAbstract} to find every type of answerable
     * communications.
     */
    <T extends CommAgreeRejectAbstract> Collection<T> findUnansweredBySendingUserId(Class<T> type, Long userId);

    /**
     * A generic method to save a communication in database. The returned object
     * is handled by entity manager.
     */
    <T extends CommAbstract> T save(Class<T> type, T comm);

    <T extends CommAbstract> T save(T comm);

    /**
     * Delete a communication by id and return abstract super class for this type.
     */
    CommAbstract deleteById(Long id);

    /**
     * A generic method to delete a communication by type and id.
     */
    <T extends CommAbstract> T deleteById(Class<T> type, Long id);

    /**
     * Delete all communications and return abstract super class collection. The
     * given comms must be handled by entitymanager.
     */
    Collection<CommAbstract> deleteAll(Collection<CommAbstract> comms);

    /**
     * Find all communication of all types, which are connected with a
     * {@link Metaproject}. Returns a collection of the abstract superclass.
     */
    Collection<CommAbstract> findByMetaproject(Long metaprojectId);

    /**
     * Find all communication of all types, which are connected with a
     * {@link Project}. Returns a collection of the abstract superclass.
     */
    Collection<CommAbstract> findByProject(Long projectId);

    /**
     * Find all communication of all types, which are connected with a
     * {@link Team}. Returns a collection of the abstract superclass.
     */
    Collection<CommAbstract> findByTeam(Long teamId);

    /**
     * Find communications of type {@link CommType#TEAM_INVITE} with a given
     * {@link Team} id.
     */
    Collection<CommTeamInvite> findCommTeamInviteByTeam(Long teamId);

    /**
     * Find communications of type {@link CommType#TEAM_REQUEST} with a given
     * {@link Team} id.
     */
    Collection<CommTeamRequest> findCommTeamRequestByTeam(Long teamId);

    /**
     * Find communications of type {@link CommType#TEAM_REQUEST} with a given
     * {@link Team} id and user id. This should be only one. There are only
     * actual comms found (not {@link CommStatus#FINISHED}).
     */
    Collection<CommTeamRequest> findCommTeamRequestByTeamAndUser(Long teamId, Long userId);

    /**
     * Find communications of type {@link CommType#TEAM_INVITE} with a given
     * {@link Team} id and user id. This should be only one. There are only
     * actual comms found (not {@link CommStatus#FINISHED}).
     */
    Collection<CommTeamInvite> findCommTeamInviteByTeamAndUser(Long teamId, Long userId);

}