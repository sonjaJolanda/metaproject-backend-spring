package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.FileUpload;

import java.io.InputStream;
import java.util.Collection;

/**
 * Data access object for {@link FileUpload}.
 *
 * @author SiKelle
 */
public interface FileUploadDAO {

    FileUpload findById(Long uploadId);

    /**
     * Get the file binaries from disk, by meta info about file upload.
     */
    byte[] getFileBinaries(FileUpload fileInfo);

    /**
     * Persist a new file upload and save file to file system.
     */
    FileUpload save(FileUpload newFileUpload, InputStream inputStream);

    /**
     * Delete a file upload by id. The file is removed from disk.
     */
    FileUpload delete(Long uploadId);

    /**
     * Find all file uploads of a project by id.
     */
    Collection<FileUpload> findAllByProject(Long projectId);

    /**
     * Delete all file uploads from a project by id.
     */
    Collection<FileUpload> deleteAllByProject(Long projectId);

    /**
     * Find all file uploads of a metaproject by id.
     */
    Collection<FileUpload> findAllByMetaproject(Long metaprojectId);

    /**
     * Delete all file uploads from a metaproject by id.
     */
    Collection<FileUpload> deleteAllByMetaproject(Long metaprojectId);

}