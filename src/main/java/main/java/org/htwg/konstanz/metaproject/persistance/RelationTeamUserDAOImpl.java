package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import main.java.org.htwg.konstanz.metaproject.repositories.RelationTeamUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Data access object implementation for the relation team user.
 *
 * @author SiKelle
 */
@Service
public class RelationTeamUserDAOImpl implements RelationTeamUserDAO {

    private static final Logger log = LoggerFactory.getLogger(RelationTeamUserDAOImpl.class);

    private final RelationTeamUserRepository relationTeamUserRepo;

    private final TeamDAO teamDAO;

    public RelationTeamUserDAOImpl(RelationTeamUserRepository relationTeamUserRepo, TeamDAO teamDAO) {
        this.relationTeamUserRepo = relationTeamUserRepo;
        this.teamDAO = teamDAO;
    }

    @Override
    public RelationTeamUser save(RelationTeamUser transRel) {
        return relationTeamUserRepo.save(transRel);
    }

    @Override
    public RelationTeamUser updateStatus(Long id, TeamMemberStatus status) {
        RelationTeamUser relation = findById(id);
        if (relation == null)
            return null;

        relation.setTeamMemberStatus(status);
        return save(relation);
    }

    @Override
    public void deleteById(Long id) {
        RelationTeamUser relation = findById(id);
        relationTeamUserRepo.delete(relation);
    }

    @Override
    public RelationTeamUser findByUserAndTeam(User user, Team team) {
        List<RelationTeamUser> result = relationTeamUserRepo.findByTeamIdAndUserId(team, user);
        if (result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Override
    public Collection<RelationTeamUser> findByTeam(Team team) {
        return relationTeamUserRepo.findByTeamId(team);
    }

    @Override
    public Collection<RelationTeamUser> deleteByTeam(Team team) {
        Collection<RelationTeamUser> relations = findByTeam(team);
        relationTeamUserRepo.deleteAll(relations);
        return relations;
    }

    @Override
    public Collection<RelationTeamUser> findByTeamAndMemberStatus(Team team, TeamMemberStatus teamMemberStatus) {
        return relationTeamUserRepo.findByTeamIdAndTeamMemberStatus(team, teamMemberStatus);
    }

    @Override
    public Collection<RelationTeamUser> findByTeamAndStatus(Team team) {
        Collection<RelationTeamUser> relations = relationTeamUserRepo.findByTeamIdAndTeamMemberStatus(team, TeamMemberStatus.INVITED);
        relations.addAll(relationTeamUserRepo.findByTeamIdAndTeamMemberStatus(team, TeamMemberStatus.TEAMMEMBER));
        return relations;
    }

    @Override
    public Collection<RelationTeamUser> findTeammemberByTeam(Team team) {
        return relationTeamUserRepo.findByTeamIdAndTeamMemberStatus(team, TeamMemberStatus.TEAMMEMBER);
    }

    @Override
    public void deleteAll(Collection<RelationTeamUser> relations) {
        relationTeamUserRepo.deleteAll(relations);
    }

    @Override
    public RelationTeamUser findById(Long id) {
        return relationTeamUserRepo.findById(id).orElse(null);
    }

    @Override
    public Collection<RelationTeamUser> deleteByUserAndMeta(User user, Metaproject metaproject) {
        Collection<RelationTeamUser> relations = findByUserAndMeta(user, metaproject);
        relationTeamUserRepo.deleteAll(relations);
        return relations;
    }

    @Override
    public Collection<RelationTeamUser> findByUserAndMeta(User user, Metaproject metaproject) {
        Collection<RelationTeamUser> relationsUser = findByUser(user);
        Collection<RelationTeamUser> result = new LinkedList<>();
        for (RelationTeamUser relation : relationsUser) {
            //log.info("RelationReamUser {}", relation.getRelationTeamUserId());
            if (relation.getTeamId().getMetaProjectId().getMetaprojectId().equals(metaproject.getMetaprojectId())) {
                log.info("Add {}", relation.getRelationTeamUserId());
                result.add(relation);
            }
        }
        return result;
    }

    @Override
    public Collection<RelationTeamUser> findByProject(Project project) {
        Team team = teamDAO.findByProjectId(project.getProjectId());
        return findByTeam(team);
    }

    @Override
    public Collection<RelationTeamUser> findByUser(User user) {
        return relationTeamUserRepo.findByUserId(user);
    }

    @Override
    public long getNumberOfRelations() {
        return relationTeamUserRepo.findAll().size();
    }

    @Override
    public long getNumberOfRelationsForMetaproject(Metaproject metaproject) {
        Collection<Team> teams = teamDAO.findByMetaprojectId(metaproject.getMetaprojectId());
        long count = 0;
        for (Team team : teams) {
            count += findByTeam(team).size();
        }
        return count;
    }

    @Override
    public List<RelationTeamUser> findAll() {
        return relationTeamUserRepo.findAll();
    }
}
