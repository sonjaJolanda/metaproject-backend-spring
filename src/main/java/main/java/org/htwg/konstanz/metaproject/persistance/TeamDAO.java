package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Team;

import java.util.Collection;
import java.util.List;

/**
 * Data access object for team resource.
 *
 * @author SiKelle
 */
public interface TeamDAO {

    Team findById(Long id);

    /**
     * Find all teams in a metaproject.
     */
    Collection<Team> findByMetaprojectId(Long metaprojectId);

    /**
     * Find team by assigned projectId
     */
    Team findByProjectId(Long projectId);

    /**
     * Update a team by id. The returned updated team is a copy of the parameter
     * and is handled with entity manager.
     */
    Team update(Team transientTeam, long id);

    /**
     * Save a new team.
     */
    Team save(Team team);

    /**
     * Delete a {@link Team} by id.
     */
    Team delete(Long teamId);

    /**
     * Delete all projects in a metaproject.
     */
    Collection<Team> deleteByMetaproject(Long metaprojectId);

    /**
     * @return number of teams saved in the db
     */
    long getNumberOfTeams();

    List<Team> findAll();

}