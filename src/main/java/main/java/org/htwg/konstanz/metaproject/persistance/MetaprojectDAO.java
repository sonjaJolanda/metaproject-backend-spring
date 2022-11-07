package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

/**
 * Data access object for {@link Metaproject} objects. This class handles
 * requests to database.
 *
 * @author SiKelle
 */
public interface MetaprojectDAO {

    /**
     * Find a {@link Metaproject} by id.
     */
    Metaproject findById(Long id);

    /**
     * Save a new {@link Metaproject} to database, returned metaproject is
     * handled by entity manager.
     */
    Metaproject save(Metaproject metaproject);

    /**
     * Update a {@link Metaproject} by id. Returns null in case of error or not found.
     */
    Metaproject update(Long metaprojectId, Metaproject transientMetaproject);

    /**
     * Delete a {@link Metaproject} by id. The returned object isn't handled
     * anymore with entitymanager. Returns null, if project isn't found.
     */
    Metaproject delete(Long metaprojectId);

    Collection<Metaproject> findAll();
    Page<Metaproject> findAll(int page, int size, String sortAttribute, boolean isDescending);

    /**
     * Find all {@link Metaproject}s which are visible for non-authorized user with pagination.
     */
    Collection<Metaproject> findAllVisible(int page, int size, String sortAttribute, boolean isDescending);

    /**
     * Find all {@link Metaproject}s which are visible for non-authorized user.
     */
    Collection<Metaproject> findAllVisible();

    Collection<Metaproject> findByRegisterType(String registerType);


    /**
     * Find all Metaprojects which need no preRegistration
     */
    Collection<Metaproject> findAllNonPreRegistration();
}

