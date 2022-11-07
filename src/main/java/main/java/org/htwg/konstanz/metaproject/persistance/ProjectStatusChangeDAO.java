package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.ProjectStatusChange;

import java.util.Collection;

/**
 * Data access object for {@link ProjectStatusChange} objects.
 *
 * @author Sennur Kaya, Elisa-Lauren Bohnet
 */
public interface ProjectStatusChangeDAO {

    /**
     * Find a statuschange by id. The object is handled by entity manager.
     */
    ProjectStatusChange findById(Long id);

    /**
     * Find all status changes of a project by id.
     */
    Collection<ProjectStatusChange> findByProject(Long projectId);

    ProjectStatusChange save(ProjectStatusChange statusChange);

    /**
     * Update an existing statuschange by id.
     */
    ProjectStatusChange update(Long id, ProjectStatusChange statusChange);

    /**
     * Delete an existing statuschange by id.
     */
    ProjectStatusChange delete(Long id);

}
