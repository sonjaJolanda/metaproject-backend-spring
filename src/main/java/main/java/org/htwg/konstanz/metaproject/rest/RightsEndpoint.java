package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.TokenInfo;
import main.java.org.htwg.konstanz.metaproject.persistance.RightDetailsDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.rights.RoleTypes;
import main.java.org.htwg.konstanz.metaproject.rights.UserRight;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Endpoint for rights.
 *
 * @author LuHansen, SiKelle
 * @version 2.0
 */

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/rights")
public class RightsEndpoint {

    private static final String RIGHT_MODEL_DETAILS = "details";

    private static final String RIGHT_MODEL_TYPES = "types";

    private static final Logger log = LoggerFactory.getLogger(RightsEndpoint.class);

    private final RightDetailsDAO rightDetailsDao;

    private final RightService rightService;

    private final TokenService tokenService;

    public RightsEndpoint(RightDetailsDAO rightDetailsDao, RightService rightService, TokenService tokenService) {
        this.rightDetailsDao = rightDetailsDao;
        this.rightService = rightService;
        this.tokenService = tokenService;
    }

    /**
     * Get all Rights and their structure.
     *
     * @return Collection<Right>
     */
    @GetMapping("/structure")
    public ResponseEntity getRightStructure(@RequestHeader String token) {
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST)
                .checkForAppRight(Rights.ROLES_CREATE).checkForAppRight(Rights.ROLES_EDIT).validate();
        if (!hasRights) {
            // User has no rights
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // User has rights, continue
        Map<Rights, Set<RoleTypes>> result = new HashMap<>();
        for (Rights right : Rights.values()) {
            result.put(right, right.getLinked());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Get details about a right by its name.
     */
    @GetMapping("/{right}")
    public ResponseEntity getRightDetails(@PathVariable Rights right, @RequestHeader String token) {
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST)
                .validate();
        if (!hasRights) {
            // User has no rights
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.badRequest().build();
        }
        // User has rights, continue
        Map<String, Object> result = new HashMap<>();
        // convert to an object and add type structures
        result.put(RIGHT_MODEL_TYPES, right.getLinked());
        // add detail description
        result.put(RIGHT_MODEL_DETAILS, rightDetailsDao.findById(right));
        return ResponseEntity.ok(result);
    }

    /**
     * List all rights with their descriptions.
     */
    @GetMapping("")
    public ResponseEntity getRightDetails(@RequestHeader String token) {
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.ROLES_LIST)
                .validate();
        if (!hasRights) {
            // User has no rights
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // User has rights, continue
        return ResponseEntity.ok(rightDetailsDao.findAll());
    }

    /**
     * Get all {@link UserRight} for a user by token. Every user can requests
     * its own rights.
     */
    @GetMapping(value = "/user")
    public ResponseEntity getUserRightsOfUser(@RequestHeader String token) {
        // Load user by token
        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (token == null || tokenInfo == null) {
            return ResponseEntity.badRequest().build(); // User isn't logged in or token is invalid
        }
        // return rights of user
        return ResponseEntity.ok(rightService.getAllUserRights(tokenInfo.getUserId()));
    }

    /**
     * Get all {@link UserRight} for a user by id. Every user can requests its
     * own rights, but there is a right check for list other user's rights.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity getUserRightsOfUser(@PathVariable Long id, @RequestHeader String token) {
        // If the user requests its own rights, the answer is given immediately.
        // This check is done first, because every user has to load its rights
        // very often and the access for rights of other users is only done by
        // administrator or super user.
        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo != null && tokenInfo.getUserId().equals(id)) {
            return ResponseEntity.ok(rightService.getAllUserRights(id));
        }
        // User does not requests its own rights, so check whether user has
        // permissions to list rights of other users.
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.ROLES_USER_LIST).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(rightService.getAllUserRights(id));
    }

}