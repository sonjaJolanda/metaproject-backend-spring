package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;

import java.util.Collection;
import java.util.List;

/**
 * Data access object for resource relation team user.
 *
 * @author SiKelle
 */
public interface RelationTeamUserDAO {

    /**
     * Save a new relation in database. The new created and handled entity is
     * returned as copy of inserted parameter, updated with generated id.
     */
    RelationTeamUser save(RelationTeamUser transRel);

    /**
     * Find a relation by user and team.
     */
    RelationTeamUser findByUserAndTeam(User user, Team team);

    /**
     * Find all relations by user from every metaproject
     */
    Collection<RelationTeamUser> findByUser(User user);

    /**
     * Find a relation by user and status.
     */
    Collection<RelationTeamUser> findByTeamAndStatus(Team team);

    /**
     * Find all relation with status TEAMMEMBER for one team.
     */
    Collection<RelationTeamUser> findTeammemberByTeam(Team team);

    /**
     * Find all relations by team.
     */
    Collection<RelationTeamUser> findByTeam(Team team);

    /**
     * Find all relations by project.
     */
    Collection<RelationTeamUser> findByProject(Project project);

    /**
     * Find all relations by user.
     */
    Collection<RelationTeamUser> findByUserAndMeta(User user, Metaproject metaproject);

    /**
     * Delete all relations of one team.
     */
    Collection<RelationTeamUser> deleteByTeam(Team team);

    /**
     * Delete all relations of one user.
     */
    Collection<RelationTeamUser> deleteByUserAndMeta(User user, Metaproject metaproject);

    void deleteAll(Collection<RelationTeamUser> relations);

    /**
     * Update relation
     */
    RelationTeamUser updateStatus(Long id, TeamMemberStatus status);

    /**
     * Find relation by ID
     */
    RelationTeamUser findById(Long id);

    void deleteById(Long id);

    long getNumberOfRelations();

    long getNumberOfRelationsForMetaproject(Metaproject metaproject);

    List<RelationTeamUser> findAll();

    Collection<RelationTeamUser> findByTeamAndMemberStatus(Team team, TeamMemberStatus teamMemberStatus);
}