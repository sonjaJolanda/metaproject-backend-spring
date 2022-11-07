package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.SystemVariable;
import main.java.org.htwg.konstanz.metaproject.persistance.SystemVariableDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SystemVariablesEndpoint
 * <p>
 * This Endpoint is used for the implemented SystemVariables, where an administrator can add values with keys, which can be used by accessing the key.
 */

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/systemvariables")
public class SystemVariablesEndpoint {

    private static final Logger log = LoggerFactory.getLogger(SystemVariablesEndpoint.class);

    private final SystemVariableDAO systemVariableDAO;

    private final RightService rightService;

    public SystemVariablesEndpoint(SystemVariableDAO systemVariableDAO, RightService rightService) {
        this.systemVariableDAO = systemVariableDAO;
        this.rightService = rightService;
    }

    /**
     * Persists a new SystemVariable
     */
    @PutMapping(value = "")
    public ResponseEntity putCreateSecret(@RequestBody SystemVariable newSecret, @RequestHeader String token) {
        log.info("Request PUT /systemvariables");
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("New Variable '" + newSecret.getName() + "' is required: " + newSecret.getRequired());

        systemVariableDAO.save(newSecret);
        return ResponseEntity.ok(systemVariableDAO.findAll());
    }

    /**
     * Returns all persistent SystemVariables
     */
    @GetMapping("")
    public ResponseEntity<Object> getRetrieveAllVariables(@RequestHeader String token) {
        log.info("Request <-- GET /systemvariables");
        return ResponseEntity.ok(systemVariableDAO.findAll());
    }

    /**
     * Updates an existing SystemVariable.
     */
    @PostMapping(value = "")
    public ResponseEntity postUpdateSecret(@RequestBody SystemVariable transientSecret, @RequestHeader String token, @RequestHeader String method) {
        if (method.equals("DELETE")) {
            log.info("Removing key: " + transientSecret.getKey());
            systemVariableDAO.remove(transientSecret.getKey());
            return ResponseEntity.ok(systemVariableDAO.findAll());
        } else {
            return ResponseEntity.ok("{answer: 'not yet implemented'}");
        }
    }

    @PostMapping(value = "/delete")
    public ResponseEntity deleteRemoveSecret(@RequestBody SystemVariable element, @RequestHeader String token) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
