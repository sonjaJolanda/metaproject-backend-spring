package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.PrioTeamProject;
import java.util.Collection;

/**
 * Data access object for {@link PrioTeamProject}.
 * 
 * @author SiKelle
 *
 */
public interface PrioTeamProjectDAO {

	PrioTeamProject save(PrioTeamProject prio);

	/**
	 * Save prio with flush afterwards
	 */
	PrioTeamProject saveWithFlush(PrioTeamProject prio);
	
	/**
	 * Update prio
	 */
	PrioTeamProject update(Long id, PrioTeamProject prio);
	
	/**
	 * Find by Id
	 */
	PrioTeamProject findById(Long id);
	
	/**
	 * Find all prio for a project.
	 */
	Collection<PrioTeamProject> findByProject(Long projectId);

	/**
	 * Find all prio for a team.
	 */
	Collection<PrioTeamProject> findByTeam(Long teamId);

	/**
	 * Find all prio for a team and project.
	 */
	Collection<PrioTeamProject> findByTeamAndProject(Long teamId, Long projectId);

	/**
	 * Find all prio for a team and project.
	 */
	Collection<PrioTeamProject> findByMetaAndTeam(Long metaId, Long teamId);

	/**
	 * Find all prio for a metaProject.
	 */
	Collection<PrioTeamProject> findByMeta(Long metaId);

	/**
	 * Delete all prios of a project.
	 */
	Collection<PrioTeamProject> deleteByProject(Long projectId);

	/**
	 * Delete all prios of a team.
	 */
	Collection<PrioTeamProject> deleteByTeam(Long teamId);

	/**
	 * Delete a prio
	 */
	PrioTeamProject delete(Long prioId);
}