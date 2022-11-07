package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.CommAgreeRejectAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.*;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommRead;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.repositories.CommAbstractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;

/**
 * Implementation of data access object for {@link CommAbstract}.
 *
 * @author SiKelle
 */
@Service
public class CommDAOImpl implements CommDAO {

    private static final Logger log = LoggerFactory.getLogger(CommDAOImpl.class);

    @Autowired
    private EntityManager em;

    private final CommAbstractRepository commAbstractRepo;

    public CommDAOImpl(CommAbstractRepository commAbstractRepo) {
        this.commAbstractRepo = commAbstractRepo;
    }

    @Override
    public CommAbstract findById(Long id) {
        return findById(CommAbstract.class, id);
    }

    @Override
    public <T extends CommAbstract> T findById(Class<T> type, Long id) {
        try {
            return em.find(type, id);
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T extends CommAbstract> Collection<T> findByTargetUserId(Class<T> type, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("targetUser"), userId),
                        criteriaBuilder.equal(root.get("status"), CommStatus.UNTOUCHED))));
        return allQuery.getResultList();
    }

    @Override
    public <T extends CommAbstract> Collection<T> findByTargetUserId(Class<T> type, Long userId, int offset,
                                                                     int limit) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("targetUser"), userId))
                        .orderBy(criteriaBuilder.desc(root.get("created"))));
        return allQuery.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Override
    public <T extends CommAbstract> Collection<T> findBySendingUserId(Class<T> type, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("sendingUser"), userId)));
        return allQuery.getResultList();
    }

    @Override
    public <T extends CommAbstract> Collection<T> findUnrespondedByTargetUserId(Class<T> type, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em.createQuery(criteriaQuery.select(root)
                .where(criteriaBuilder.and(criteriaBuilder.equal(root.get("targetUser"), userId),
                        criteriaBuilder.equal(root.get("readStatus"), CommRead.NOT_READ))));
        return allQuery.getResultList();
    }

    @Override
    public <T extends CommAbstract> Collection<T> findUnrespondedBySendingUserId(Class<T> type, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em.createQuery(criteriaQuery.select(root)
                .where(criteriaBuilder.and(criteriaBuilder.equal(root.get("sendingUser"), userId),
                        criteriaBuilder.equal(root.get("status"), CommStatus.ANSWERED))));
        return allQuery.getResultList();
    }

    @Override
    public <T extends CommAgreeRejectAbstract> Collection<T> findUnansweredBySendingUserId(Class<T> type, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em.createQuery(criteriaQuery.select(root)
                .where(criteriaBuilder.and(criteriaBuilder.equal(root.get("sendingUser"), userId),
                        criteriaBuilder.equal(root.get("status"), CommStatus.UNTOUCHED))));
        return allQuery.getResultList();
    }

    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.persistance.CommDAO#save(java.lang.Class, T)
     */
    @Override
    public <T extends CommAbstract> T save(Class<T> type, T comm) {
        return commAbstractRepo.save(comm);
    }

    @Override
    public <T extends CommAbstract> T save(T comm) {
        return commAbstractRepo.save(comm);
    }



    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.persistance.CommDAO#deleteById(java.lang.Long)
     */
    @Override
    public CommAbstract deleteById(Long id) {
        return deleteById(CommAbstract.class, id);
    }

    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.persistance.CommDAO#deleteById(java.lang.Class, java.lang.Long)
     */
    @Override
    public <T extends CommAbstract> T deleteById(Class<T> type, Long id) {
        T comm = findById(type, id);
        if (comm == null)
            return null;

        commAbstractRepo.delete(comm);
        return comm;
    }

    @Override
    public Collection<CommAbstract> deleteAll(Collection<CommAbstract> comms) {
        commAbstractRepo.deleteAll(comms);
        return comms;
    }

    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.persistance.CommDAO#findCommTeamInviteByTeam(java.lang.Long)
     */
    @Override
    public Collection<CommTeamInvite> findCommTeamInviteByTeam(Long teamId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CommTeamInvite> criteriaQuery = criteriaBuilder.createQuery(CommTeamInvite.class);
        Root<CommTeamInvite> root = criteriaQuery.from(CommTeamInvite.class);
        TypedQuery<CommTeamInvite> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("team"), teamId)));
        return allQuery.getResultList();
    }

    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.persistance.CommDAO#findCommTeamRequestByTeam(java.lang.Long)
     */
    @Override
    public Collection<CommTeamRequest> findCommTeamRequestByTeam(Long teamId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CommTeamRequest> criteriaQuery = criteriaBuilder.createQuery(CommTeamRequest.class);
        Root<CommTeamRequest> root = criteriaQuery.from(CommTeamRequest.class);
        TypedQuery<CommTeamRequest> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("team"), teamId)));
        return allQuery.getResultList();
    }

    @Override
    public Collection<CommAbstract> findByMetaproject(Long metaprojectId) {
        String fieldName = "metaproject";
        // find all communication types, which have a field "metaproject" and
        // are connected
        // with a metaproject with a foreign key
        Collection<CommAbstract> comms = findByFieldAndValue(CommAssignProjectToTeam.class,
                metaprojectId, fieldName);
        comms.addAll(findByFieldAndValue(CommMetaprojectAddMember.class, metaprojectId, fieldName));
        comms.addAll(findByFieldAndValue(CommMetaprojectLeaderAppointment.class, metaprojectId, fieldName));
        comms.addAll(findByFieldAndValue(CommMetaprojectLeaderChange.class, metaprojectId, fieldName));
        comms.addAll(findByFieldAndValue(CommProjectDelete.class, metaprojectId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamDelete.class, metaprojectId, fieldName));
        return comms;
    }

    @Override
    public Collection<CommAbstract> findByProject(Long projectId) {
        String fieldName = "project";
        // find all communication types, which have a field "project" and
        // are connected
        // with a project with a foreign key
        Collection<CommAbstract> comms = findByFieldAndValue(CommProjectLeaderChange.class, projectId, fieldName);
        comms.addAll(findByFieldAndValue(CommProjectLeaderAppointment.class, projectId, fieldName));
        return comms;
    }

    @Override
    public Collection<CommAbstract> findByTeam(Long teamId) {
        String fieldName = "team";
        // find all communication types, which have a field "team" and
        // are connected
        // with a team with a foreign key
        Collection<CommAbstract> comms = findByFieldAndValue(CommTeamApplicationReject.class, teamId, fieldName);
        comms.addAll(findByFieldAndValue(CommTeamInvitationReject.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamInvitationWithdraw.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamInvitationAccept.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamApplicationReject.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamApplicationWithdraw.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamApplicationAccept.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamInvite.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamLeaderChange.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamLeaderAppointment.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamRemoveMember.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamRequest.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommTeamSendPrio.class, teamId, fieldName));
        comms.addAll(findByFieldAndValue(CommMetaprojectleaderAddTeamMember.class, teamId, fieldName));
        return comms;
    }

    private <T extends CommAbstract> Collection<CommAbstract> findByFieldAndValue(Class<T> type, Object value,
                                                                                  String fieldName) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        TypedQuery<T> allQuery = em
                .createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(fieldName), value)));
        return (Collection<CommAbstract>) allQuery.getResultList();
    }

    @Override
    public Collection<CommTeamRequest> findCommTeamRequestByTeamAndUser(Long teamId, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CommTeamRequest> criteriaQuery = criteriaBuilder.createQuery(CommTeamRequest.class);
        Root<CommTeamRequest> root = criteriaQuery.from(CommTeamRequest.class);
        TypedQuery<CommTeamRequest> allQuery = em.createQuery(
                criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("team"), teamId),
                        criteriaBuilder.equal(root.get("sendingUser"), userId),
                        criteriaBuilder.not(criteriaBuilder.equal(root.get("status"), CommStatus.FINISHED)))));
        return allQuery.getResultList();
    }

    @Override
    public Collection<CommTeamInvite> findCommTeamInviteByTeamAndUser(Long teamId, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CommTeamInvite> criteriaQuery = criteriaBuilder.createQuery(CommTeamInvite.class);
        Root<CommTeamInvite> root = criteriaQuery.from(CommTeamInvite.class);
        TypedQuery<CommTeamInvite> allQuery = em.createQuery(
                criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("team"), teamId),
                        criteriaBuilder.equal(root.get("targetUser"), userId),
                        criteriaBuilder.not(criteriaBuilder.equal(root.get("status"), CommStatus.FINISHED)))));
        return allQuery.getResultList();
    }

}
