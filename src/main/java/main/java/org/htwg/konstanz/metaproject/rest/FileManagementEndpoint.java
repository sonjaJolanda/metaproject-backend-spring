package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.FileUpload;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.enums.FileUploadType;
import main.java.org.htwg.konstanz.metaproject.persistance.FileUploadDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.MetaprojectDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.ProjectDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * REST enpoint for file upload/download/management.
 *
 * @author SiKelle, PaDrautz
 */

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/filemanagement")
public class FileManagementEndpoint {

    public static final Logger log = LoggerFactory.getLogger(FileManagementEndpoint.class);

    private final RightService rightService;

    private final FileUploadDAO fileUploadDao;

    private final MetaprojectDAO metaprojectDao;

    private final ProjectDAO projectDao;

    public FileManagementEndpoint(RightService rightService, FileUploadDAO fileUploadDao, MetaprojectDAO metaprojectDao, ProjectDAO projectDao) {
        this.rightService = rightService;
        this.fileUploadDao = fileUploadDao;
        this.metaprojectDao = metaprojectDao;
        this.projectDao = projectDao;
    }

    /**
     * Upload a new file and create a new entry to file upload database table.
     */
    @PostMapping(value = "fileUpload/{projectId}/{fileName}/{type}")
    public ResponseEntity<Object> recieveFileUpload(@PathVariable Long projectId, @PathVariable String fileName, @PathVariable FileUploadType type,
                                                    MultipartFile file, @RequestHeader String token) throws IOException {
        log.info("Request <-- POST /filemanagement/fileUpload/{}/{}/{}", projectId, fileName, type);

        // Parse multipart form data for file content.
        InputStream fileBinaries = file.getInputStream();
        if (fileBinaries == null) {
            log.error("fileBinariesAreNull");
            return ResponseEntity.badRequest().build(); // Return 400 in case of parse request error or if no file was found
        }

        // Create database entry for file meta data.
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(fileName);
        // Set a dummy file path cause of not null constraint
        fileUpload.setFilePath("");
        fileUpload.setProjectId(projectId);
        fileUpload.setType(type);
        // persist entry in database and write file to disk
        FileUpload persistedFileUpload = fileUploadDao.save(fileUpload, fileBinaries);

        if (persistedFileUpload == null)
            return ResponseEntity.badRequest().build();
        else
            return ResponseEntity.status(HttpStatus.CREATED).body(persistedFileUpload);
    }

    /**
     * List information about all files in a project or metaproject, specified
     * by {@link FileUploadType#METAPROJECT_UPLOAD} and
     * {@link FileUploadType#PROJECT_UPLOAD}.
     */
    @GetMapping(value = "/listForProject/{projectId}/{type}")
    public ResponseEntity<Object> listAllFiles(@PathVariable long projectId, @PathVariable FileUploadType type, @RequestHeader String token) {
        log.info("Request <-- GET /filemanagement/listForProject/{}", projectId);

        boolean hasRights = false;
        if (FileUploadType.METAPROJECT_UPLOAD.equals(type)) {
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        } else if (FileUploadType.PROJECT_UPLOAD.equals(type)) {
            Project project = projectDao.findById(projectId);
            if (project == null)
                return ResponseEntity.badRequest().build();

            Long metaprojectId = project.getMetaproject().getMetaprojectId();
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS)
                    .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaprojectId).validate();
        }

        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // find all uploads according to type and project
        Collection<FileUpload> uploads = null;
        if (FileUploadType.METAPROJECT_UPLOAD.equals(type)) {
            uploads = fileUploadDao.findAllByMetaproject(projectId);
        } else if (FileUploadType.PROJECT_UPLOAD.equals(type)) {
            uploads = fileUploadDao.findAllByProject(projectId);
        }

        if (uploads == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(uploads);
    }

    /**
     * Download a file by id. The second path variable "fileName" is used to
     * create the download file with correct file name in browser.
     */
    @GetMapping(value = "/download/{id}/{fileName}")
    public void downloadFile(@PathVariable long id, @PathVariable String fileName, @RequestParam String token, HttpServletResponse response) throws IOException {
        log.info("Request <-- GET /filemanagement/download/{}/{}?token=?", id, fileName);

        FileUpload fileInfo = fileUploadDao.findById(id);
        if (fileInfo == null)
            response.setStatus(HttpStatus.NOT_FOUND.value());

        boolean hasRights = false;
        // check for upload type
        if (FileUploadType.METAPROJECT_UPLOAD.equals(fileInfo.getType())) {
            // upload is a metaproject upload
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        } else if (FileUploadType.PROJECT_UPLOAD.equals(fileInfo.getType())) {
            // upload is a project upload
            Long metaprojectId = projectDao.findById(fileInfo.getProjectId()).getMetaproject().getMetaprojectId();
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS)
                    .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaprojectId).validate();
        }
        // Routine to check if token is valid & if user has the permissions
        if (!hasRights) {
            // User has no rights
            log.info("User has no permissions to do that operation.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        // get file from disk
        byte[] binaries = fileUploadDao.getFileBinaries(fileInfo);

        // Check whether file read was successfull
        if (binaries != null) {
            // Return this byte array to create a download
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(binaries);
            response.setStatus(HttpStatus.OK.value());
        }
        // Return request fail in case of error
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Delete a single file from project or metaproject
     */
    @DeleteMapping("fileUpload/{uploadId}")
    public ResponseEntity<Object> deleteFileUpload(@PathVariable Long uploadId, @RequestHeader String token) {
        log.info("Request <-- DELETE {}", uploadId);

        FileUpload fileUpload = fileUploadDao.findById(uploadId);
        if (fileUpload == null)
            return ResponseEntity.notFound().build();

        boolean hasRights = false;
        // check for upload type
        if (FileUploadType.METAPROJECT_UPLOAD.equals(fileUpload.getType())) {
            // upload is a metaproject upload
            Long metaprojectId = metaprojectDao.findById(fileUpload.getProjectId()).getMetaprojectId();
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForAppRight(Rights.METAPROJECT_EDIT)
                    .checkForMetaprojectRight(Rights.METAPROJECT_EDIT, metaprojectId).validate();
        } else if (FileUploadType.PROJECT_UPLOAD.equals(fileUpload.getType())) {
            // upload is a project upload
            Project project = projectDao.findById(fileUpload.getProjectId());
            Long projectId = project.getProjectId();
            Long metaprojectId = project.getMetaproject().getMetaprojectId();
            hasRights = rightService.newRightHandler(token).checkForSuperUser()
                    .checkForProjectRight(Rights.METAPROJECT_PROJECT_EDIT, projectId)
                    .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_EDIT, metaprojectId).validate();
        }
        // Routine to check if token is valid & if user has the permissions
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        fileUploadDao.delete(uploadId);
        return ResponseEntity.ok().build();
    }

}
