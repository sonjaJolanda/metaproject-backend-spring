package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.FileUpload;
import main.java.org.htwg.konstanz.metaproject.enums.FileUploadType;
import main.java.org.htwg.konstanz.metaproject.repositories.FileUploadRepository;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.*;
import java.util.Collection;

/**
 * Implementation of file upload data access object.
 *
 * @author SiKelle
 */
@Service
public class FileUploadDAOImpl implements FileUploadDAO {

    private static final Logger log = LoggerFactory.getLogger(FileUploadDAOImpl.class);

    private final FileUploadRepository fileUploadRepo;

    public FileUploadDAOImpl(FileUploadRepository fileUploadRepo) {
        this.fileUploadRepo = fileUploadRepo;
    }

    @Override
    public FileUpload findById(Long uploadId) {
        return fileUploadRepo.findById(uploadId).orElse(null);
    }

    @Override
    public FileUpload delete(Long uploadId) {
        FileUpload upload = findById(uploadId);
        if (upload == null)
            return null;

        fileUploadRepo.delete(upload);
        removeFileOnDisk(upload);
        return upload;
    }

    @Override
    public Collection<FileUpload> findAllByProject(Long projectId) {
        return fileUploadRepo.findAllByProject(projectId, FileUploadType.PROJECT_UPLOAD);
    }

    @Override
    public Collection<FileUpload> deleteAllByProject(Long projectId) {
        Collection<FileUpload> fileUploads = findAllByProject(projectId);
        for (FileUpload upload : fileUploads) {
            fileUploadRepo.delete(upload);
            removeFileOnDisk(upload);
        }
        return fileUploads;
    }

    @Override
    public Collection<FileUpload> findAllByMetaproject(Long metaprojectId) {
        return fileUploadRepo.findAllByProject(metaprojectId, FileUploadType.METAPROJECT_UPLOAD);
    }

    @Override
    public Collection<FileUpload> deleteAllByMetaproject(Long metaprojectId) {
        Collection<FileUpload> fileUploads = findAllByMetaproject(metaprojectId);
        for (FileUpload upload : fileUploads) {
            fileUploadRepo.delete(upload);
            removeFileOnDisk(upload);
        }
        return fileUploads;
    }

    private boolean removeFileOnDisk(FileUpload fileUpload) {
        File file = new File(fileUpload.getFilePath());
        return file.delete();
    }

    @Override
    public FileUpload save(FileUpload newFileUpload, InputStream inputStream) {

        newFileUpload = fileUploadRepo.save(newFileUpload);
        fileUploadRepo.flush(); // flush, to get generated id

        // Create file location path
        // Auto generate filename with unique key to prevent duplicated filenames
        String uploadedFileLocation = Constants.FILE_LOCATION_PATH + newFileUpload.getUploadId();

        // Create file object with path.
        File objFile = new File(uploadedFileLocation);
        if (objFile.exists())
            log.warn("File {} already exists, will be overwritten!!", newFileUpload.getUploadId());

        // Save file to target location
        if (!writeFileToTarget(inputStream, objFile)) {
            log.error("couldNotWriteToFile");
            // Return internal server error in case of write exception and don't
            // save database entry for file upload remove meta data entry in database
            fileUploadRepo.delete(newFileUpload);
            log.error("Entry in database is removed.");
            return null;
        }

        newFileUpload.setFilePath(uploadedFileLocation); // Save url to file on server
        return fileUploadRepo.save(newFileUpload);
    }

    /**
     * Saves a file as input stream to a target file location, pointed in a file
     * object.
     * <p>
     * Returns false in case of error and true in case of success
     */
    private boolean writeFileToTarget(InputStream fileBinaries, File objFile) {
        // Check parameters for null
        if (fileBinaries == null || objFile == null) {
            return false;
        }
        try {
            // Read in file and write bytes down to target location.
            int read;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream(objFile);
            while ((read = fileBinaries.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            // Return false in case of error.
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public byte[] getFileBinaries(FileUpload fileInfo) {
        // Get file by meta file path
        File file = new File(fileInfo.getFilePath());
        // Read in file from target and get byte array
        return readFileFromTarget(file);
    }

    /**
     * Read in a file from target location and return the file as byte array.
     * <p>
     * Returns null in case of error.
     */
    private byte[] readFileFromTarget(File file) {
        try {
            // Read in file as byte array
            return IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
