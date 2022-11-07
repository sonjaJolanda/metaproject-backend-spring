package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Project;

import java.util.Collection;
import java.util.List;

/**
 * Data access object for {@link Project} objects.
 *
 * @author SiKelle
 */
public interface ProjectDAO {

    /**
     * Find a project by id. The object is handled by entity manager.
     */
    Project findById(Long id);

    /**
     * Find all projects of a metaproject by id.
     */
    Collection<Project> findByMetaproject(Long metaprojectId);


    Project findByMetaprojectAndTitle(Long metaprojectId, String title);

    /**
     * Save a new project.
     */
    Project save(Project project);

    /**
     * Update an existing project by id.
     */
    Project update(Long id, Project project);

    /**
     * Delete a project by id. Returns the removed object.
     */
    Project delete(Long id);

    List<Project> findAll();


}