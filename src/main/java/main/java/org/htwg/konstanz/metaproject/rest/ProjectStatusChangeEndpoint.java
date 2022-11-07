package main.java.org.htwg.konstanz.metaproject.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectStatusChange;
import main.java.org.htwg.konstanz.metaproject.persistance.ProjectStatusChangeDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Sennur Kaya, Elisa-Lauren Bohnet
 * @version 1.2
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class ProjectStatusChangeEndpoint {
    private final static Logger log = LoggerFactory.getLogger(ProjectStatusChangeEndpoint.class);

    private final RightService rightService;

    private final ProjectStatusChangeDAO projectStatusChangeDao;

    public ProjectStatusChangeEndpoint(RightService rightService, ProjectStatusChangeDAO projectStatusChangeDao) {
        this.rightService = rightService;
        this.projectStatusChangeDao = projectStatusChangeDao;
    }

    @PostMapping(value = "statuschange/{metaprojectId}/project/{projectId}")
    public ResponseEntity createProjectStatusChange(@PathVariable Long metaprojectId, @PathVariable Long projectId,
                                                    @RequestBody ProjectStatusChange statusChange, @RequestHeader String token) throws IOException {
        log.info("Request <-- POST metaproject/{}/project/{}", metaprojectId, projectId);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_PROJECT_CREATE)
                .checkForProjectRight(Rights.METAPROJECT_PROJECT_CREATE, projectId).validate();

        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        ProjectStatusChange newStatusChange = projectStatusChangeDao.save(statusChange);
        ObjectMapper mapper = new ObjectMapper();
        return ResponseEntity.ok("{\"changeId\":" + mapper.writeValueAsString(newStatusChange.getStatusChangeId()) + "}");
    }
}

